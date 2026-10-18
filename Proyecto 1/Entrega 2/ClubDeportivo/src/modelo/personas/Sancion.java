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
public class Sancion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private String motivo;
    private TipoSancion tipo;
    private LocalDate fechaInicio,fechaFin;
    private Deporte deporte;
    public Sancion(Socio s,String m,TipoSancion t,LocalDate ini,LocalDate fin,Deporte d) {
        Validacion.exigir(!fin.isBefore(ini),"Fechas de sanción invertidas");
        socio=Objects.requireNonNull(s);
        motivo=Validacion.texto(m);
        tipo=Objects.requireNonNull(t);
        fechaInicio=ini;
        fechaFin=fin;
        deporte=d;
        Validacion.exigir(t!=TipoSancion.SUSPENSION_DEPORTIVA||d!=null,"Falta deporte");
    }
    public boolean estaVigente(LocalDate f) {
        return tipo!=TipoSancion.AMONESTACION&&!f.isBefore(fechaInicio)&&!f.isAfter(fechaFin);
    }
    public boolean aplicaA(Deporte d) {
        return tipo==TipoSancion.SUSPENSION_TOTAL||deporte==d;
    }
    public Socio getSocio() {
        return socio;
    }
    public String getMotivo() {
        return motivo;
    }
}
