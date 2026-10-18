package modelo.inventario;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import modelo.personas.*;
import modelo.deportes.*;
import modelo.actividades.*;
import modelo.competencias.*;
import modelo.personal.*;
import modelo.inventario.*;
import modelo.comercio.*;
import modelo.tipos.*;
import logica.*;
import excepciones.*;
public class Traslado extends MovimientoInventario implements Serializable {
    private static final long serialVersionUID = 1L;
    private Ubicacion origen;
    public Traslado(Producto p,Ubicacion o,Ubicacion d,int n,Administrador a) {
        super(p,d,n,a);
        Validacion.exigir(o!=d,"Origen igual a destino");
        origen=o;
    }
    public void aplicar(Inventario inv) {
        Validacion.exigir(!aplicado,"Movimiento ya aplicado");
        if(inv.getCantidad(producto,origen)<cantidad)throw new InventarioException("Stock insuficiente para traslado");
        Existencia des=inv.obtenerExistencia(producto,destino);
        Validacion.exigir((long)des.getCantidad()+cantidad<=Integer.MAX_VALUE,"Desbordamiento de inventario");
        inv.descontar(producto,origen,cantidad);
        des.aumentar(cantidad);
        aplicado=true;
    }
}
