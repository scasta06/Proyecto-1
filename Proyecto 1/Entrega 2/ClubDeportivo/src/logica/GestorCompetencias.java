package logica;

import excepciones.ClubException;
import excepciones.InscripcionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import modelo.actividades.Instalacion;
import modelo.actividades.Partido;
import modelo.actividades.PartidoOficial;
import modelo.competencias.Competencia;
import modelo.competencias.InscripcionCompetencia;
import modelo.deportes.Equipo;
import modelo.personas.Socio;
import modelo.tipos.Categoria;
import modelo.tipos.EstadoCompetencia;
import modelo.tipos.Resultado;

/** Coordina nominas, partidos oficiales y resultados de competencias. */
public class GestorCompetencias implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Competencia> competencias=new ArrayList<>();
    private GestorInstalaciones instalaciones;
    public GestorCompetencias(GestorInstalaciones i)
    {
        instalaciones=i;
    }
    public void registrarCompetencia(Competencia c) throws ClubException
    {
        for (Competencia registrada : competencias)
        {
            validar(!registrada.getCodigo().equals(c.getCodigo()), "Competencia duplicada");
        }
        competencias.add(c);
    }
    public List<Competencia> getCompetencias()
    {
        return copiaLectura(competencias);
    }
    private void abierta(Competencia c) throws ClubException
    {
        validar(competencias.contains(c)&&c.inscripcionesAbiertas(LocalDate.now()),"Inscripciones cerradas");
    }
    public InscripcionCompetencia inscribirEquipo(Competencia c,Equipo e,List<Socio> nomina) throws ClubException
    {
        abierta(c);
        if (e.getDeporte() != c.getDeporte() || e.getCategoria() != c.getCategoria() || !c.getDeporte().nominaValida(nomina.size()))
        {
            throw new InscripcionException("Equipo o tamaño de nómina no válido");
        }
        List<Socio> revisados = new ArrayList<Socio>();
        for (Socio socio : nomina)
        {
            if (revisados.contains(socio) || !e.esIntegrante(socio) || socio.estaSuspendido(c.getDeporte(), c.getFechaInicio()))
            {
                throw new InscripcionException("Nómina no válida");
            }
            revisados.add(socio);
        }
        for (InscripcionCompetencia anterior : c.getInscripciones())
        {
            validar(anterior.getEquipo() != e, "Equipo ya inscrito en la competencia");
        }
        InscripcionCompetencia inscripcion = new InscripcionCompetencia(c, e, null, nomina);
        c.agregarInscripcion(inscripcion);
        return inscripcion;
    }
    public InscripcionCompetencia inscribirIndividual(Competencia c,Socio s) throws ClubException
    {
        abierta(c);
        validar(!c.getDeporte().isEsDeConjunto() && s.isActivo() && !s.estaSuspendido(c.getDeporte(), c.getFechaInicio()) && Categoria.paraEdad(s.getEdad(c.getFechaInicio())) == c.getCategoria(), "Inscripción individual inválida");
        for (InscripcionCompetencia anterior : c.getInscripciones())
        {
            validar(anterior.getJugador() != s, "Socio ya inscrito en la competencia");
        }
        s.getNivel(c.getDeporte());
        List<Socio> nomina = new ArrayList<Socio>();
        nomina.add(s);
        InscripcionCompetencia inscripcion = new InscripcionCompetencia(c, null, s, nomina);
        c.agregarInscripcion(inscripcion);
        return inscripcion;
    }
    public PartidoOficial programarPartido(Competencia c,InscripcionCompetencia i,String rival,LocalDate f,LocalTime ini,boolean local) throws ClubException
    {
        validar(competencias.contains(c)&&c.getInscripciones().contains(i)&&!i.isEliminada()&&c.getEstado()!=EstadoCompetencia.FINALIZADA&&!f.isBefore(c.getFechaInicio())&&!f.isAfter(c.getFechaFin()),"Partido fuera de competencia");
        List<Socio> aptos = new ArrayList<Socio>();
        for (Socio socio : i.getNomina())
        {
            if (socio.isActivo() && !socio.estaSuspendido(c.getDeporte(), f))
            {
                aptos.add(socio);
            }
        }
        int min=c.getDeporte().isEsDeConjunto()?c.getDeporte().getJugadoresEnCampo():1;
        validar(aptos.size()>=min,"Nómina habilitada insuficiente");
        Instalacion inst=local?instalaciones.buscarDisponible(c.getDeporte(),f,ini,c.getDeporte().getDuracionPartidoMin(),min):null;
        PartidoOficial p=new PartidoOficial(UUID.randomUUID().toString(),c,i,rival,f,ini,local,inst);
        for (Socio socio : aptos)
        {
            p.agregarParticipante(socio);
        }
        if(inst!=null)inst.agregarOcupacion(p);
        c.agregarPartido(p);
        return p;
    }
    public void registrarResultado(PartidoOficial p,int propio,int rival) throws ClubException
    {
        validar(competencias.contains(p.getCompetencia())&&p.getCompetencia().getPartidos().contains(p),"Partido desconocido");
        boolean eliminacion=p.getFase().equals("ELIMINACION_DIRECTA");
        validar(!eliminacion||propio!=rival,"Eliminatoria sin ganador");
        p.registrarResultado(propio,rival);
        p.getInscripcion().sumarPuntos(p.getPuntosObtenidos());
        if(eliminacion&&p.getResultado()==Resultado.DERROTA)p.getInscripcion().eliminar();
    }
    public List<InscripcionCompetencia> consultarTabla(Competencia c)
    {
        return c.getTablaPosiciones();
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
