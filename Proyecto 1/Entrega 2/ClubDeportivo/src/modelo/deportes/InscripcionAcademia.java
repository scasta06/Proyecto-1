package modelo.deportes;

import java.io.Serializable;
import java.time.LocalDate;
import modelo.personas.Socio;

public class InscripcionAcademia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio alumno;
    private Academia academia;
    private LocalDate fechaInscripcion;
    private boolean activa=true;
    public InscripcionAcademia(Socio s,Academia a,LocalDate f)
    {
        alumno=s;
        academia=a;
        fechaInscripcion=f;
    }
    public void retirar()
    {
        activa=false;
    }
    public boolean isActiva()
    {
        return activa;
    }
    public Socio getAlumno()
    {
        return alumno;
    }
    public Academia getAcademia()
    {
        return academia;
    }
    public LocalDate getFechaInscripcion()
    {
        return fechaInscripcion;
    }
}
