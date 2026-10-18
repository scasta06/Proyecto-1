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
public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numero;
    private LocalDateTime fechaHora=LocalDateTime.now();
    private double subtotal,descuento,impuesto,propina,valorRedimido,totalPagado;
    private int puntosRedimidos,puntosGanados;
    private boolean alergenosInformados,cerrada;
    private Persona comprador;
    private PuntoVenta puntoVenta;
    private List<ItemVenta> items=new ArrayList<>();
    private boolean codigoCompartido;
    public Venta(String n,Persona c,PuntoVenta pv) {
        numero=n;
        comprador=c;
        puntoVenta=pv;
    }
    private void abierta() {
        Validacion.exigir(!cerrada,"Venta cerrada");
    }
    public void agregarItem(Producto p,int cantidad) {
        abierta();
        Validacion.exigir(puntoVenta.admiteProducto(p),"Producto no admitido");
        items.add(new ItemVenta(p,cantidad));
        alergenosInformados=false;
    }
    public boolean requiereInfoAlergenos() {
        return items.stream().anyMatch(i->i.getProducto() instanceof Snack&&!((Snack)i.getProducto()).getAlergenos().isEmpty());
    }
    public void marcarAlergenosInformados() {
        abierta();
        alergenosInformados=true;
    }
    public void aplicarCodigoCompartido() {
        abierta();
        codigoCompartido=true;
    }
    public void calcularTotales(PuntoVenta pv) {
        abierta();
        Validacion.exigir(pv==puntoVenta,"Punto de venta incorrecto");
        subtotal=Validacion.dinero(items.stream().mapToDouble(ItemVenta::getSubtotal).sum());
        descuento=pv.calcularDescuento(comprador,subtotal);
        if(codigoCompartido&&comprador instanceof Socio&&pv instanceof Tienda)descuento=Validacion.dinero(subtotal*Tienda.DESCUENTO_SOCIO);
        impuesto=pv.calcularImpuesto(subtotal-descuento);
        totalPagado=Validacion.dinero(subtotal-descuento+impuesto+propina-valorRedimido);
    }
    public void aplicarPropina(double v) {
        abierta();
        Validacion.exigir(puntoVenta.admitePropina()||v==0,"La tienda no admite propina");
        propina=Validacion.dinero(v);
    }
    public void redimirPuntos(int p,double valor) {
        abierta();
        Validacion.exigir(p>=0&&valor==(double)p*CuentaFidelizacion.VALOR_PUNTO&&valor<=subtotal-descuento+impuesto,"Redención inválida");
        puntosRedimidos=p;
        valorRedimido=valor;
    }
    public double getBaseFidelizacion() {
        return Math.max(0,subtotal-descuento-valorRedimido);
    }
    public void cerrar() {
        abierta();
        Validacion.exigir(!items.isEmpty()&&(!requiereInfoAlergenos()||alergenosInformados),"Venta vacía o alérgenos no informados");
        cerrada=true;
    }
    public boolean isAlergenosInformados() {
        return alergenosInformados;
    }
    public boolean isCerrada() {
        return cerrada;
    }
    public Persona getComprador() {
        return comprador;
    }
    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }
    public List<ItemVenta> getItems() {
        return Validacion.copia(items);
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public double getTotalPagado() {
        return totalPagado;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public double getDescuento() {
        return descuento;
    }
    public double getImpuesto() {
        return impuesto;
    }
    public double getPropina() {
        return propina;
    }
    public int getPuntosGanados() {
        return puntosGanados;
    }
    public void setPuntosGanados(int n) {
        puntosGanados=n;
    }
    public String getNumero() {
        return numero;
    }
}
