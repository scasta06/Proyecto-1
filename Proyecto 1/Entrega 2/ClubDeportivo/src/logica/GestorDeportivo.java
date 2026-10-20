package logica;

import excepciones.ClubException;
import excepciones.InscripcionException;
import excepciones.ParticipacionException;
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
import modelo.actividades.EstadisticaIndividual;
import modelo.actividades.Instalacion;
import modelo.actividades.Partido;
import modelo.actividades.PartidoInterno;
import modelo.actividades.PartidoOficial;
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
import modelo.tipos.TipoEstadistica;

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
    private List<PartidoInterno> partidosInternos = new ArrayList<>();
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
        if (!s.isActivo() || s.estaSuspendido(e.getDeporte(), LocalDate.now()))
            throw new ParticipacionException("Socio suspendido o inactivo");
        if (!equipos.contains(e) || !e.isActivo() || buscarEquipoDe(s,e.getDeporte()) != null)
            throw new InscripcionException("Equipo no disponible o ya pertenece a uno de este deporte");
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
        if (!sol.isActivo() || sol.estaSuspendido(d,f) || sol == rival)
            throw new ParticipacionException("Solicitante no habilitado");
        if (rival instanceof Socio)
        {
            Socio otro = (Socio) rival;
            if (!otro.isActivo() || otro.estaSuspendido(d,f) || !d.nivelesCompatibles(sol.getNivel(d),otro.getNivel(d)))
                throw new ParticipacionException("Rival suspendido o niveles incompatibles");
        }
        else if (!(rival instanceof Entrenador) || !((Entrenador)rival).puedeEntrenar(d))
            throw new ParticipacionException("Rival no habilitado");
        if (d.esPadel()) throw new ParticipacionException("Pádel requiere cuatro jugadores; use programarDobles");
        Instalacion i=instalaciones.buscarDisponible(d,f,ini,d.getDuracionPartidoMin(),2);
        PartidoPractica p=new PartidoPractica(UUID.randomUUID().toString(),sol,rival,d,f,ini,i);
        i.agregarOcupacion(p);
        partidosPractica.add(p);
        return p;
    }
    public Estadistica registrarEstadistica(Entrenador en,Partido p,Socio j,Map<String,Integer> v) throws ClubException
    {
        validar(en.puedeEntrenar(p.getDeporte())&&p.participo(j)&&p.getEstado()==EstadoPartido.FINALIZADO,"Entrenador o jugador no habilitado");
        Map<String,Integer> valores = new LinkedHashMap<>(v);
        if (p instanceof PartidoPractica)
        {
            int gano = ((PartidoPractica)p).gano(j) ? 1 : 0;
            validar(!valores.containsKey("gano") || valores.get("gano") != null && valores.get("gano") == gano,"La estadística contradice el resultado");
            valores.put("gano",gano);
        }
        Estadistica e=p.getDeporte().crearEstadistica(j,p,valores);
        if (e instanceof EstadisticaIndividual && p instanceof PartidoPractica)
            ((EstadisticaIndividual)e).setCambioRanking(((PartidoPractica)p).cambioRanking(j,rankings.get(p.getDeporte())));
        p.agregarEstadistica(e);
        if (p.getDeporte().getTipoEstadistica() == TipoEstadistica.FUTBOL)
        {
            Integer amarillas = v.get("amarillas"), rojas = v.get("rojas");
            p.registrarTarjetas(j, amarillas == null ? 0 : amarillas, rojas == null ? 0 : rojas);
        }
        Ranking ranking = rankings.get(p.getDeporte());
        if (ranking == null)
        {
            ranking = new Ranking(p.getDeporte());
            rankings.put(p.getDeporte(), ranking);
        }
        ranking.actualizar(e);
        if (p instanceof PartidoOficial)
            ((PartidoOficial)p).getCompetencia().getRanking().actualizar(e);
        return e;
    }
    public List<EntradaRanking> consultarRanking(Deporte d) throws ClubException
    {
        validar(rankings.containsKey(d),"Deporte desconocido");
        return rankings.get(d).getPosiciones();
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ParticipacionException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
    public void darDeBajaEquipo(Equipo equipo) throws ClubException
    {
        validar(equipos.contains(equipo), "Equipo desconocido");
        equipo.darDeBaja();
    }

    public PartidoInterno programarPartidoInterno(Equipo local, Equipo visitante, LocalDate fecha, LocalTime hora) throws ClubException
    {
        validar(equipos.contains(local) && equipos.contains(visitante), "Equipo fuera del club");
        Instalacion instalacion = instalaciones.buscarDisponible(local.getDeporte(),fecha,hora,local.getDeporte().getDuracionPartidoMin(),local.getDeporte().getJugadoresEnCampo()*2);
        PartidoInterno partido = new PartidoInterno(UUID.randomUUID().toString(),local,visitante,fecha,hora,instalacion);
        instalacion.agregarOcupacion(partido);
        if (partidosInternos == null) partidosInternos = new ArrayList<>();
        partidosInternos.add(partido);
        return partido;
    }
    public List<PartidoInterno> getPartidosInternos()
    {
        if (partidosInternos == null) partidosInternos = new ArrayList<>();
        return new ArrayList<>(partidosInternos);
    }

    public PartidoPractica programarDobles(List<Socio> propios, List<Socio> rivales, Deporte deporte, LocalDate fecha, LocalTime hora) throws ClubException
    {
        validar(deportes.contains(deporte) && !deporte.isEsDeConjunto(),"Deporte individual desconocido");
        Instalacion instalacion = instalaciones.buscarDisponible(deporte,fecha,hora,deporte.getDuracionPartidoMin(),4);
        PartidoPractica partido = new PartidoPractica(UUID.randomUUID().toString(),propios,rivales,deporte,fecha,hora,instalacion);
        instalacion.agregarOcupacion(partido);
        partidosPractica.add(partido);
        return partido;
    }

}
