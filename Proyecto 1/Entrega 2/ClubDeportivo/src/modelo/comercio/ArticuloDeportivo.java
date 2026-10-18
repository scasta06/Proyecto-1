package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.tipos.Rubro;

public class ArticuloDeportivo extends Producto implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String marca;
    private String talla;
    public ArticuloDeportivo(String c,String n,double p,String marca,String talla) throws ClubException
    {
        super(c,n,p);
        this.marca=marca;
        this.talla=talla;
    }
    @Override
    public Rubro getRubro()
    {
        return Rubro.ARTICULOS_DEPORTIVOS;
    }
    public String getMarca()
    {
        return marca;
    }
    public String getTalla()
    {
        return talla;
    }
}
