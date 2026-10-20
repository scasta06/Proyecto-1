package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import modelo.personas.Socio;
import modelo.tipos.EstadoAsistencia;

public class RegistroAsistencia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio jugador;
    private EstadoAsistencia estado;
    private Integer notaDesempeno;
    private String comentario;
    public RegistroAsistencia(Socio s,EstadoAsistencia e,Integer nota,String com) throws ClubException
    {
        validar(nota==null||(nota>=1&&nota<=5),"Nota fuera de 1..5");
        jugador=s;
        validar(e != null && s != null,"Jugador y estado de asistencia obligatorios");
        estado=e;
        notaDesempeno=nota;
        comentario=com;
    }
    public boolean tieneNota()
    {
        return notaDesempeno!=null;
    }
    public Socio getJugador()
    {
        return jugador;
    }
    public EstadoAsistencia getEstado()
    {
        return estado;
    }
    public Integer getNotaDesempeno()
    {
        return notaDesempeno;
    }
    public String getComentario()
    {
        return comentario;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ParticipacionException(mensaje);
    }
}
