package modelo.inventario;
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
public class Ubicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo,nombre;
    private TipoUbicacion tipo;
    public Ubicacion(String c,String n,TipoUbicacion t) {
        codigo=Validacion.texto(c);
        nombre=Validacion.texto(n);
        tipo=t;
    }
    public String getCodigo() {
        return codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public TipoUbicacion getTipo() {
        return tipo;
    }
}
