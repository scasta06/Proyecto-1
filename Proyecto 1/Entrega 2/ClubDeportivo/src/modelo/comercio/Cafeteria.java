package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.inventario.Ubicacion;
import modelo.personas.Persona;
import modelo.tipos.TipoUbicacion;

public class Cafeteria extends PuntoVenta implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final double TASA_IMPOCONSUMO=0.08,PROPINA_SUGERIDA=0.10;
    public Cafeteria(Ubicacion u) throws ClubException
    {
        super("Cafetería",u);
        validar(u.getTipo()==TipoUbicacion.CAFETERIA,"Ubicación no es cafetería");
    }
    @Override
    public boolean admiteProducto(Producto p)
    {
        return p instanceof Snack;
    }
    @Override
    public double calcularDescuento(Persona c,double s)
    {
        return 0;
    }
    @Override
    public double calcularImpuesto(double b) throws ClubException
    {
        return redondearDinero(b*TASA_IMPOCONSUMO);
    }
    @Override
    public boolean admitePropina()
    {
        return true;
    }
    public double sugerirPropina(double s) throws ClubException
    {
        return redondearDinero(s*PROPINA_SUGERIDA);
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
