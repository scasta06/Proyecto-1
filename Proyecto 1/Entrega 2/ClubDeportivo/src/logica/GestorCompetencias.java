package logica;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import modelo.personas.*;
import modelo.deportes.*;
import modelo.actividades.*;
import modelo.competencias.*;
import modelo.personal.*;
import modelo.inventario.*;
import modelo.comercio.*;
import modelo.tipos.*;
import logica.*;
import excepciones.*;
public class GestorCompetencias implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Competencia> competencias=new ArrayList<>();
    private GestorInstalaciones instalaciones;
    public GestorCompetencias(GestorInstalaciones i) {
        instalaciones=i;
    }
    public void registrarCompetencia(Competencia c) {
        validar(competencias.stream().noneMatch(x->x.getCodigo().equals(c.getCodigo())),"Competencia duplicada");
        competencias.add(c);
    }
    public List<Competencia> getCompetencias() {
        return copiaLectura(competencias);
    }
    private void abierta(Competencia c) {
        validar(competencias.contains(c)&&c.inscripcionesAbiertas(LocalDate.now()),"Inscripciones cerradas");
    }
    public InscripcionCompetencia inscribirEquipo(Competencia c,Equipo e,List<Socio> nomina) {
        abierta(c);
        if(e.getDeporte()!=c.getDeporte()||e.getCategoria()!=c.getCategoria()||!c.getDeporte().nominaValida(nomina.size())||new HashSet<>(nomina).size()!=nomina.size()||nomina.stream().anyMatch(s->!e.esIntegrante(s)||s.estaSuspendido(c.getDeporte(),c.getFechaInicio()))||c.getInscripciones().stream().anyMatch(i->i.getEquipo()==e))throw new InscripcionException("Nómina no válida");
        InscripcionCompetencia i=new InscripcionCompetencia(c,e,null,nomina);
        c.agregarInscripcion(i);
        return i;
    }
    public InscripcionCompetencia inscribirIndividual(Competencia c,Socio s) {
        abierta(c);
        validar(!c.getDeporte().isEsDeConjunto()&&s.isActivo()&&!s.estaSuspendido(c.getDeporte(),c.getFechaInicio())&&Categoria.paraEdad(s.getEdad(c.getFechaInicio()))==c.getCategoria()&&c.getInscripciones().stream().noneMatch(i->i.getJugador()==s),"Inscripción individual inválida");
        s.getNivel(c.getDeporte());
        InscripcionCompetencia i=new InscripcionCompetencia(c,null,s,List.of(s));
        c.agregarInscripcion(i);
        return i;
    }
    public PartidoOficial programarPartido(Competencia c,InscripcionCompetencia i,String rival,LocalDate f,LocalTime ini,boolean local) {
        validar(competencias.contains(c)&&c.getInscripciones().contains(i)&&!i.isEliminada()&&c.getEstado()!=EstadoCompetencia.FINALIZADA&&!f.isBefore(c.getFechaInicio())&&!f.isAfter(c.getFechaFin()),"Partido fuera de competencia");
        List<Socio> aptos=i.getNomina().stream().filter(s->s.isActivo()&&!s.estaSuspendido(c.getDeporte(),f)).collect(Collectors.toList());
        int min=c.getDeporte().isEsDeConjunto()?c.getDeporte().getJugadoresEnCampo():1;
        validar(aptos.size()>=min,"Nómina habilitada insuficiente");
        Instalacion inst=local?instalaciones.buscarDisponible(c.getDeporte(),f,ini,c.getDeporte().getDuracionPartidoMin(),min):null;
        PartidoOficial p=new PartidoOficial(UUID.randomUUID().toString(),c,i,rival,f,ini,local,inst);
        aptos.forEach(p::agregarParticipante);
        if(inst!=null)inst.agregarOcupacion(p);
        c.agregarPartido(p);
        return p;
    }
    public void registrarResultado(PartidoOficial p,int propio,int rival) {
        validar(competencias.contains(p.getCompetencia())&&p.getCompetencia().getPartidos().contains(p),"Partido desconocido");
        boolean eliminacion=p.getFase().equals("ELIMINACION_DIRECTA");
        validar(!eliminacion||propio!=rival,"Eliminatoria sin ganador");
        p.registrarResultado(propio,rival);
        p.getInscripcion().sumarPuntos(p.getPuntosObtenidos());
        if(eliminacion&&p.getResultado()==Resultado.DERROTA)p.getInscripcion().eliminar();
    }
    public List<InscripcionCompetencia> consultarTabla(Competencia c) {
        return c.getTablaPosiciones();
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
