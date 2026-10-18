package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public abstract class Evento implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int duracionMinutos;
    private Instalacion instalacion;
    protected Evento(String id,LocalDate f,LocalTime h,int dur,Instalacion i) throws ClubException
    {
        this.id=textoRequerido(id);
        fecha=Objects.requireNonNull(f);
        horaInicio=Objects.requireNonNull(h);
        duracionMinutos=enteroPositivo(dur);
        validar(dur<=1440&&h.toSecondOfDay()+dur*60<=86400,"El evento cruza medianoche");
        instalacion=i;
    }
    public LocalTime getHoraFin()
    {
        return horaInicio.plusMinutes(duracionMinutos);
    }
    public boolean seTraslapaCon(LocalDate f,LocalTime ini,LocalTime fin)
    {
        return fecha.equals(f)&&horaInicio.isBefore(fin)&&ini.isBefore(getHoraFin());
    }
    public abstract boolean bloqueaInstalacion();
    public String getId()
    {
        return id;
    }
    public LocalDate getFecha()
    {
        return fecha;
    }
    public LocalTime getHoraInicio()
    {
        return horaInicio;
    }
    public Instalacion getInstalacion()
    {
        return instalacion;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
}
