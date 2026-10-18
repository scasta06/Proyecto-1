package modelo.comercio;
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
public class Ingrediente implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private List<Alergeno> alergenos;
    public Ingrediente(String n,List<Alergeno> a) {
        nombre=textoRequerido(n);
        alergenos=new ArrayList<>(a);
    }
    public String getNombre() {
        return nombre;
    }
    public List<Alergeno> getAlergenos() {
        return copiaLectura(alergenos);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
