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
public class ReporteVentas implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate fechaInicio,fechaFin;
    private Map<Rubro,Double> totalesPorRubro=new EnumMap<>(Rubro.class);
    private Map<YearMonth,Map<Rubro,Double>> totalesPorMes=new TreeMap<>();
    private int transacciones;
    public ReporteVentas(LocalDate ini,LocalDate fin) {
        validar(!fin.isBefore(ini),"Periodo invertido");
        fechaInicio=ini;
        fechaFin=fin;
    }
    public void agregar(Rubro r,YearMonth mes,double valor) {
        totalesPorRubro.merge(r,valor,Double::sum);
        totalesPorMes.computeIfAbsent(mes,k->new EnumMap<>(Rubro.class)).merge(r,valor,Double::sum);
        transacciones++;
    }
    public double getTotal() {
        return redondearDinero(totalesPorRubro.values().stream().mapToDouble(Double::doubleValue).sum());
    }
    public boolean estaVacio() {
        return transacciones==0;
    }
    public Map<Rubro,Double> getTotalesPorRubro() {
        return Collections.unmodifiableMap(totalesPorRubro);
    }
    public Map<YearMonth,Map<Rubro,Double>> getTotalesPorMes() {
        Map<YearMonth,Map<Rubro,Double>> r=new TreeMap<>();
        totalesPorMes.forEach((k,v)->r.put(k,Collections.unmodifiableMap(v)));
        return Collections.unmodifiableMap(r);
    }
    public int getTransacciones() {
        return transacciones;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static double redondearDinero(double valor) {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
