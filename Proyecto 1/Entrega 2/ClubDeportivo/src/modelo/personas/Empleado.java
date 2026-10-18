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
public abstract class Empleado extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigoEmpleado;
    private LocalDate fechaContratacion;
    private String codigoDescuento;
    protected Empleado(String doc,String nom,LocalDate nac,String login,String clave,String codigo) {
        super(doc,nom,nac,login,clave);
        codigoEmpleado=Validacion.texto(codigo);
        codigoDescuento="EMP-"+codigo;
        fechaContratacion=LocalDate.now();
    }
    public String getTipo() {
        return "EMPLEADO";
    }
    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }
    public String getCodigoDescuento() {
        return codigoDescuento;
    }
    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }
}
