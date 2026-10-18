package modelo.inventario;

import excepciones.ClubException;
import excepciones.InventarioException;
import java.io.Serializable;
import modelo.comercio.Producto;
import modelo.personas.Administrador;

public class Traslado extends MovimientoInventario implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Ubicacion origen;
    public Traslado(Producto p,Ubicacion o,Ubicacion d,int n,Administrador a) throws ClubException
    {
        super(p,d,n,a);
        validar(o!=d,"Origen igual a getDestino()");
        origen=o;
    }
    @Override
    public void aplicar(Inventario inv) throws ClubException
    {
        validar(!isAplicado(),"Movimiento ya isAplicado()");
        if(inv.getCantidad(getProducto(),origen)<getCantidad())throw new InventarioException("Stock insuficiente para traslado");
        Existencia des=inv.obtenerExistencia(getProducto(),getDestino());
        validar((long)des.getCantidad()+getCantidad()<=Integer.MAX_VALUE,"Desbordamiento de inventario");
        inv.descontar(getProducto(),origen,getCantidad());
        des.aumentar(getCantidad());
        marcarAplicado();
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
