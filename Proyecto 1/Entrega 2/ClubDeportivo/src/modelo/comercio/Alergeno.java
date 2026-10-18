package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;

public class Alergeno implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String descripcion;
    public Alergeno(String n,String d) throws ClubException
    {
        nombre=textoRequerido(n);
        descripcion=d;
    }
    public String getNombre()
    {
        return nombre;
    }
    public String getDescripcion()
    {
        return descripcion;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
