package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.inventario.Ubicacion;
import modelo.personas.Persona;

public abstract class PuntoVenta implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private Ubicacion ubicacion;
    protected List<Venta> ventas=new ArrayList<>();
    protected PuntoVenta(String n,Ubicacion u)
    {
        nombre=n;
        ubicacion=u;
    }
    public abstract boolean admiteProducto(Producto p);
    public abstract double calcularDescuento(Persona c,double subtotal) throws ClubException ;
    public abstract double calcularImpuesto(double base) throws ClubException ;
    public abstract boolean admitePropina();
    public void registrarVenta(Venta v) throws ClubException
    {
        validar(v.isCerrada()&&v.getPuntoVenta()==this&&!ventas.contains(v),"Venta no registrable");
        ventas.add(v);
    }
    public Ubicacion getUbicacion()
    {
        return ubicacion;
    }
    public List<Venta> getVentas()
    {
        return copiaLectura(ventas);
    }
    public String getNombre()
    {
        return nombre;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
