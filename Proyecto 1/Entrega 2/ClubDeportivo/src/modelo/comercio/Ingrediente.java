package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Ingrediente implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private List<Alergeno> alergenos;
    public Ingrediente(String n,List<Alergeno> a) throws ClubException
    {
        nombre=textoRequerido(n);
        alergenos=new ArrayList<>(a);
    }
    public String getNombre()
    {
        return nombre;
    }
    public List<Alergeno> getAlergenos()
    {
        return copiaLectura(alergenos);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
