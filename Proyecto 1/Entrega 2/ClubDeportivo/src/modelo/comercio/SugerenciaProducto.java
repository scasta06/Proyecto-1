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
public class SugerenciaProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Empleado empleado;
    private Producto producto;
    private EstadoSolicitud estado=EstadoSolicitud.PENDIENTE;
    private Administrador administrador;
    public SugerenciaProducto(Empleado e,Producto p) {
        empleado=Objects.requireNonNull(e);
        producto=Objects.requireNonNull(p);
    }
    public Producto getProducto() {
        return producto;
    }
    public EstadoSolicitud getEstado() {
        return estado;
    }
    public void responder(Administrador a,boolean aprobar) {
        Validacion.exigir(estado==EstadoSolicitud.PENDIENTE,"Sugerencia ya respondida");
        administrador=Objects.requireNonNull(a);
        estado=aprobar?EstadoSolicitud.APROBADA:EstadoSolicitud.RECHAZADA;
    }
}
