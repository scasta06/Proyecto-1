package modelo.personal;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import modelo.personas.Administrador;
import modelo.personas.Empleado;
import modelo.tipos.EstadoSolicitud;

public class SolicitudCambioTurno implements Serializable
{
    private static final long serialVersionUID = 1L;
    private LocalDate fechaSolicitud=LocalDate.now(),fechaRespuesta;
    private String motivo;
    private String observacion;
    private EstadoSolicitud estado=EstadoSolicitud.PENDIENTE;
    private Empleado solicitante;
    private Turno turnoActual,turnoPropuesto;
    private Administrador revisadaPor;
    public SolicitudCambioTurno(Empleado e,Turno a,Turno p,String motivo) throws ClubException
    {
        solicitante=e;
        turnoActual=a;
        turnoPropuesto=p;
        this.motivo=textoRequerido(motivo);
    }
    public void aprobar(Administrador a,LocalDate f) throws ClubException
    {
        responder(a,f,EstadoSolicitud.APROBADA,"");
    }
    public void rechazar(Administrador a,String obs,LocalDate f) throws ClubException
    {
        responder(a,f,EstadoSolicitud.RECHAZADA,textoRequerido(obs));
    }
    private void responder(Administrador a,LocalDate f,EstadoSolicitud e,String obs) throws ClubException
    {
        validar(estado==EstadoSolicitud.PENDIENTE,"Solicitud ya respondida");
        revisadaPor=Objects.requireNonNull(a);
        fechaRespuesta=f;
        estado=e;
        observacion=obs;
    }
    public Turno getTurnoActual()
    {
        return turnoActual;
    }
    public Turno getTurnoPropuesto()
    {
        return turnoPropuesto;
    }
    public Empleado getSolicitante()
    {
        return solicitante;
    }
    public EstadoSolicitud getEstado()
    {
        return estado;
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
}
