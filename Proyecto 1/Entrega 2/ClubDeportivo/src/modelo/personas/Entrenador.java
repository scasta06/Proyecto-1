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
public class Entrenador extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    private String licencia;
    private List<Deporte> especialidades=new ArrayList<>();
    public Entrenador(String doc,String nom,LocalDate nac,String login,String clave,String codigo,String dato) {
        super(doc,nom,nac,login,clave,codigo);
        licencia=Validacion.texto(dato);
    }
    public String getTipo() {
        return "ENTRENADOR";
    }
    public String getLicencia() {
        return licencia;
    }
    public void agregarEspecialidad(Deporte d) {
        if(!especialidades.contains(d))especialidades.add(d);
    }
    public boolean puedeEntrenar(Deporte d) {
        return especialidades.contains(d);
    }
    public List<Deporte> getEspecialidades() {
        return Validacion.copia(especialidades);
    }
}
