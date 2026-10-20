package modelo.inventario;

import excepciones.ClubException;
import excepciones.InventarioException;
import java.io.Serializable;
import modelo.comercio.Producto;

public class Existencia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private Ubicacion ubicacion;
    private int cantidad;
    private int stockMinimo;
    public Existencia(Producto p,Ubicacion u)
    {
        producto=p;
        ubicacion=u;
    }
    public void aumentar(int n) throws ClubException
    {
        if (n <= 0 || (long)cantidad + n > Integer.MAX_VALUE)
            throw new InventarioException("Cantidad fuera de rango");
        cantidad += n;
    }
    public void disminuir(int n) throws ClubException
    {
        if(n<=0||n>cantidad)throw new InventarioException("Existencias insuficientes");
        cantidad-=n;
    }
    public boolean estaBajoMinimo()
    {
        return cantidad<stockMinimo;
    }
    public int getCantidad()
    {
        return cantidad;
    }
    public Producto getProducto()
    {
        return producto;
    }
    public Ubicacion getUbicacion()
    {
        return ubicacion;
    }
    public void setStockMinimo(int n) throws ClubException
    {
        validar(n>=0,"Mínimo negativo");
        stockMinimo=n;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
}
