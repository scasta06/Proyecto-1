package modelo.comercio;
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
public class Tienda extends PuntoVenta implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double TASA_IVA=0.19,DESCUENTO_EMPLEADO=0.15,DESCUENTO_SOCIO=0.08;
    public Tienda(Ubicacion u) {
        super("Tienda",u);
        validar(u.getTipo()==TipoUbicacion.TIENDA,"Ubicación no es tienda");
    }
    public boolean admiteProducto(Producto p) {
        return p instanceof ArticuloDeportivo;
    }
    public double calcularDescuento(Persona c,double s) {
        return c instanceof Empleado?redondearDinero(s*DESCUENTO_EMPLEADO):c instanceof Socio?redondearDinero(s*DESCUENTO_SOCIO):0;
    }
    public double calcularImpuesto(double b) {
        return redondearDinero(b*TASA_IVA);
    }
    public boolean admitePropina() {
        return false;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static double redondearDinero(double valor) {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
