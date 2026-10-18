package modelo.inventario;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.tipos.TipoUbicacion;

public class Ubicacion implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private TipoUbicacion tipo;
    public Ubicacion(String c,String n,TipoUbicacion t) throws ClubException
    {
        codigo=textoRequerido(c);
        nombre=textoRequerido(n);
        tipo=t;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public String getNombre()
    {
        return nombre;
    }
    public TipoUbicacion getTipo()
    {
        return tipo;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
