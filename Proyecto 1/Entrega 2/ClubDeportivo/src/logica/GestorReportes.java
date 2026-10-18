package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import modelo.comercio.Mensualidad;
import modelo.comercio.Venta;
import modelo.tipos.Rubro;

/** Construye los reportes de ventas a partir del gestor de comercio. */
public class GestorReportes implements Serializable
{
    private static final long serialVersionUID = 1L;
    private GestorComercio comercio;
    public GestorReportes(GestorComercio c)
    {
        comercio=c;
    }
    public ReporteVentas generarReporteVentas(LocalDate ini,LocalDate fin) throws ClubException
    {
        ReporteVentas r=new ReporteVentas(ini,fin);
        for(Venta v:comercio.getVentas(ini,fin))r.agregar(v.getItems().get(0).getProducto().getRubro(),YearMonth.from(v.getFechaHora()),v.getTotalPagado());
        for(Mensualidad m:comercio.getMensualidades(ini,fin))r.agregar(Rubro.MENSUALIDADES,YearMonth.from(m.getFechaPago()),m.getValor());
        return r;
    }
}
