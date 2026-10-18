package logica;

import excepciones.ClubException;
import excepciones.InventarioException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.comercio.Alergeno;
import modelo.comercio.Cafeteria;
import modelo.comercio.CuentaFidelizacion;
import modelo.comercio.Ingrediente;
import modelo.comercio.ItemVenta;
import modelo.comercio.Mensualidad;
import modelo.comercio.Producto;
import modelo.comercio.PuntoVenta;
import modelo.comercio.Snack;
import modelo.comercio.Tienda;
import modelo.comercio.Venta;
import modelo.inventario.Inventario;
import modelo.personas.Empleado;
import modelo.personas.Persona;
import modelo.personas.Socio;
import modelo.tipos.ConceptoMensualidad;

/** Coordina las ventas y valida inventario y fidelizacion antes de cobrar. */
public class GestorComercio implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Tienda tienda;
    private Cafeteria cafeteria;
    private Inventario inventario=new Inventario();
    private Map<String,Producto> productos=new LinkedHashMap<>();
    private List<Alergeno> alergenos=new ArrayList<>();
    private List<Mensualidad> mensualidades=new ArrayList<>();
    private int consecutivoVentas;
    private List<Venta> pendientes=new ArrayList<>();
    private GestorUsuarios usuarios;
    public GestorComercio(Tienda t,Cafeteria c,GestorUsuarios u)
    {
        tienda=t;
        cafeteria=c;
        usuarios=u;
    }
    public Tienda getTienda()
    {
        return tienda;
    }
    public Cafeteria getCafeteria()
    {
        return cafeteria;
    }
    public List<Producto> getProductos()
    {
        return copiaLectura(productos.values());
    }
    public void registrarProducto(Producto p) throws ClubException
    {
        validar(!productos.containsKey(p.getCodigo()),"Producto duplicado");
        productos.put(p.getCodigo(),p);
    }
    public Venta iniciarVenta(PuntoVenta pv,Persona comprador) throws ClubException
    {
        validar((pv==tienda||pv==cafeteria)&&(comprador instanceof Empleado||(comprador instanceof Socio&&((Socio)comprador).isActivo())),"Comprador o punto de venta inválido");
        Venta v=new Venta(String.format("V%06d",++consecutivoVentas),comprador,pv);
        pendientes.add(v);
        return v;
    }
    public void agregarProducto(Venta v,Producto p,int cant) throws ClubException
    {
        validar(pendientes.contains(v) && productos.get(p.getCodigo()) == p && cant > 0, "Producto, venta o cantidad inválidos");
        int cantidadAnterior = 0;
        for (ItemVenta item : v.getItems())
        {
            if (item.getProducto() == p)
            {
                cantidadAnterior += item.getCantidad();
            }
        }
        if (inventario.getCantidad(p, v.getPuntoVenta().getUbicacion()) < (long) cantidadAnterior + cant)
        {
            throw new InventarioException("Stock insuficiente");
        }
        v.agregarItem(p, cant);
    }
    public List<Ingrediente> consultarIngredientes(Snack s)
    {
        return s.getIngredientes();
    }
    public synchronized Venta finalizarVenta(Venta v,double propina,int puntos) throws ClubException
    {
        validar(pendientes.contains(v)&&!v.isCerrada()&&!v.getItems().isEmpty()&&(!v.requiereInfoAlergenos()||alergenosInformados(v)),"Venta vacía, desconocida o alérgenos no informados");
        validar(puntos>=0&&(v.getComprador() instanceof Socio||puntos==0),"Redención no permitida");
        Map<Producto,Integer> cantidades=new LinkedHashMap<>();
        for (ItemVenta item : v.getItems())
        {
            Integer cantidadAnterior = cantidades.get(item.getProducto());
            if (cantidadAnterior == null)
            {
                cantidadAnterior = 0;
            }
            cantidades.put(item.getProducto(), cantidadAnterior + item.getCantidad());
        }
        for(Map.Entry<Producto,Integer> e:cantidades.entrySet())if(inventario.getCantidad(e.getKey(),v.getPuntoVenta().getUbicacion())<e.getValue())throw new InventarioException("Stock cambió antes de pagar");
        v.aplicarPropina(propina);
        v.calcularTotales(v.getPuntoVenta());
        Socio s=v.getComprador() instanceof Socio?(Socio)v.getComprador():null;
        validar(s==null||s.getCuenta().puedeRedimir(puntos),"Puntos insuficientes");
        v.redimirPuntos(puntos,(double)puntos*CuentaFidelizacion.VALOR_PUNTO);
        v.calcularTotales(v.getPuntoVenta());
        v.cerrar();
        if(s!=null)
        {
            s.getCuenta().redimir(puntos);
            v.setPuntosGanados(s.getCuenta().acumular(v.getBaseFidelizacion()));
        }
        for (Producto producto : cantidades.keySet())
        {
            inventario.descontar(producto, v.getPuntoVenta().getUbicacion(), cantidades.get(producto));
        }
        v.getPuntoVenta().registrarVenta(v);
        pendientes.remove(v);
        return v;
    }
    private boolean alergenosInformados(Venta v)
    {
        return v.isAlergenosInformados();
    }
    public Mensualidad registrarMensualidad(Socio s,YearMonth periodo,ConceptoMensualidad c,double valor) throws ClubException
    {
        validar(s.isActivo(), "Socio inactivo");
        for (Mensualidad anterior : mensualidades)
        {
            validar(!(anterior.getSocio() == s && anterior.getPeriodo().equals(periodo) && anterior.getConcepto() == c), "Mensualidad duplicada");
        }
        Mensualidad mensualidad = new Mensualidad(s, periodo, c, valor);
        mensualidades.add(mensualidad);
        return mensualidad;
    }
    public List<Snack> buscarSnacksConAlergeno(Alergeno a)
    {
        List<Snack> resultado = new ArrayList<Snack>();
        for (Producto producto : productos.values())
        {
            if (producto instanceof Snack)
            {
                Snack snack = (Snack) producto;
                if (snack.contieneAlergeno(a))
                {
                    resultado.add(snack);
                }
            }
        }
        return resultado;
    }
    public List<Venta> getVentas(LocalDate ini,LocalDate fin) throws ClubException
    {
        validar(!fin.isBefore(ini), "Periodo invertido");
        List<Venta> ventas = new ArrayList<Venta>(tienda.getVentas());
        ventas.addAll(cafeteria.getVentas());
        List<Venta> resultado = new ArrayList<Venta>();
        for (Venta venta : ventas)
        {
            LocalDate fecha = venta.getFechaHora().toLocalDate();
            if (!fecha.isBefore(ini) && !fecha.isAfter(fin))
            {
                resultado.add(venta);
            }
        }
        return resultado;
    }
    public List<Mensualidad> getMensualidades(LocalDate ini,LocalDate fin) throws ClubException
    {
        validar(!fin.isBefore(ini), "Periodo invertido");
        List<Mensualidad> resultado = new ArrayList<Mensualidad>();
        for (Mensualidad mensualidad : mensualidades)
        {
            if (!mensualidad.getFechaPago().isBefore(ini) && !mensualidad.getFechaPago().isAfter(fin))
            {
                resultado.add(mensualidad);
            }
        }
        return resultado;
    }
    public Inventario getInventario()
    {
        return inventario;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
