package modelo.actividades;
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
public abstract class Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String id;
    protected LocalDate fecha;
    protected LocalTime horaInicio;
    protected int duracionMinutos;
    protected Instalacion instalacion;
    protected Evento(String id,LocalDate f,LocalTime h,int dur,Instalacion i) {
        this.id=textoRequerido(id);
        fecha=Objects.requireNonNull(f);
        horaInicio=Objects.requireNonNull(h);
        duracionMinutos=enteroPositivo(dur);
        validar(dur<=1440&&h.toSecondOfDay()+dur*60<=86400,"El evento cruza medianoche");
        instalacion=i;
    }
    public LocalTime getHoraFin() {
        return horaInicio.plusMinutes(duracionMinutos);
    }
    public boolean seTraslapaCon(LocalDate f,LocalTime ini,LocalTime fin) {
        return fecha.equals(f)&&horaInicio.isBefore(fin)&&ini.isBefore(getHoraFin());
    }
    public abstract boolean bloqueaInstalacion();
    public String getId() {
        return id;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public Instalacion getInstalacion() {
        return instalacion;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static int enteroPositivo(int valor) {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
}
