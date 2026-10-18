package modelo.deportes;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import modelo.actividades.Estadistica;
import modelo.actividades.EstadisticaBaloncesto;
import modelo.actividades.EstadisticaFutbol;
import modelo.actividades.EstadisticaIndividual;
import modelo.actividades.EstadisticaVoleibol;
import modelo.actividades.Partido;
import modelo.personas.Socio;
import modelo.tipos.TipoEstadistica;

public class Deporte implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private boolean esDeConjunto;
    private boolean permiteEmpate;
    private int jugadoresEnCampo;
    private int minJugadoresReserva;
    private int maxJugadoresReserva;
    private int minJugadoresNomina;
    private int maxJugadoresNomina;
    private int duracionPartidoMin;
    private int diferenciaNivelPermitida;
    private List<String> posiciones;
    private TipoEstadistica tipoEstadistica;
    public Deporte(String nombre,boolean conjunto,int campo,int minReserva,int maxReserva,int minNomina,int maxNomina,int duracion,boolean empate,int diferencia,List<String> posiciones,TipoEstadistica tipo) throws ClubException
    {
        this.nombre=textoRequerido(nombre);
        esDeConjunto=conjunto;
        jugadoresEnCampo=enteroPositivo(campo);
        minJugadoresReserva=enteroPositivo(minReserva);
        maxJugadoresReserva=maxReserva;
        minJugadoresNomina=enteroPositivo(minNomina);
        maxJugadoresNomina=maxNomina;
        duracionPartidoMin=enteroPositivo(duracion);
        permiteEmpate=empate;
        diferenciaNivelPermitida=diferencia;
        this.posiciones=new ArrayList<>(posiciones);
        tipoEstadistica=Objects.requireNonNull(tipo);
        validar(maxReserva>=minReserva&&maxNomina>=minNomina&&diferencia>=0,"Configuración de deporte inválida");
    }
    public boolean cantidadReservaValida(int n)
    {
        return n>=minJugadoresReserva&&n<=maxJugadoresReserva;
    }
    public boolean nominaValida(int n)
    {
        return n>=minJugadoresNomina&&n<=maxJugadoresNomina;
    }
    public boolean esPosicionValida(String p)
    {
        return posiciones.contains(p);
    }
    public boolean nivelesCompatibles(int a,int b)
    {
        return Math.abs(a-b)<=diferenciaNivelPermitida;
    }
    public Estadistica crearEstadistica(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        Set<String> claves;
        switch(tipoEstadistica)
        {
            case FUTBOL:claves=new HashSet<String>(Arrays.asList("goles","asistencias"));
            break;
            case BALONCESTO:claves=new HashSet<String>(Arrays.asList("puntos","rebotes"));
            break;
            case VOLEIBOL:claves=new HashSet<String>(Arrays.asList("aces","bloqueos"));
            break;
            default:claves=new HashSet<String>(Arrays.asList("puntosAnotados","gano"));
        }
        validar(claves.containsAll(v.keySet()),"Atributo estadístico no definido para este deporte");
        if(v.containsKey("gano"))validar(v.get("gano")!=null&&(v.get("gano")==0||v.get("gano")==1),"gano debe ser 0 o 1");
        switch(tipoEstadistica)
        {
            case FUTBOL:return new EstadisticaFutbol(j,p,v);
            case BALONCESTO:return new EstadisticaBaloncesto(j,p,v);
            case VOLEIBOL:return new EstadisticaVoleibol(j,p,v);
            default:return new EstadisticaIndividual(j,p,v);
        }
    }
    public String getNombre()
    {
        return nombre;
    }
    public boolean isEsDeConjunto()
    {
        return esDeConjunto;
    }
    public int getJugadoresEnCampo()
    {
        return jugadoresEnCampo;
    }
    public int getDuracionPartidoMin()
    {
        return duracionPartidoMin;
    }
    public boolean isPermiteEmpate()
    {
        return permiteEmpate;
    }
    public TipoEstadistica getTipoEstadistica()
    {
        return tipoEstadistica;
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
}
