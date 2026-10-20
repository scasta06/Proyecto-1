package modelo.inventario;

import excepciones.ClubException;
import excepciones.InventarioException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import modelo.comercio.Producto;
import modelo.personas.Administrador;

public class Inventario implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Existencia> existencias=new ArrayList<>();
    private List<MovimientoInventario> movimientos=new ArrayList<>();
    public boolean estaRegistrado(Producto p)
    {
        for (Existencia existencia : existencias)
        {
            if (existencia.getProducto() == p)
            {
                return true;
            }
        }
        return false;
    }
    public int getCantidad(Producto p,Ubicacion u)
    {
        int cantidad = 0;
        for (Existencia existencia : existencias)
        {
            if (existencia.getProducto() == p && existencia.getUbicacion() == u)
            {
                cantidad += existencia.getCantidad();
            }
        }
        return cantidad;
    }
    public List<Existencia> consultar(Ubicacion u)
    {
        List<Existencia> resultado = new ArrayList<Existencia>();
        for (Existencia existencia : existencias)
        {
            if (existencia.getUbicacion() == u)
            {
                resultado.add(existencia);
            }
        }
        return resultado;
    }
    public Existencia obtenerExistencia(Producto p,Ubicacion u)
    {
        Objects.requireNonNull(p);
        Objects.requireNonNull(u);
        for(Existencia e:existencias)if(e.getProducto()==p&&e.getUbicacion()==u)return e;
        Existencia e=new Existencia(p,u);
        existencias.add(e);
        return e;
    }
    public Reabastecimiento reabastecer(Producto p,Ubicacion u,int n,Administrador a) throws ClubException
    {
        if (p == null || u == null || a == null || n <= 0 || !estaRegistrado(p))
            throw new InventarioException("Cantidad o producto no registrado");
        Reabastecimiento r=new Reabastecimiento(p,u,n,a);
        r.aplicar(this);
        movimientos.add(r);
        return r;
    }
    public Traslado trasladar(Producto p,Ubicacion o,Ubicacion d,int n,Administrador a) throws ClubException
    {
        if (p == null || o == null || d == null || a == null || n <= 0 || o == d || getCantidad(p,o) < n)
            throw new InventarioException("Traslado inválido o existencias insuficientes");
        Traslado t=new Traslado(p,o,d,n,a);
        t.aplicar(this);
        movimientos.add(t);
        return t;
    }
    public void descontar(Producto p,Ubicacion u,int n) throws ClubException
    {
        obtenerExistencia(p,u).disminuir(n);
    }
    public List<MovimientoInventario> getMovimientos()
    {
        return copiaLectura(movimientos);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
