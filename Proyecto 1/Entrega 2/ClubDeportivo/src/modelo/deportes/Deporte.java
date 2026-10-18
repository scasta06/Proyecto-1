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
        this.nombre=Validacion.texto(nombre);
        esDeConjunto=conjunto;
        jugadoresEnCampo=Validacion.positivo(campo);
        minJugadoresReserva=Validacion.positivo(minReserva);
        maxJugadoresReserva=maxReserva;
        minJugadoresNomina=Validacion.positivo(minNomina);
        maxJugadoresNomina=maxNomina;
        duracionPartidoMin=Validacion.positivo(duracion);
        permiteEmpate=empate;
        diferenciaNivelPermitida=diferencia;
        this.posiciones=new ArrayList<>(posiciones);
        tipoEstadistica=Objects.requireNonNull(tipo);
        Validacion.exigir(maxReserva>=minReserva&&maxNomina>=minNomina&&diferencia>=0,"Configuración de deporte inválida");
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
}
