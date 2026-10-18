package modelo.actividades;

import excepciones.ClubException;
import excepciones.ReservaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import modelo.personas.Socio;
import modelo.tipos.EstadoReserva;
import modelo.tipos.ModalidadReserva;

public class Reserva extends Evento implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private int cantidadJugadores;
    private ModalidadReserva modalidad;
    private EstadoReserva estado=EstadoReserva.CONFIRMADA;
    public Reserva(String id,Socio s,LocalDate f,LocalTime h,int dur,Instalacion i,int n,ModalidadReserva m) throws ClubException
    {
        super(id,f,h,dur,i);
        socio=s;
        cantidadJugadores=n;
        modalidad=m;
    }
    public void cancelar() throws ClubException
    {
        if(estado!=EstadoReserva.CONFIRMADA)throw new ReservaException("Reserva no confirmada");
        estado=EstadoReserva.CANCELADA;
        getInstalacion().liberar(this);
    }
    @Override
    public boolean bloqueaInstalacion()
    {
        return estado==EstadoReserva.CONFIRMADA;
    }
    public Socio getSocio()
    {
        return socio;
    }
    public EstadoReserva getEstado()
    {
        return estado;
    }
    public int getCantidadJugadores()
    {
        return cantidadJugadores;
    }
    public ModalidadReserva getModalidad()
    {
        return modalidad;
    }
}
