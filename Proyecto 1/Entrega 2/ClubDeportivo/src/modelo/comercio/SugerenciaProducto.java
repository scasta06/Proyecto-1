package modelo.comercio;

import excepciones.ClubException;
import excepciones.VentaException;
import java.io.Serializable;
import modelo.personas.Administrador;
import modelo.personas.Empleado;
import modelo.tipos.EstadoSolicitud;

/** Un empleado propone un producto; solo se incorpora al catalogo al aprobarlo. */
public class SugerenciaProducto implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private Empleado autor;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private Administrador revisadaPor;
    public SugerenciaProducto(Producto producto, Empleado autor) throws ClubException
    {
        if (producto == null || autor == null) throw new VentaException("Sugerencia incompleta");
        this.producto = producto;
        this.autor = autor;
    }
    public Producto getProducto() { return producto; }
    public Empleado getAutor() { return autor; }
    public EstadoSolicitud getEstado() { return estado; }
    public Administrador getRevisadaPor() { return revisadaPor; }
    public void resolver(Administrador admin, boolean aprobar) throws ClubException
    {
        if (admin == null || estado != EstadoSolicitud.PENDIENTE) throw new VentaException("Sugerencia ya resuelta o administrador inválido");
        estado = aprobar ? EstadoSolicitud.APROBADA : EstadoSolicitud.RECHAZADA;
        revisadaPor = admin;
    }
}
