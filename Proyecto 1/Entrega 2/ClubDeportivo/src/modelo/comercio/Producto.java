package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.tipos.Rubro;

public abstract class Producto implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private double precio;
    protected Producto(String c,String n,double p) throws ClubException
    {
        codigo=textoRequerido(c);
        nombre=textoRequerido(n);
        precio=redondearDinero(p);
    }
    public abstract Rubro getRubro();
    public String getCodigo()
    {
        return codigo;
    }
    public String getNombre()
    {
        return nombre;
    }
    public double getPrecio()
    {
        return precio;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
