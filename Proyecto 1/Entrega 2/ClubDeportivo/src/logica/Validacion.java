package logica;
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
public final class Validacion {
    private Validacion() {
    }
    public static void exigir(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    public static String texto(String v) {
        exigir(v!=null&&!v.isBlank(),"Texto obligatorio");
        return v.trim();
    }
    public static int positivo(int v) {
        exigir(v>0,"Debe ser positivo");
        return v;
    }
    public static double dinero(double v) {
        exigir(Double.isFinite(v)&&v>=0,"Valor monetario inválido");
        return Math.round(v*100.0)/100.0;
    }
    public static <T> List<T> copia(Collection<T> c) {
        return Collections.unmodifiableList(new ArrayList<>(c));
    }
}
