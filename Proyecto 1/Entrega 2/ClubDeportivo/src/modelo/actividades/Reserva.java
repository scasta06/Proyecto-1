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
public class Reserva extends Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private int cantidadJugadores;
    private ModalidadReserva modalidad;
    private EstadoReserva estado=EstadoReserva.CONFIRMADA;
    public Reserva(String id,Socio s,LocalDate f,LocalTime h,int dur,Instalacion i,int n,ModalidadReserva m) {
        super(id,f,h,dur,i);
        socio=s;
        cantidadJugadores=n;
        modalidad=m;
    }
    public void cancelar() {
        if(estado!=EstadoReserva.CONFIRMADA)throw new ReservaException("Reserva no confirmada");
        estado=EstadoReserva.CANCELADA;
        instalacion.liberar(this);
    }
    public boolean bloqueaInstalacion() {
        return estado==EstadoReserva.CONFIRMADA;
    }
    public Socio getSocio() {
        return socio;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public int getCantidadJugadores() {
        return cantidadJugadores;
    }
    public ModalidadReserva getModalidad() {
        return modalidad;
    }
}
