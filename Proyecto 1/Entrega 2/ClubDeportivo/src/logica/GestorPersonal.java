package logica;

import excepciones.ClubException;
import excepciones.TurnoException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Turno;
import modelo.personas.Administrador;
import modelo.personas.Empleado;
import modelo.personas.Entrenador;
import modelo.personas.Fisioterapeuta;
import modelo.tipos.EstadoSolicitud;

/** Administra turnos y revisa la cobertura antes de aprobar cambios. */
public class GestorPersonal implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Turno> turnos=new ArrayList<>();
    private List<SolicitudCambioTurno> solicitudes=new ArrayList<>();
    public static final int MIN_ENTRENADORES=2,MIN_FISIOTERAPEUTAS=1;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    public GestorPersonal(LocalTime ini,LocalTime fin)
    {
        horaApertura=ini;
        horaCierre=fin;
    }
    public void asignarTurno(Turno t) throws ClubException
    {
        for (Turno anterior : turnos)
        {
            validar(!anterior.getId().equals(t.getId()) && !(anterior.getEmpleado() == t.getEmpleado() && anterior.seTraslapaCon(t)), "Turno duplicado o superpuesto");
        }
        turnos.add(t);
    }
    public SolicitudCambioTurno solicitarCambio(Empleado e,Turno actual,Turno propuesto,String motivo) throws ClubException
    {
        validar(turnos.contains(actual)&&actual.getEmpleado()==e&&propuesto.getEmpleado()==e,"Turno de otro empleado");
        SolicitudCambioTurno s=new SolicitudCambioTurno(e,actual,propuesto,motivo);
        solicitudes.add(s);
        return s;
    }
    public void aprobarCambio(SolicitudCambioTurno s,Administrador a) throws ClubException
    {
        validar(solicitudes.contains(s)&&s.getEstado()==EstadoSolicitud.PENDIENTE&&turnos.contains(s.getTurnoActual()),"Solicitud inválida");
        List<Turno> nuevos=new ArrayList<>(turnos);
        nuevos.remove(s.getTurnoActual());
        Turno p=s.getTurnoPropuesto();
        for (Turno turno : nuevos)
        {
            validar(!turno.getId().equals(p.getId()) && !(turno.getEmpleado() == p.getEmpleado() && turno.seTraslapaCon(p)), "Turno duplicado o superpuesto");
        }
        nuevos.add(p);
        if(!coberturaMinimaCumplida(nuevos,s.getTurnoActual().getFecha())||!coberturaMinimaCumplida(nuevos,p.getFecha()))throw new TurnoException("El cambio deja al club sin cobertura mínima");
        s.aprobar(a,LocalDate.now());
        turnos=nuevos;
    }
    public void rechazarCambio(SolicitudCambioTurno s,Administrador a,String obs) throws ClubException
    {
        validar(solicitudes.contains(s),"Solicitud desconocida");
        s.rechazar(a,obs,LocalDate.now());
    }
    public boolean coberturaMinimaCumplida(List<Turno> lista,LocalDate f)
    {
        Set<LocalTime> cortes=new TreeSet<>();
        cortes.add(horaApertura);
        for(Turno t:lista)if(t.getFecha().equals(f))
        {
            if(!t.getHoraInicio().isBefore(horaApertura)&&t.getHoraInicio().isBefore(horaCierre))cortes.add(t.getHoraInicio());
            if(!t.getHoraFin().isBefore(horaApertura)&&t.getHoraFin().isBefore(horaCierre))cortes.add(t.getHoraFin());
        }
        for(LocalTime h:cortes)if(contarDisponibles(lista,f,h,Entrenador.class)<MIN_ENTRENADORES||contarDisponibles(lista,f,h,Fisioterapeuta.class)<MIN_FISIOTERAPEUTAS)return false;
        return true;
    }
    private int contarDisponibles(List<Turno> lista,LocalDate f,LocalTime h,Class<?> tipo)
    {
        List<Empleado> contados = new ArrayList<Empleado>();
        for (Turno turno : lista)
        {
            Empleado empleado = turno.getEmpleado();
            if (turno.cubre(f, h) && tipo.isInstance(empleado) && !contados.contains(empleado))
            {
                contados.add(empleado);
            }
        }
        return contados.size();
    }
    public List<Turno> getTurnos()
    {
        return copiaLectura(turnos);
    }
    public List<SolicitudCambioTurno> getSolicitudes()
    {
        return copiaLectura(solicitudes);
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new TurnoException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
