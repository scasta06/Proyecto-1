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
public class Inventario implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Existencia> existencias=new ArrayList<>();
    private List<MovimientoInventario> movimientos=new ArrayList<>();
    public boolean estaRegistrado(Producto p) {
        return existencias.stream().anyMatch(e->e.getProducto()==p);
    }
    public int getCantidad(Producto p,Ubicacion u) {
        return existencias.stream().filter(e->e.getProducto()==p&&e.getUbicacion()==u).mapToInt(Existencia::getCantidad).sum();
    }
    public List<Existencia> consultar(Ubicacion u) {
        return existencias.stream().filter(e->e.getUbicacion()==u).collect(Collectors.toUnmodifiableList());
    }
    public Existencia obtenerExistencia(Producto p,Ubicacion u) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(u);
        for(Existencia e:existencias)if(e.getProducto()==p&&e.getUbicacion()==u)return e;
        Existencia e=new Existencia(p,u);
        existencias.add(e);
        return e;
    }
    public Reabastecimiento reabastecer(Producto p,Ubicacion u,int n,Administrador a) {
        Reabastecimiento r=new Reabastecimiento(p,u,n,a);
        r.aplicar(this);
        movimientos.add(r);
        return r;
    }
    public Traslado trasladar(Producto p,Ubicacion o,Ubicacion d,int n,Administrador a) {
        Traslado t=new Traslado(p,o,d,n,a);
        t.aplicar(this);
        movimientos.add(t);
        return t;
    }
    public void descontar(Producto p,Ubicacion u,int n) {
        obtenerExistencia(p,u).disminuir(n);
    }
    public List<MovimientoInventario> getMovimientos() {
        return Validacion.copia(movimientos);
    }
}
