package modelo.personal;
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
public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private LocalDate fecha;
    private LocalTime horaInicio,horaFin;
    private Empleado empleado;
    public Turno(String id,LocalDate f,LocalTime ini,LocalTime fin,Empleado e) {
        Validacion.exigir(ini.isBefore(fin),"Horario de turno inválido");
        this.id=id;
        fecha=f;
        horaInicio=ini;
        horaFin=fin;
        empleado=e;
    }
    public boolean cubre(LocalDate f,LocalTime h) {
        return fecha.equals(f)&&!h.isBefore(horaInicio)&&h.isBefore(horaFin);
    }
    public boolean seTraslapaCon(Turno otro) {
        return fecha.equals(otro.fecha)&&horaInicio.isBefore(otro.horaFin)&&otro.horaInicio.isBefore(horaFin);
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public LocalTime getHoraFin() {
        return horaFin;
    }
    public Empleado getEmpleado() {
        return empleado;
    }
    public String getId() {
        return id;
    }
}
