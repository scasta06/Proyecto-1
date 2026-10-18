package modelo.inventario;
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
public class Existencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private Ubicacion ubicacion;
    private int cantidad,stockMinimo;
    public Existencia(Producto p,Ubicacion u) {
        producto=p;
        ubicacion=u;
    }
    public void aumentar(int n) {
        cantidad=Math.addExact(cantidad,Validacion.positivo(n));
    }
    public void disminuir(int n) {
        if(n<=0||n>cantidad)throw new InventarioException("Existencias insuficientes");
        cantidad-=n;
    }
    public boolean estaBajoMinimo() {
        return cantidad<stockMinimo;
    }
    public int getCantidad() {
        return cantidad;
    }
    public Producto getProducto() {
        return producto;
    }
    public Ubicacion getUbicacion() {
        return ubicacion;
    }
    public void setStockMinimo(int n) {
        Validacion.exigir(n>=0,"Mínimo negativo");
        stockMinimo=n;
    }
}
