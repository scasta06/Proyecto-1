package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.tipos.Rubro;

public class Snack extends Producto implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int calorias;
    private List<Ingrediente> ingredientes;
    public Snack(String c,String n,double p,int cal,List<Ingrediente> ing) throws ClubException
    {
        super(c,n,p);
        validar(cal>=0&&!ing.isEmpty(),"Snack sin ingredientes o calorías negativas");
        calorias=cal;
        ingredientes=new ArrayList<>(ing);
    }
    @Override
    public Rubro getRubro()
    {
        return Rubro.CAFETERIA;
    }
    public List<Ingrediente> getIngredientes()
    {
        return copiaLectura(ingredientes);
    }
    public List<Alergeno> getAlergenos()
    {
        List<Alergeno> resultado = new ArrayList<Alergeno>();
        for (Ingrediente ingrediente : ingredientes)
        {
            for (Alergeno alergeno : ingrediente.getAlergenos())
            {
                if (!resultado.contains(alergeno))
                {
                    resultado.add(alergeno);
                }
            }
        }
        return resultado;
    }
    public boolean contieneAlergeno(Alergeno a)
    {
        return getAlergenos().contains(a);
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
