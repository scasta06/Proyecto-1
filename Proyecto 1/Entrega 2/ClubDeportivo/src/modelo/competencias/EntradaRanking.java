package modelo.competencias;
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
public class EntradaRanking implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio jugador;
    private int partidosJugados,puntosRanking=1000;
    private Map<String,Integer> acumulados=new LinkedHashMap<>();
    public EntradaRanking(Socio s) {
        jugador=s;
    }
    public void acumular(Estadistica e) {
        Validacion.exigir(e.getJugador()==jugador,"Estadística de otro jugador");
        partidosJugados++;
        puntosRanking=Math.max(0,puntosRanking+e.calcularPuntosRanking());
        e.getValores().forEach((k,v)->acumulados.merge(k,v,Integer::sum));
    }
    public Socio getJugador() {
        return jugador;
    }
    public int getPartidosJugados() {
        return partidosJugados;
    }
    public int getPuntosRanking() {
        return puntosRanking;
    }
    public Map<String,Integer> getAcumulados() {
        return Collections.unmodifiableMap(acumulados);
    }
}
