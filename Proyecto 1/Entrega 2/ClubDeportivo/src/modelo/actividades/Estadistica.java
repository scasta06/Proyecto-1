package modelo.actividades;
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
public abstract class Estadistica implements Serializable {
    private static final long serialVersionUID = 1L;
    protected LocalDate fechaRegistro=LocalDate.now();
    protected Socio jugador;
    protected Partido partido;
    protected Map<String,Integer> valores;
    protected Estadistica(Socio j,Partido p,Map<String,Integer> v) {
        jugador=j;
        partido=p;
        valores=new LinkedHashMap<>(v);
        Validacion.exigir(v.values().stream().allMatch(n->n!=null&&n>=0),"Estadísticas negativas o nulas");
    }
    public Map<String,Integer> getValores() {
        return Collections.unmodifiableMap(valores);
    }
    public abstract int calcularPuntosRanking();
    public Socio getJugador() {
        return jugador;
    }
    public Partido getPartido() {
        return partido;
    }
    protected int valor(String k) {
        return valores.getOrDefault(k,0);
    }
}
