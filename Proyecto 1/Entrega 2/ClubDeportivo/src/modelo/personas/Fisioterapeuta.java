package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;

public class Fisioterapeuta extends Empleado implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String tarjetaProfesional;
    public Fisioterapeuta(String doc,String nom,LocalDate nac,String login,String clave,String codigo,String dato) throws ClubException
    {
        super(doc,nom,nac,login,clave,codigo);
        tarjetaProfesional=textoRequerido(dato);
    }
    @Override
    public String getTipo()
    {
        return "FISIOTERAPEUTA";
    }
    public String getTarjetaProfesional()
    {
        return tarjetaProfesional;
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
