package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import modelo.tipos.Rubro;

public class ReporteVentas implements Serializable
{
    private static final long serialVersionUID = 1L;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Map<Rubro,Double> totalesPorRubro=new EnumMap<>(Rubro.class);
    private Map<YearMonth,Map<Rubro,Double>> totalesPorMes=new TreeMap<>();
    private int transacciones;
    public ReporteVentas(LocalDate ini,LocalDate fin) throws ClubException
    {
        validar(!fin.isBefore(ini),"Periodo invertido");
        fechaInicio=ini;
        fechaFin=fin;
    }
    public void agregar(Rubro r,YearMonth mes,double valor)
    {
        Double acumulado = totalesPorRubro.get(r);
        if (acumulado == null)
        {
            acumulado = 0.0;
        }
        totalesPorRubro.put(r, acumulado + valor);
        Map<Rubro, Double> totalesMes = totalesPorMes.get(mes);
        if (totalesMes == null)
        {
            totalesMes = new HashMap<Rubro, Double>();
            totalesPorMes.put(mes, totalesMes);
        }
        Double acumuladoMes = totalesMes.get(r);
        if (acumuladoMes == null)
        {
            acumuladoMes = 0.0;
        }
        totalesMes.put(r, acumuladoMes + valor);
        transacciones++;
    }
    public double getTotal() throws ClubException
    {
        double total = 0;
        for (Double valor : totalesPorRubro.values())
        {
            total += valor;
        }
        return redondearDinero(total);
    }
    public boolean estaVacio()
    {
        return transacciones==0;
    }
    public Map<Rubro,Double> getTotalesPorRubro()
    {
        return new HashMap<Rubro, Double>(totalesPorRubro);
    }
    public Map<YearMonth,Map<Rubro,Double>> getTotalesPorMes()
    {
        Map<YearMonth, Map<Rubro, Double>> resultado = new TreeMap<YearMonth, Map<Rubro, Double>>();
        for (YearMonth mes : totalesPorMes.keySet())
        {
            resultado.put(mes, new HashMap<Rubro, Double>(totalesPorMes.get(mes)));
        }
        return resultado;
    }
    public int getTransacciones()
    {
        return transacciones;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
