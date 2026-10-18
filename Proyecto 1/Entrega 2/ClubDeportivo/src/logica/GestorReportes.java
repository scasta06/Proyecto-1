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
public class GestorReportes implements Serializable {
    private static final long serialVersionUID = 1L;
    private GestorComercio comercio;
    public GestorReportes(GestorComercio c) {
        comercio=c;
    }
    public ReporteVentas generarReporteVentas(LocalDate ini,LocalDate fin) {
        ReporteVentas r=new ReporteVentas(ini,fin);
        for(Venta v:comercio.getVentas(ini,fin))r.agregar(v.getItems().get(0).getProducto().getRubro(),YearMonth.from(v.getFechaHora()),v.getTotalPagado());
        for(Mensualidad m:comercio.getMensualidades(ini,fin))r.agregar(Rubro.MENSUALIDADES,YearMonth.from(m.getFechaPago()),m.getValor());
        return r;
    }
}
