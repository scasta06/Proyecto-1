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
public abstract class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String codigo,nombre;
    protected double precio;
    protected Producto(String c,String n,double p) {
        codigo=Validacion.texto(c);
        nombre=Validacion.texto(n);
        precio=Validacion.dinero(p);
    }
    public abstract Rubro getRubro();
    public String getCodigo() {
        return codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public double getPrecio() {
        return precio;
    }
}
