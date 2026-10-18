package modelo.deportes;
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
public class InscripcionEquipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio jugador;
    private Equipo equipo;
    private LocalDate fechaInscripcion;
    private String posicion;
    private int numeroCamiseta;
    private boolean activa=true;
    public InscripcionEquipo(Socio s,Equipo e,String p,int n,LocalDate f) {
        jugador=s;
        equipo=e;
        posicion=p;
        numeroCamiseta=n;
        fechaInscripcion=f;
    }
    public void retirar() {
        activa=false;
    }
    public boolean isActiva() {
        return activa;
    }
    public Socio getJugador() {
        return jugador;
    }
    public Equipo getEquipo() {
        return equipo;
    }
    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }
    public String getPosicion() {
        return posicion;
    }
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
}
