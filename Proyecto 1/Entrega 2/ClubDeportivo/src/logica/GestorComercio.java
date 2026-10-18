package logica;
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
public class GestorComercio implements Serializable {
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
    public GestorComercio(Tienda t,Cafeteria c,GestorUsuarios u) {
        tienda=t;
        cafeteria=c;
        usuarios=u;
    }
    public Tienda getTienda() {
        return tienda;
    }
    public Cafeteria getCafeteria() {
        return cafeteria;
    }
    public List<Producto> getProductos() {
        return copiaLectura(productos.values());
    }
    public void registrarProducto(Producto p) {
        validar(!productos.containsKey(p.getCodigo()),"Producto duplicado");
        productos.put(p.getCodigo(),p);
    }
    public Venta iniciarVenta(PuntoVenta pv,Persona comprador) {
        validar((pv==tienda||pv==cafeteria)&&(comprador instanceof Empleado||(comprador instanceof Socio&&((Socio)comprador).isActivo())),"Comprador o punto de venta inválido");
        Venta v=new Venta(String.format("V%06d",++consecutivoVentas),comprador,pv);
        pendientes.add(v);
        return v;
    }
    public void agregarProducto(Venta v,Producto p,int cant) {
        validar(pendientes.contains(v)&&productos.get(p.getCodigo())==p&&cant>0,"Producto, venta o cantidad inválidos");
        int ya=v.getItems().stream().filter(i->i.getProducto()==p).mapToInt(ItemVenta::getCantidad).sum();
        if(inventario.getCantidad(p,v.getPuntoVenta().getUbicacion())<(long)ya+cant)throw new InventarioException("Stock insuficiente");
        v.agregarItem(p,cant);
    }
    public List<Ingrediente> consultarIngredientes(Snack s) {
        return s.getIngredientes();
    }
    public synchronized Venta finalizarVenta(Venta v,double propina,int puntos) {
        validar(pendientes.contains(v)&&!v.isCerrada()&&!v.getItems().isEmpty()&&(!v.requiereInfoAlergenos()||alergenosInformados(v)),"Venta vacía, desconocida o alérgenos no informados");
        validar(puntos>=0&&(v.getComprador() instanceof Socio||puntos==0),"Redención no permitida");
        Map<Producto,Integer> cantidades=new LinkedHashMap<>();
        v.getItems().forEach(i->cantidades.merge(i.getProducto(),i.getCantidad(),Integer::sum));
        for(Map.Entry<Producto,Integer> e:cantidades.entrySet())if(inventario.getCantidad(e.getKey(),v.getPuntoVenta().getUbicacion())<e.getValue())throw new InventarioException("Stock cambió antes de pagar");
        v.aplicarPropina(propina);
        v.calcularTotales(v.getPuntoVenta());
        Socio s=v.getComprador() instanceof Socio?(Socio)v.getComprador():null;
        validar(s==null||s.getCuenta().puedeRedimir(puntos),"Puntos insuficientes");
        v.redimirPuntos(puntos,(double)puntos*CuentaFidelizacion.VALOR_PUNTO);
        v.calcularTotales(v.getPuntoVenta());
        v.cerrar();
        if(s!=null) {
            s.getCuenta().redimir(puntos);
            v.setPuntosGanados(s.getCuenta().acumular(v.getBaseFidelizacion()));
        }
        cantidades.forEach((p,n)->inventario.descontar(p,v.getPuntoVenta().getUbicacion(),n));
        v.getPuntoVenta().registrarVenta(v);
        pendientes.remove(v);
        return v;
    }
    private boolean alergenosInformados(Venta v) {
        return v.isAlergenosInformados();
    }
    public Mensualidad registrarMensualidad(Socio s,YearMonth periodo,ConceptoMensualidad c,double valor) {
        validar(s.isActivo()&&mensualidades.stream().noneMatch(m->m.getSocio()==s&&m.getPeriodo().equals(periodo)&&m.getConcepto()==c),"Mensualidad duplicada o socio inactivo");
        Mensualidad m=new Mensualidad(s,periodo,c,valor);
        mensualidades.add(m);
        return m;
    }
    public List<Snack> buscarSnacksConAlergeno(Alergeno a) {
        return productos.values().stream().filter(p->p instanceof Snack&&((Snack)p).contieneAlergeno(a)).map(p->(Snack)p).collect(Collectors.toUnmodifiableList());
    }
    public List<Venta> getVentas(LocalDate ini,LocalDate fin) {
        return Stream.concat(tienda.getVentas().stream(),cafeteria.getVentas().stream()).filter(v->!v.getFechaHora().toLocalDate().isBefore(ini)&&!v.getFechaHora().toLocalDate().isAfter(fin)).collect(Collectors.toUnmodifiableList());
    }
    public List<Mensualidad> getMensualidades(LocalDate ini,LocalDate fin) {
        return mensualidades.stream().filter(m->!m.getFechaPago().isBefore(ini)&&!m.getFechaPago().isAfter(fin)).collect(Collectors.toUnmodifiableList());
    }
    public Inventario getInventario() {
        return inventario;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
