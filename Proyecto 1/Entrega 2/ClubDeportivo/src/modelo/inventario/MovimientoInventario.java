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
public abstract class MovimientoInventario implements Serializable {
    private static final long serialVersionUID = 1L;
    protected LocalDateTime fechaHora=LocalDateTime.now();
    protected int cantidad;
    protected Producto producto;
    protected Ubicacion destino;
    protected Administrador realizadoPor;
    protected boolean aplicado;
    protected MovimientoInventario(Producto p,Ubicacion d,int n,Administrador a) {
        producto=p;
        destino=d;
        cantidad=Validacion.positivo(n);
        realizadoPor=Objects.requireNonNull(a);
    }
    public abstract void aplicar(Inventario inv);
}
