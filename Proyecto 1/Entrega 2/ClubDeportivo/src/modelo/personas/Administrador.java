package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;

public class Administrador extends Empleado implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String cargo;
    public Administrador(String doc,String nom,LocalDate nac,String login,String clave,String codigo,String dato) throws ClubException
    {
        super(doc,nom,nac,login,clave,codigo);
        cargo=textoRequerido(dato);
    }
    @Override
    public String getTipo()
    {
        return "ADMINISTRADOR";
    }
    public String getCargo()
    {
        return cargo;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
