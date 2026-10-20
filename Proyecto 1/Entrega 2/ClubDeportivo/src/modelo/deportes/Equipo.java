package modelo.deportes;

import excepciones.ClubException;
import excepciones.InscripcionException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.personas.Entrenador;
import modelo.personas.Socio;
import modelo.tipos.Categoria;

public class Equipo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private Map<String,LocalDateTime> ultimosPartidos = new HashMap<>();
    private boolean dadoDeBaja;
    private Map<String, Map<Socio,Integer>> amarillas = new HashMap<>();
    private Map<String, List<Socio>> suspendidos = new HashMap<>();
    private Categoria categoria;
    private int cupoMaximo;
    private Deporte deporte;
    private Entrenador entrenador;
    private List<InscripcionEquipo> inscripciones=new ArrayList<>();
    public Equipo(String n,Categoria c,int cupo,Deporte d,Entrenador en) throws ClubException
    {
        validar(d.isEsDeConjunto()&&en.puedeEntrenar(d),"Deporte o entrenador no válido");
        nombre=textoRequerido(n);
        categoria=c;
        cupoMaximo=enteroPositivo(cupo);
        deporte=d;
        entrenador=en;
    }
    public boolean tieneCupo()
    {
        return !dadoDeBaja && getJugadoresActivos().size()<cupoMaximo;
    }
    public boolean esIntegrante(Socio s)
    {
        return getJugadoresActivos().contains(s);
    }
    public InscripcionEquipo inscribir(Socio s,String pos,int num,LocalDate f) throws ClubException
    {
        if (!s.isActivo() || s.estaSuspendido(deporte,f) || !tieneCupo() || esIntegrante(s) || Categoria.paraEdad(s.getEdad(f)) != categoria || !deporte.esPosicionValida(pos) || num < 0 || num > 99)
        {
            throw new InscripcionException("Inscripción de equipo inválida");
        }
        for (InscripcionEquipo anterior : inscripciones)
        {
            if (anterior.isActiva() && anterior.getNumeroCamiseta() == num)
            {
                throw new InscripcionException("Número de camiseta ocupado");
            }
        }
        InscripcionEquipo inscripcion = new InscripcionEquipo(s, this, pos, num, f);
        inscripciones.add(inscripcion);
        return inscripcion;
    }
    public List<Socio> getJugadoresActivos()
    {
        List<Socio> jugadores = new ArrayList<Socio>();
        for (InscripcionEquipo inscripcion : inscripciones)
        {
            if (inscripcion.isActiva() && inscripcion.getJugador().isActivo())
            {
                jugadores.add(inscripcion.getJugador());
            }
        }
        return jugadores;
    }
    public Deporte getDeporte()
    {
        return deporte;
    }
    public Categoria getCategoria()
    {
        return categoria;
    }
    public String getNombre()
    {
        return nombre;
    }
    public Entrenador getEntrenador()
    {
        return entrenador;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    public boolean isActivo() { return !dadoDeBaja; }
    public void darDeBaja()
    {
        dadoDeBaja = true;
        for (InscripcionEquipo inscripcion : inscripciones) inscripcion.retirar();
    }

    public void validarOrdenPartido(String circuito, LocalDateTime fecha) throws ClubException
    {
        if (ultimosPartidos != null && ultimosPartidos.containsKey(circuito) && !fecha.isAfter(ultimosPartidos.get(circuito)))
            throw new ParticipacionException("Ya se registró un partido posterior de este equipo");
    }
    public void registrarPartidoDisputado(String circuito, LocalDateTime fecha)
    {
        if (ultimosPartidos == null) ultimosPartidos = new HashMap<>();
        ultimosPartidos.put(circuito,fecha);
    }
    public void validarFechaEstadisticas(String circuito, LocalDateTime fecha) throws ClubException
    {
        if (ultimosPartidos != null && ultimosPartidos.containsKey(circuito) && fecha.isBefore(ultimosPartidos.get(circuito)))
            throw new ParticipacionException("Registre las estadísticas antes de disputar el siguiente partido del equipo");
    }
    private void prepararDisciplina()
    {
        if (amarillas == null) amarillas = new HashMap<>();
        if (suspendidos == null) suspendidos = new HashMap<>();
    }
    public boolean suspendidoPorTarjetas(Socio socio, String circuito)
    {
        prepararDisciplina();
        return suspendidos.containsKey(circuito) && suspendidos.get(circuito).contains(socio);
    }
    public void registrarTarjetas(Socio socio, String circuito, int amarillasPartido, int rojas, int limite) throws ClubException
    {
        prepararDisciplina();
        if (amarillasPartido < 0 || amarillasPartido > 2 || rojas < 0 || rojas > 1 || limite < 1)
            throw new ParticipacionException("Tarjetas inválidas");
        if (!amarillas.containsKey(circuito)) amarillas.put(circuito,new HashMap<Socio,Integer>());
        Map<Socio,Integer> acumuladas = amarillas.get(circuito);
        Integer anteriores = acumuladas.get(socio);
        int total = (anteriores == null ? 0 : anteriores) + amarillasPartido;
        if (total >= limite || amarillasPartido == 2 || rojas == 1)
        {
            if (!suspendidos.containsKey(circuito)) suspendidos.put(circuito,new ArrayList<Socio>());
            if (!suspendidos.get(circuito).contains(socio)) suspendidos.get(circuito).add(socio);
            total = 0;
        }
        acumuladas.put(socio,total);
    }
    public void cumplirSuspensiones(String circuito)
    {
        prepararDisciplina();
        if (suspendidos.containsKey(circuito)) suspendidos.get(circuito).clear();
    }

}
