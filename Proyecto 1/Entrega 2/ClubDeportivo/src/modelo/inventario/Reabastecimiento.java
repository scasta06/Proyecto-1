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
public class Reabastecimiento extends MovimientoInventario implements Serializable {
    private static final long serialVersionUID = 1L;
    public Reabastecimiento(Producto p,Ubicacion d,int n,Administrador a) {
        super(p,d,n,a);
    }
    public void aplicar(Inventario inv) {
        validar(!aplicado,"Movimiento ya aplicado");
        inv.obtenerExistencia(producto,destino).aumentar(cantidad);
        aplicado=true;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
}
