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
public class GestorPersonal implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Turno> turnos=new ArrayList<>();
    private List<SolicitudCambioTurno> solicitudes=new ArrayList<>();
    public static final int MIN_ENTRENADORES=2,MIN_FISIOTERAPEUTAS=1;
    private LocalTime horaApertura,horaCierre;
    public GestorPersonal(LocalTime ini,LocalTime fin) {
        horaApertura=ini;
        horaCierre=fin;
    }
    public void asignarTurno(Turno t) {
        validar(turnos.stream().noneMatch(x->x.getId().equals(t.getId())||(x.getEmpleado()==t.getEmpleado()&&x.seTraslapaCon(t))),"Turno duplicado o superpuesto");
        turnos.add(t);
    }
    public SolicitudCambioTurno solicitarCambio(Empleado e,Turno actual,Turno propuesto,String motivo) {
        validar(turnos.contains(actual)&&actual.getEmpleado()==e&&propuesto.getEmpleado()==e,"Turno de otro empleado");
        SolicitudCambioTurno s=new SolicitudCambioTurno(e,actual,propuesto,motivo);
        solicitudes.add(s);
        return s;
    }
    public void aprobarCambio(SolicitudCambioTurno s,Administrador a) {
        validar(solicitudes.contains(s)&&s.getEstado()==EstadoSolicitud.PENDIENTE&&turnos.contains(s.getTurnoActual()),"Solicitud inválida");
        List<Turno> nuevos=new ArrayList<>(turnos);
        nuevos.remove(s.getTurnoActual());
        Turno p=s.getTurnoPropuesto();
        validar(nuevos.stream().noneMatch(t->t.getEmpleado()==p.getEmpleado()&&t.seTraslapaCon(p)),"Turnos superpuestos");
        nuevos.add(p);
        if(!coberturaMinimaCumplida(nuevos,s.getTurnoActual().getFecha())||!coberturaMinimaCumplida(nuevos,p.getFecha()))throw new TurnoException("El cambio deja al club sin cobertura mínima");
        s.aprobar(a,LocalDate.now());
        turnos=nuevos;
    }
    public void rechazarCambio(SolicitudCambioTurno s,Administrador a,String obs) {
        validar(solicitudes.contains(s),"Solicitud desconocida");
        s.rechazar(a,obs,LocalDate.now());
    }
    public boolean coberturaMinimaCumplida(List<Turno> lista,LocalDate f) {
        Set<LocalTime> cortes=new TreeSet<>();
        cortes.add(horaApertura);
        for(Turno t:lista)if(t.getFecha().equals(f)) {
            if(!t.getHoraInicio().isBefore(horaApertura)&&t.getHoraInicio().isBefore(horaCierre))cortes.add(t.getHoraInicio());
            if(!t.getHoraFin().isBefore(horaApertura)&&t.getHoraFin().isBefore(horaCierre))cortes.add(t.getHoraFin());
        }
        for(LocalTime h:cortes)if(contarDisponibles(lista,f,h,Entrenador.class)<MIN_ENTRENADORES||contarDisponibles(lista,f,h,Fisioterapeuta.class)<MIN_FISIOTERAPEUTAS)return false;
        return true;
    }
    private int contarDisponibles(List<Turno> lista,LocalDate f,LocalTime h,Class<?> tipo) {
        return (int)lista.stream().filter(t->t.cubre(f,h)&&tipo.isInstance(t.getEmpleado())).map(Turno::getEmpleado).distinct().count();
    }
    public List<Turno> getTurnos() {
        return copiaLectura(turnos);
    }
    public List<SolicitudCambioTurno> getSolicitudes() {
        return copiaLectura(solicitudes);
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
