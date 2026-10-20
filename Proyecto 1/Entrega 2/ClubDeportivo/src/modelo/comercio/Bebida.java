package modelo.comercio;

import excepciones.ClubException;
import modelo.tipos.Rubro;

/** La temperatura forma parte del producto y determina la restriccion de seguridad. */
public class Bebida extends Producto
{
    private static final long serialVersionUID = 1L;
    private boolean caliente;
    public Bebida(String codigo, String nombre, double precio, boolean caliente) throws ClubException
    {
        super(codigo,nombre,precio);
        this.caliente = caliente;
    }
    public boolean isCaliente() { return caliente; }
    @Override
    public Rubro getRubro() { return Rubro.CAFETERIA; }
}
