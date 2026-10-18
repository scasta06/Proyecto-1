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
public class Fisioterapeuta extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tarjetaProfesional;
    public Fisioterapeuta(String doc,String nom,LocalDate nac,String login,String clave,String codigo,String dato) {
        super(doc,nom,nac,login,clave,codigo);
        tarjetaProfesional=textoRequerido(dato);
    }
    public String getTipo() {
        return "FISIOTERAPEUTA";
    }
    public String getTarjetaProfesional() {
        return tarjetaProfesional;
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
