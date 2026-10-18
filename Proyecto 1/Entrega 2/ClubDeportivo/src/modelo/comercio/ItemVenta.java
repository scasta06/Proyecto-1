package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;

public class ItemVenta implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    public ItemVenta(Producto p,int n) throws ClubException
    {
        producto=p;
        cantidad=enteroPositivo(n);
        precioUnitario=p.getPrecio();
    }
    public double getSubtotal() throws ClubException
    {
        return redondearDinero(cantidad*precioUnitario);
    }
    public Producto getProducto()
    {
        return producto;
    }
    public int getCantidad()
    {
        return cantidad;
    }
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
