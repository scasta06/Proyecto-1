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
public class Cafeteria extends PuntoVenta implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double TASA_IMPOCONSUMO=0.08,PROPINA_SUGERIDA=0.10;
    public Cafeteria(Ubicacion u) {
        super("Cafetería",u);
        Validacion.exigir(u.getTipo()==TipoUbicacion.CAFETERIA,"Ubicación no es cafetería");
    }
    public boolean admiteProducto(Producto p) {
        return p instanceof Snack||p instanceof Bebida;
    }
    public double calcularDescuento(Persona c,double s) {
        return 0;
    }
    public double calcularImpuesto(double b) {
        return Validacion.dinero(b*TASA_IMPOCONSUMO);
    }
    public boolean admitePropina() {
        return true;
    }
    public double sugerirPropina(double s) {
        return Validacion.dinero(s*PROPINA_SUGERIDA);
    }
}
