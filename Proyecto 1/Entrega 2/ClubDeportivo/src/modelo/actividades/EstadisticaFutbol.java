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
public class EstadisticaFutbol extends Estadistica implements Serializable {
    private static final long serialVersionUID = 1L;
    private int goles,asistencias;
    public EstadisticaFutbol(Socio j,Partido p,Map<String,Integer> v) {
        super(j,p,v);
        goles=valor("goles");
        asistencias=valor("asistencias");
    }
    public int calcularPuntosRanking() {
        return valor("goles")*3+valor("asistencias");
    }
}
