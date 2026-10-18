package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import modelo.actividades.Estadistica;
import modelo.actividades.Instalacion;
import modelo.actividades.Partido;
import modelo.actividades.PartidoPractica;
import modelo.actividades.SesionEntrenamiento;
import modelo.competencias.EntradaRanking;
import modelo.competencias.Ranking;
import modelo.deportes.Academia;
import modelo.deportes.Deporte;
import modelo.deportes.Equipo;
import modelo.deportes.InscripcionAcademia;
import modelo.deportes.InscripcionEquipo;
import modelo.personas.Entrenador;
import modelo.personas.Persona;
import modelo.personas.Socio;
import modelo.tipos.EstadoAsistencia;
import modelo.tipos.EstadoPartido;

/** Coordina las inscripciones y actividades deportivas del modelo. */
public class GestorDeportivo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Deporte> deportes=new ArrayList<>();
    private List<Equipo> equipos=new ArrayList<>();
    private List<Academia> academias=new ArrayList<>();
    private List<SesionEntrenamiento> sesiones=new ArrayList<>();
    private List<PartidoPractica> partidosPractica=new ArrayList<>();
    private Map<Deporte,Ranking> rankings=new LinkedHashMap<>();
    private GestorInstalaciones instalaciones;
    public GestorDeportivo(GestorInstalaciones i)
    {
        instalaciones=i;
    }
    public void registrarDeporte(Deporte d) throws ClubException
    {
        for (Deporte registrado : deportes)
        {
            validar(!registrado.getNombre().equalsIgnoreCase(d.getNombre()), "Deporte duplicado");
        }
        deportes.add(d);
        rankings.put(d, new Ranking(d));
    }
    public void crearEquipo(Equipo e) throws ClubException
    {
        for (Equipo registrado : equipos)
        {
            validar(!registrado.getNombre().equals(e.getNombre()), "Equipo duplicado");
        }
        equipos.add(e);
    }
    public void registrarAcademia(Academia a) throws ClubException
    {
        validar(!academias.contains(a),"Academia duplicada");
        academias.add(a);
    }
    public List<Deporte> getDeportes()
    {
        return copiaLectura(deportes);
    }
    public List<Equipo> getEquipos()
    {
        return copiaLectura(equipos);
    }
    public List<Academia> getAcademias()
    {
        return copiaLectura(academias);
    }
    public List<SesionEntrenamiento> getSesiones()
    {
        return copiaLectura(sesiones);
    }
    public List<PartidoPractica> getPartidosPractica()
    {
        return copiaLectura(partidosPractica);
    }
    public InscripcionEquipo inscribirEnEquipo(Socio s,Equipo e,String pos,int numero) throws ClubException
    {
        validar(equipos.contains(e)&&buscarEquipoDe(s,e.getDeporte())==null,"Ya pertenece a un equipo de este deporte");
        return e.inscribir(s,pos,numero,LocalDate.now());
    }
    private Equipo buscarEquipoDe(Socio s,Deporte d)
    {
        for (Equipo equipo : equipos)
        {
            if (equipo.getDeporte() == d && equipo.esIntegrante(s))
            {
                return equipo;
            }
        }
        return null;
    }
    public InscripcionAcademia inscribirEnAcademia(Socio s,Academia a) throws ClubException
    {
        validar(academias.contains(a),"Academia desconocida");
        return a.inscribir(s,LocalDate.now());
    }
    public SesionEntrenamiento programarEntrenamiento(Entrenador en,LocalDate f,LocalTime ini,int dur,Instalacion i,List<Socio> conv) throws ClubException
    {
        validar(instalaciones.getInstalaciones().contains(i) && en.puedeEntrenar(i.getDeporte()), "Instalación o entrenador no válido");
        validar(!conv.isEmpty() && i.admiteCantidad(conv.size()), "Cantidad de convocados inválida");
        List<Socio> revisados = new ArrayList<Socio>();
        for (Socio socio : conv)
        {
            validar(!revisados.contains(socio) && socio.isActivo() && !socio.estaSuspendido(i.getDeporte(), f), "Socio repetido o no habilitado");
            revisados.add(socio);
        }
        validar(dur > 0 && ini.toSecondOfDay() + dur * 60L < 86400, "Duración inválida");
        for (SesionEntrenamiento sesion : sesiones)
        {
            validar(!(sesion.getEntrenador() == en && sesion.bloqueaInstalacion() && sesion.seTraslapaCon(f, ini, ini.plusMinutes(dur))), "Entrenador ocupado");
        }
        SesionEntrenamiento sesion = new SesionEntrenamiento(UUID.randomUUID().toString(), f, ini, dur, i, en, conv);
        i.agregarOcupacion(sesion);
        sesiones.add(sesion);
        return sesion;
    }
    public void registrarAsistencia(Entrenador en,SesionEntrenamiento se,Socio s,EstadoAsistencia e,Integer nota,String com) throws ClubException
    {
        validar(sesiones.contains(se)&&se.getEntrenador()==en,"No es el entrenador responsable");
        se.registrarAsistencia(s,e,nota,com);
    }
    public PartidoPractica programarPartidoPractica(Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime ini) throws ClubException
    {
        validar(deportes.contains(d)&&!d.isEsDeConjunto(),"La práctica individual requiere deporte de raqueta");
        Instalacion i=instalaciones.buscarDisponible(d,f,ini,d.getDuracionPartidoMin(),2);
        PartidoPractica p=new PartidoPractica(UUID.randomUUID().toString(),sol,rival,d,f,ini,i);
        i.agregarOcupacion(p);
        partidosPractica.add(p);
        return p;
    }
    public Estadistica registrarEstadistica(Entrenador en,Partido p,Socio j,Map<String,Integer> v) throws ClubException
    {
        validar(en.puedeEntrenar(p.getDeporte())&&p.participo(j)&&p.getEstado()==EstadoPartido.FINALIZADO,"Entrenador o jugador no habilitado");
        Estadistica e=p.getDeporte().crearEstadistica(j,p,v);
        p.agregarEstadistica(e);
        Ranking ranking = rankings.get(p.getDeporte());
        if (ranking == null)
        {
            ranking = new Ranking(p.getDeporte());
            rankings.put(p.getDeporte(), ranking);
        }
        ranking.actualizar(e);
        return e;
    }
    public List<EntradaRanking> consultarRanking(Deporte d) throws ClubException
    {
        validar(rankings.containsKey(d),"Deporte desconocido");
        return rankings.get(d).getPosiciones();
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
