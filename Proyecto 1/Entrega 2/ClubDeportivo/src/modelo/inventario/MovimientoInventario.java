package modelo.inventario;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import modelo.comercio.Producto;
import modelo.personas.Administrador;

public abstract class MovimientoInventario implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected LocalDateTime fechaHora=LocalDateTime.now();
    private int cantidad;
    private Producto producto;
    private Ubicacion destino;
    private Administrador realizadoPor;
    private boolean aplicado;
    protected MovimientoInventario(Producto p,Ubicacion d,int n,Administrador a) throws ClubException
    {
        producto=p;
        destino=d;
        cantidad=enteroPositivo(n);
        realizadoPor=Objects.requireNonNull(a);
    }
    public abstract void aplicar(Inventario inv) throws ClubException ;
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    protected Producto getProducto()
    {
        return producto;
    }
    protected Ubicacion getDestino()
    {
        return destino;
    }
    protected int getCantidad()
    {
        return cantidad;
    }
    protected boolean isAplicado()
    {
        return aplicado;
    }
    protected void marcarAplicado()
    {
        aplicado = true;
    }
}
