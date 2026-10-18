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
public class Mensualidad implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private Academia academia;
    private YearMonth periodo;
    private ConceptoMensualidad concepto;
    private double valor;
    private LocalDate fechaPago=LocalDate.now();
    public Mensualidad(Socio s,YearMonth p,ConceptoMensualidad c,double v) {
        socio=s;
        periodo=p;
        concepto=c;
        valor=Validacion.dinero(v);
    }
    public Rubro getRubro() {
        return Rubro.MENSUALIDADES;
    }
    public Socio getSocio() {
        return socio;
    }
    public YearMonth getPeriodo() {
        return periodo;
    }
    public ConceptoMensualidad getConcepto() {
        return concepto;
    }
    public double getValor() {
        return valor;
    }
    public LocalDate getFechaPago() {
        return fechaPago;
    }
}
