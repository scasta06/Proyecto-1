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
public class RegistroAsistencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio jugador;
    private EstadoAsistencia estado;
    private Integer notaDesempeno;
    private String comentario;
    public RegistroAsistencia(Socio s,EstadoAsistencia e,Integer nota,String com) {
        Validacion.exigir(nota==null||(nota>=1&&nota<=5),"Nota fuera de 1..5");
        jugador=s;
        estado=Objects.requireNonNull(e);
        notaDesempeno=nota;
        comentario=com;
    }
    public boolean tieneNota() {
        return notaDesempeno!=null;
    }
    public Socio getJugador() {
        return jugador;
    }
    public EstadoAsistencia getEstado() {
        return estado;
    }
    public Integer getNotaDesempeno() {
        return notaDesempeno;
    }
    public String getComentario() {
        return comentario;
    }
}
