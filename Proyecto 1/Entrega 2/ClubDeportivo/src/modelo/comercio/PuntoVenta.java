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
public abstract class PuntoVenta implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String nombre;
    protected Ubicacion ubicacion;
    protected List<Venta> ventas=new ArrayList<>();
    protected PuntoVenta(String n,Ubicacion u) {
        nombre=n;
        ubicacion=u;
    }
    public abstract boolean admiteProducto(Producto p);
    public abstract double calcularDescuento(Persona c,double subtotal);
    public abstract double calcularImpuesto(double base);
    public abstract boolean admitePropina();
    public void registrarVenta(Venta v) {
        Validacion.exigir(v.isCerrada()&&v.getPuntoVenta()==this&&!ventas.contains(v),"Venta no registrable");
        ventas.add(v);
    }
    public Ubicacion getUbicacion() {
        return ubicacion;
    }
    public List<Venta> getVentas() {
        return Validacion.copia(ventas);
    }
    public String getNombre() {
        return nombre;
    }
}
