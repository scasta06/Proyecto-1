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
public class InscripcionAcademia implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio alumno;
    private Academia academia;
    private LocalDate fechaInscripcion;
    private boolean activa=true;
    public InscripcionAcademia(Socio s,Academia a,LocalDate f) {
        alumno=s;
        academia=a;
        fechaInscripcion=f;
    }
    public void retirar() {
        activa=false;
    }
    public boolean isActiva() {
        return activa;
    }
    public Socio getAlumno() {
        return alumno;
    }
    public Academia getAcademia() {
        return academia;
    }
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
}
