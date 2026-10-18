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
public class ItemVenta implements Serializable {
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    public ItemVenta(Producto p,int n) {
        producto=p;
        cantidad=enteroPositivo(n);
        precioUnitario=p.getPrecio();
    }
    public double getSubtotal() {
        return redondearDinero(cantidad*precioUnitario);
    }
    public Producto getProducto() {
        return producto;
    }
    public int getCantidad() {
        return cantidad;
    }
    private static int enteroPositivo(int valor) {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    private static double redondearDinero(double valor) {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
