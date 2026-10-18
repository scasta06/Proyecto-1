package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Empleado extends Persona implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigoEmpleado;
    private LocalDate fechaContratacion;
    protected Empleado(String doc,String nom,LocalDate nac,String login,String clave,String codigo) throws ClubException
    {
        super(doc,nom,nac,login,clave);
        codigoEmpleado=textoRequerido(codigo);
        fechaContratacion=LocalDate.now();
    }
    @Override
    public String getTipo()
    {
        return "EMPLEADO";
    }
    public String getCodigoEmpleado()
    {
        return codigoEmpleado;
    }
    public LocalDate getFechaContratacion()
    {
        return fechaContratacion;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
