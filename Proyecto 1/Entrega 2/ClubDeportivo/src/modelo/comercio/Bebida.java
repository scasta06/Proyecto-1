package modelo.comercio;
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
public class Bebida extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean caliente;
    public Bebida(String c,String n,double precio,boolean caliente) {
        super(c,n,precio);
        this.caliente=caliente;
    }
    public boolean isCaliente() {
        return caliente;
    }
    public Rubro getRubro() {
        return Rubro.CAFETERIA;
    }
}
