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
public class EstadisticaBaloncesto extends Estadistica implements Serializable {
    private static final long serialVersionUID = 1L;
    private int puntos,rebotes;
    public EstadisticaBaloncesto(Socio j,Partido p,Map<String,Integer> v) {
        super(j,p,v);
        puntos=valor("puntos");
        rebotes=valor("rebotes");
    }
    public int calcularPuntosRanking() {
        return valor("puntos")+valor("rebotes");
    }
}
