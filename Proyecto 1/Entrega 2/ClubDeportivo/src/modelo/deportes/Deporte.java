package modelo.deportes;
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
public class Deporte implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private boolean esDeConjunto,permiteEmpate;
    private int jugadoresEnCampo,minJugadoresReserva,maxJugadoresReserva,minJugadoresNomina,maxJugadoresNomina,duracionPartidoMin,diferenciaNivelPermitida;
    private List<String> posiciones;
    private TipoEstadistica tipoEstadistica;
    public Deporte(String nombre,boolean conjunto,int campo,int minReserva,int maxReserva,int minNomina,int maxNomina,int duracion,boolean empate,int diferencia,List<String> posiciones,TipoEstadistica tipo) {
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
    public boolean cantidadReservaValida(int n) {
        return n>=minJugadoresReserva&&n<=maxJugadoresReserva;
    }
    public boolean nominaValida(int n) {
        return n>=minJugadoresNomina&&n<=maxJugadoresNomina;
    }
    public boolean esPosicionValida(String p) {
        return posiciones.contains(p);
    }
    public boolean nivelesCompatibles(int a,int b) {
        return Math.abs(a-b)<=diferenciaNivelPermitida;
    }
    public Estadistica crearEstadistica(Socio j,Partido p,Map<String,Integer> v) {
        Set<String> claves;
        switch(tipoEstadistica) {
            case FUTBOL:claves=Set.of("goles","asistencias");
            break;
            case BALONCESTO:claves=Set.of("puntos","rebotes");
            break;
            case VOLEIBOL:claves=Set.of("aces","bloqueos");
            break;
            default:claves=Set.of("puntosAnotados","gano");
        }
        validar(claves.containsAll(v.keySet()),"Atributo estadístico no definido para este deporte");
        if(v.containsKey("gano"))validar(v.get("gano")!=null&&(v.get("gano")==0||v.get("gano")==1),"gano debe ser 0 o 1");
        switch(tipoEstadistica) {
            case FUTBOL:return new EstadisticaFutbol(j,p,v);
            case BALONCESTO:return new EstadisticaBaloncesto(j,p,v);
            case VOLEIBOL:return new EstadisticaVoleibol(j,p,v);
            default:return new EstadisticaIndividual(j,p,v);
        }
    }
    public String getNombre() {
        return nombre;
    }
    public boolean isEsDeConjunto() {
        return esDeConjunto;
    }
    public int getJugadoresEnCampo() {
        return jugadoresEnCampo;
    }
    public int getDuracionPartidoMin() {
        return duracionPartidoMin;
    }
    public boolean isPermiteEmpate() {
        return permiteEmpate;
    }
    public TipoEstadistica getTipoEstadistica() {
        return tipoEstadistica;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static int enteroPositivo(int valor) {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
}
