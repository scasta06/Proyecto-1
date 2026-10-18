package modelo.personas;
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
public class NivelDeportivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private Deporte deporte;
    private int nivel;
    private LocalDate fechaActualizacion;
    public NivelDeportivo(Socio s,Deporte d,int n) {
        Validacion.exigir(n>=1&&n<=10,"Nivel fuera de 1..10");
        socio=s;
        deporte=d;
        nivel=n;
        fechaActualizacion=LocalDate.now();
    }
    public int diferenciaCon(NivelDeportivo otro) {
        Validacion.exigir(deporte==otro.deporte,"Disciplinas distintas");
        return Math.abs(nivel-otro.nivel);
    }
    public int getNivel() {
        return nivel;
    }
    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }
}
