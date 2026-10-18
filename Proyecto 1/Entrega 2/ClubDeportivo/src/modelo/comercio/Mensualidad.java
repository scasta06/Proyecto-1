package modelo.comercio;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import modelo.deportes.Academia;
import modelo.personas.Socio;
import modelo.tipos.ConceptoMensualidad;
import modelo.tipos.Rubro;

public class Mensualidad implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private Academia academia;
    private YearMonth periodo;
    private ConceptoMensualidad concepto;
    private double valor;
    private LocalDate fechaPago=LocalDate.now();
    public Mensualidad(Socio s,YearMonth p,ConceptoMensualidad c,double v) throws ClubException
    {
        socio=s;
        periodo=p;
        concepto=c;
        valor=redondearDinero(v);
    }
    public Rubro getRubro()
    {
        return Rubro.MENSUALIDADES;
    }
    public Socio getSocio()
    {
        return socio;
    }
    public YearMonth getPeriodo()
    {
        return periodo;
    }
    public ConceptoMensualidad getConcepto()
    {
        return concepto;
    }
    public double getValor()
    {
        return valor;
    }
    public LocalDate getFechaPago()
    {
        return fechaPago;
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
