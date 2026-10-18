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
public class EstadisticaIndividual extends Estadistica implements Serializable {
    private static final long serialVersionUID = 1L;
    private int puntosAnotados;
    private boolean gano;
    public EstadisticaIndividual(Socio j,Partido p,Map<String,Integer> v) {
        super(j,p,v);
        puntosAnotados=valor("puntosAnotados");
        gano=valor("gano")==1;
    }
    public int calcularPuntosRanking() {
        return valor("gano")==1?20:-10;
    }
}
