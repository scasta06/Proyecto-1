package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.inventario.Ubicacion;
import modelo.personas.Empleado;
import modelo.personas.Persona;
import modelo.personas.Socio;
import modelo.tipos.TipoUbicacion;

public class Tienda extends PuntoVenta implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final double TASA_IVA=0.19,DESCUENTO_EMPLEADO=0.15,DESCUENTO_SOCIO=0.08;
    public Tienda(Ubicacion u) throws ClubException
    {
        super("Tienda",u);
        validar(u.getTipo()==TipoUbicacion.TIENDA,"Ubicación no es tienda");
    }
    @Override
    public boolean admiteProducto(Producto p)
    {
        return p instanceof ArticuloDeportivo;
    }
    @Override
    public double calcularDescuento(Persona c,double s) throws ClubException
    {
        return c instanceof Empleado?redondearDinero(s*DESCUENTO_EMPLEADO):c instanceof Socio?redondearDinero(s*DESCUENTO_SOCIO):0;
    }
    @Override
    public double calcularImpuesto(double b) throws ClubException
    {
        return redondearDinero(b*TASA_IVA);
    }
    @Override
    public boolean admitePropina()
    {
        return false;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
