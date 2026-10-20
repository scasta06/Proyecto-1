package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import modelo.deportes.Deporte;

public class NivelDeportivo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private Deporte deporte;
    private int nivel;
    private LocalDate fechaActualizacion;
    public NivelDeportivo(Socio s,Deporte d,int n) throws ClubException
    {
        validar(n>=1&&n<=3,"Nivel fuera de 1..3");
        socio=s;
        deporte=d;
        nivel=n;
        fechaActualizacion=LocalDate.now();
    }
    public int diferenciaCon(NivelDeportivo otro) throws ClubException
    {
        validar(deporte==otro.deporte,"Disciplinas distintas");
        return Math.abs(nivel-otro.nivel);
    }
    public int getNivel()
    {
        return nivel;
    }
    public LocalDate getFechaActualizacion()
    {
        return fechaActualizacion;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
