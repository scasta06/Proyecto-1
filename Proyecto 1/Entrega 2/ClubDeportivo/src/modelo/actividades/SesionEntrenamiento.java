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
public class SesionEntrenamiento extends Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String objetivo="Entrenamiento";
    private boolean cancelada;
    private Entrenador entrenador;
    private List<Socio> convocados;
    private Map<Socio,RegistroAsistencia> registros=new LinkedHashMap<>();
    public SesionEntrenamiento(String id,LocalDate f,LocalTime h,int dur,Instalacion i,Entrenador en,List<Socio> socios) {
        super(id,f,h,dur,i);
        entrenador=en;
        convocados=new ArrayList<>(socios);
    }
    public boolean estaConvocado(Socio s) {
        return convocados.contains(s);
    }
    public RegistroAsistencia registrarAsistencia(Socio s,EstadoAsistencia e,Integer nota,String com) {
        Validacion.exigir(!cancelada&&estaConvocado(s),"Jugador no convocado o sesión cancelada");
        RegistroAsistencia r=new RegistroAsistencia(s,e,nota,com);
        registros.put(s,r);
        return r;
    }
    public boolean bloqueaInstalacion() {
        return !cancelada;
    }
    public Entrenador getEntrenador() {
        return entrenador;
    }
    public List<Socio> getConvocados() {
        return Validacion.copia(convocados);
    }
    public List<RegistroAsistencia> getRegistros() {
        return Validacion.copia(registros.values());
    }
    public void cancelar() {
        cancelada=true;
        instalacion.liberar(this);
    }
    public void setObjetivo(String o) {
        objetivo=Validacion.texto(o);
    }
}
