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
public class GestorDeportivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Deporte> deportes=new ArrayList<>();
    private List<Equipo> equipos=new ArrayList<>();
    private List<Academia> academias=new ArrayList<>();
    private List<SesionEntrenamiento> sesiones=new ArrayList<>();
    private List<PartidoPractica> partidosPractica=new ArrayList<>();
    private Map<Deporte,Ranking> rankings=new LinkedHashMap<>();
    private GestorInstalaciones instalaciones;
    public GestorDeportivo(GestorInstalaciones i) {
        instalaciones=i;
    }
    public void registrarDeporte(Deporte d) {
        validar(deportes.stream().noneMatch(x->x.getNombre().equalsIgnoreCase(d.getNombre())),"Deporte duplicado");
        deportes.add(d);
        rankings.put(d,new Ranking(d));
    }
    public void crearEquipo(Equipo e) {
        validar(equipos.stream().noneMatch(x->x.getNombre().equals(e.getNombre())),"Equipo duplicado");
        equipos.add(e);
    }
    public void registrarAcademia(Academia a) {
        validar(!academias.contains(a),"Academia duplicada");
        academias.add(a);
    }
    public List<Deporte> getDeportes() {
        return copiaLectura(deportes);
    }
    public List<Equipo> getEquipos() {
        return copiaLectura(equipos);
    }
    public List<Academia> getAcademias() {
        return copiaLectura(academias);
    }
    public List<SesionEntrenamiento> getSesiones() {
        return copiaLectura(sesiones);
    }
    public List<PartidoPractica> getPartidosPractica() {
        return copiaLectura(partidosPractica);
    }
    public InscripcionEquipo inscribirEnEquipo(Socio s,Equipo e,String pos,int numero) {
        validar(equipos.contains(e)&&buscarEquipoDe(s,e.getDeporte())==null,"Ya pertenece a un equipo de este deporte");
        return e.inscribir(s,pos,numero,LocalDate.now());
    }
    private Equipo buscarEquipoDe(Socio s,Deporte d) {
        return equipos.stream().filter(e->e.getDeporte()==d&&e.esIntegrante(s)).findFirst().orElse(null);
    }
    public InscripcionAcademia inscribirEnAcademia(Socio s,Academia a) {
        validar(academias.contains(a),"Academia desconocida");
        return a.inscribir(s,LocalDate.now());
    }
    public SesionEntrenamiento programarEntrenamiento(Entrenador en,LocalDate f,LocalTime ini,int dur,Instalacion i,List<Socio> conv) {
        validar(instalaciones.getInstalaciones().contains(i)&&en.puedeEntrenar(i.getDeporte())&&!conv.isEmpty()&&new HashSet<>(conv).size()==conv.size()&&i.admiteCantidad(conv.size())&&conv.stream().allMatch(s->s.isActivo()&&!s.estaSuspendido(i.getDeporte(),f))&&sesiones.stream().noneMatch(s->s.getEntrenador()==en&&s.bloqueaInstalacion()&&s.seTraslapaCon(f,ini,ini.plusMinutes(dur))),"Entrenador, convocatoria o agenda inválidos");
        SesionEntrenamiento s=new SesionEntrenamiento(UUID.randomUUID().toString(),f,ini,dur,i,en,conv);
        i.agregarOcupacion(s);
        sesiones.add(s);
        return s;
    }
    public void registrarAsistencia(Entrenador en,SesionEntrenamiento se,Socio s,EstadoAsistencia e,Integer nota,String com) {
        validar(sesiones.contains(se)&&se.getEntrenador()==en,"No es el entrenador responsable");
        se.registrarAsistencia(s,e,nota,com);
    }
    public PartidoPractica programarPartidoPractica(Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime ini) {
        validar(deportes.contains(d)&&!d.isEsDeConjunto(),"La práctica individual requiere deporte de raqueta");
        Instalacion i=instalaciones.buscarDisponible(d,f,ini,d.getDuracionPartidoMin(),2);
        PartidoPractica p=new PartidoPractica(UUID.randomUUID().toString(),sol,rival,d,f,ini,i);
        i.agregarOcupacion(p);
        partidosPractica.add(p);
        return p;
    }
    public Estadistica registrarEstadistica(Entrenador en,Partido p,Socio j,Map<String,Integer> v) {
        validar(en.puedeEntrenar(p.getDeporte())&&p.participo(j)&&p.getEstado()==EstadoPartido.FINALIZADO,"Entrenador o jugador no habilitado");
        Estadistica e=p.getDeporte().crearEstadistica(j,p,v);
        p.agregarEstadistica(e);
        rankings.computeIfAbsent(p.getDeporte(),Ranking::new).actualizar(e);
        return e;
    }
    public List<EntradaRanking> consultarRanking(Deporte d) {
        validar(rankings.containsKey(d),"Deporte desconocido");
        return rankings.get(d).getPosiciones();
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
