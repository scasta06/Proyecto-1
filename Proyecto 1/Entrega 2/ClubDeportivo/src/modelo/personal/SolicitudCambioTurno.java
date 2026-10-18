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
public class SolicitudCambioTurno implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate fechaSolicitud=LocalDate.now(),fechaRespuesta;
    private String motivo,observacion;
    private EstadoSolicitud estado=EstadoSolicitud.PENDIENTE;
    private Empleado solicitante;
    private Turno turnoActual,turnoPropuesto;
    private Administrador revisadaPor;
    public SolicitudCambioTurno(Empleado e,Turno a,Turno p,String motivo) {
        solicitante=e;
        turnoActual=a;
        turnoPropuesto=p;
        this.motivo=textoRequerido(motivo);
    }
    public void aprobar(Administrador a,LocalDate f) {
        responder(a,f,EstadoSolicitud.APROBADA,"");
    }
    public void rechazar(Administrador a,String obs,LocalDate f) {
        responder(a,f,EstadoSolicitud.RECHAZADA,textoRequerido(obs));
    }
    private void responder(Administrador a,LocalDate f,EstadoSolicitud e,String obs) {
        validar(estado==EstadoSolicitud.PENDIENTE,"Solicitud ya respondida");
        revisadaPor=Objects.requireNonNull(a);
        fechaRespuesta=f;
        estado=e;
        observacion=obs;
    }
    public Turno getTurnoActual() {
        return turnoActual;
    }
    public Turno getTurnoPropuesto() {
        return turnoPropuesto;
    }
    public Empleado getSolicitante() {
        return solicitante;
    }
    public EstadoSolicitud getEstado() {
        return estado;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
