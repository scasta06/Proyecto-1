package modelo.inventario;

import excepciones.ClubException;
import java.io.Serializable;
import modelo.comercio.Producto;
import modelo.personas.Administrador;

public class Reabastecimiento extends MovimientoInventario implements Serializable
{
    private static final long serialVersionUID = 1L;
    public Reabastecimiento(Producto p,Ubicacion d,int n,Administrador a) throws ClubException
    {
        super(p,d,n,a);
    }
    @Override
    public void aplicar(Inventario inv) throws ClubException
    {
        validar(!isAplicado(),"Movimiento ya isAplicado()");
        inv.obtenerExistencia(getProducto(),getDestino()).aumentar(getCantidad());
        marcarAplicado();
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
