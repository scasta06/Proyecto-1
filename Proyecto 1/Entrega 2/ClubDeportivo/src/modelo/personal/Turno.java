package modelo.personal;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import modelo.personas.Empleado;

public class Turno implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Empleado empleado;
    public Turno(String id,LocalDate f,LocalTime ini,LocalTime fin,Empleado e) throws ClubException
    {
        validar(ini.isBefore(fin),"Horario de turno inválido");
        this.id=id;
        fecha=f;
        horaInicio=ini;
        horaFin=fin;
        empleado=e;
    }
    public boolean cubre(LocalDate f,LocalTime h)
    {
        return fecha.equals(f)&&!h.isBefore(horaInicio)&&h.isBefore(horaFin);
    }
    public boolean seTraslapaCon(Turno otro)
    {
        return fecha.equals(otro.fecha)&&horaInicio.isBefore(otro.horaFin)&&otro.horaInicio.isBefore(horaFin);
    }
    public LocalDate getFecha()
    {
        return fecha;
    }
    public LocalTime getHoraInicio()
    {
        return horaInicio;
    }
    public LocalTime getHoraFin()
    {
        return horaFin;
    }
    public Empleado getEmpleado()
    {
        return empleado;
    }
    public String getId()
    {
        return id;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
