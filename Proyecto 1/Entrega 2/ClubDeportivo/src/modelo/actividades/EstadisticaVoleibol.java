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
public class EstadisticaVoleibol extends Estadistica implements Serializable {
    private static final long serialVersionUID = 1L;
    private int aces,bloqueos;
    public EstadisticaVoleibol(Socio j,Partido p,Map<String,Integer> v) {
        super(j,p,v);
        aces=valor("aces");
        bloqueos=valor("bloqueos");
    }
    public int calcularPuntosRanking() {
        return valor("aces")*2+valor("bloqueos");
    }
}
