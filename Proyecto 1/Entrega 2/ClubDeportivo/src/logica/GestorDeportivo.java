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
    private List<PartidoInterno> partidosInternos=new ArrayList<>();
    private Map<String,Map<Socio,Integer>> amarillas=new LinkedHashMap<>();
    private Map<String,Set<Socio>> suspendidos=new LinkedHashMap<>();
    private Map<Partido,Set<Socio>> cumplen=new LinkedHashMap<>();
    private String contexto(Partido p,Socio s) {
        if(p instanceof PartidoOficial)return "OFICIAL:"+((PartidoOficial)p).getCompetencia().getCodigo();
        return "INTERNO:"+buscarEquipoDe(s,p.getDeporte()).getNombre();
    }
    public boolean habilitadoOficial(Competencia c,Socio s) {
        return !suspendidos.getOrDefault("OFICIAL:"+c.getCodigo(),Set.of()).contains(s);
    }
    public void prepararSanciones(Partido p,List<Socio> nomina) {
        Set<Socio> ausentes=new HashSet<>();
        for(Socio s:nomina)if(suspendidos.getOrDefault(contexto(p,s),Set.of()).contains(s))ausentes.add(s);
        cumplen.put(p,ausentes);
    }
    public void cumplirSanciones(Partido p) {
        for(Socio s:cumplen.getOrDefault(p,Set.of()))suspendidos.get(contexto(p,s)).remove(s);
        cumplen.remove(p);
    }
    public PartidoInterno programarPartidoInterno(Equipo a,Equipo b,LocalDate f,LocalTime h) {
        Validacion.exigir(equipos.contains(a)&&equipos.contains(b),"Equipo desconocido");
        List<Socio> nomina=new ArrayList<>(a.getJugadoresActivos());
        nomina.addAll(b.getJugadoresActivos());
        List<Socio> aptos=nomina.stream().filter(s->!s.estaSuspendido(a.getDeporte(),f)&&!suspendidos.getOrDefault("INTERNO:"+buscarEquipoDe(s,a.getDeporte()).getNombre(),Set.of()).contains(s)).collect(Collectors.toList());
        Validacion.exigir(aptos.stream().filter(a::esIntegrante).count()>=a.getDeporte().getJugadoresEnCampo()&&aptos.stream().filter(b::esIntegrante).count()>=b.getDeporte().getJugadoresEnCampo(),"Equipo sin mínimo habilitado");
        Instalacion i=instalaciones.buscarDisponible(a.getDeporte(),f,h,a.getDeporte().getDuracionPartidoMin(),aptos.size());
        PartidoInterno p=new PartidoInterno(UUID.randomUUID().toString(),a,b,f,h,i);
        aptos.forEach(p::agregarParticipante);
        i.agregarOcupacion(p);
        prepararSanciones(p,nomina);
        partidosInternos.add(p);
        return p;
    }
    public void registrarResultadoInterno(PartidoInterno p,int a,int b) {
        Validacion.exigir(partidosInternos.contains(p),"Partido desconocido");
        p.registrarResultado(a,b);
        cumplirSanciones(p);
    }
    public List<PartidoInterno> getPartidosInternos() {
        return Validacion.copia(partidosInternos);
    }
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
        Validacion.exigir(deportes.stream().noneMatch(x->x.getNombre().equalsIgnoreCase(d.getNombre())),"Deporte duplicado");
        deportes.add(d);
        rankings.put(d,new Ranking(d));
    }
    public void crearEquipo(Equipo e) {
        Validacion.exigir(equipos.stream().noneMatch(x->x.getNombre().equals(e.getNombre())),"Equipo duplicado");
        equipos.add(e);
    }
    public void registrarAcademia(Academia a) {
        Validacion.exigir(!academias.contains(a),"Academia duplicada");
        academias.add(a);
    }
    public List<Deporte> getDeportes() {
        return Validacion.copia(deportes);
    }
    public List<Equipo> getEquipos() {
        return Validacion.copia(equipos);
    }
    public List<Academia> getAcademias() {
        return Validacion.copia(academias);
    }
    public List<SesionEntrenamiento> getSesiones() {
        return Validacion.copia(sesiones);
    }
    public List<PartidoPractica> getPartidosPractica() {
        return Validacion.copia(partidosPractica);
    }
    public InscripcionEquipo inscribirEnEquipo(Socio s,Equipo e,String pos,int numero) {
        Validacion.exigir(equipos.contains(e)&&buscarEquipoDe(s,e.getDeporte())==null,"Ya pertenece a un equipo de este deporte");
        return e.inscribir(s,pos,numero,LocalDate.now());
    }
    private Equipo buscarEquipoDe(Socio s,Deporte d) {
        return equipos.stream().filter(e->e.getDeporte()==d&&e.esIntegrante(s)).findFirst().orElse(null);
    }
    public InscripcionAcademia inscribirEnAcademia(Socio s,Academia a) {
        Validacion.exigir(academias.contains(a),"Academia desconocida");
        return a.inscribir(s,LocalDate.now());
    }
    public SesionEntrenamiento programarEntrenamiento(Entrenador en,LocalDate f,LocalTime ini,int dur,Instalacion i,List<Socio> conv) {
        Validacion.exigir(instalaciones.getInstalaciones().contains(i)&&en.puedeEntrenar(i.getDeporte())&&!conv.isEmpty()&&new HashSet<>(conv).size()==conv.size()&&i.admiteCantidad(conv.size())&&conv.stream().allMatch(s->s.isActivo()&&!s.estaSuspendido(i.getDeporte(),f))&&sesiones.stream().noneMatch(s->s.getEntrenador()==en&&s.bloqueaInstalacion()&&s.seTraslapaCon(f,ini,ini.plusMinutes(dur))),"Entrenador, convocatoria o agenda inválidos");
        SesionEntrenamiento s=new SesionEntrenamiento(UUID.randomUUID().toString(),f,ini,dur,i,en,conv);
        i.agregarOcupacion(s);
        sesiones.add(s);
        return s;
    }
    public void registrarAsistencia(Entrenador en,SesionEntrenamiento se,Socio s,EstadoAsistencia e,Integer nota,String com) {
        Validacion.exigir(sesiones.contains(se)&&se.getEntrenador()==en,"No es el entrenador responsable");
        se.registrarAsistencia(s,e,nota,com);
    }
    public PartidoPractica programarPartidoPractica(Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime ini) {
        Validacion.exigir(deportes.contains(d)&&!d.isEsDeConjunto(),"La práctica individual requiere deporte de raqueta");
        Instalacion i=instalaciones.buscarDisponible(d,f,ini,d.getDuracionPartidoMin(),2);
        PartidoPractica p=new PartidoPractica(UUID.randomUUID().toString(),sol,rival,d,f,ini,i);
        i.agregarOcupacion(p);
        partidosPractica.add(p);
        return p;
    }
    public Estadistica registrarEstadistica(Entrenador en,Partido p,Socio j,Map<String,Integer> v) {
        Validacion.exigir(en.puedeEntrenar(p.getDeporte())&&p.participo(j)&&p.getEstado()==EstadoPartido.FINALIZADO,"Entrenador o jugador no habilitado");
        Estadistica e=p.getDeporte().crearEstadistica(j,p,v);
        if(p.getDeporte().getTipoEstadistica()==TipoEstadistica.FUTBOL) {
            Validacion.exigir(v.getOrDefault("amarillas",0)<=2&&v.getOrDefault("rojas",0)<=1,"Tarjetas inválidas");
        }
        p.agregarEstadistica(e);
        if(p.getDeporte().getTipoEstadistica()==TipoEstadistica.FUTBOL) {
            String ctx=contexto(p,j);
            Map<Socio,Integer> cuenta=amarillas.computeIfAbsent(ctx,k->new LinkedHashMap<>());
            int acum=cuenta.getOrDefault(j,0)+v.getOrDefault("amarillas",0);
            if(acum>=2||v.getOrDefault("rojas",0)>0) {
                suspendidos.computeIfAbsent(ctx,k->new HashSet<>()).add(j);
                cuenta.put(j,0);
            }
            else cuenta.put(j,acum);
        }
        rankings.computeIfAbsent(p.getDeporte(),Ranking::new).actualizar(e);
        return e;
    }
    public List<EntradaRanking> consultarRanking(Deporte d) {
        Validacion.exigir(rankings.containsKey(d),"Deporte desconocido");
        return rankings.get(d).getPosiciones();
    }
}
