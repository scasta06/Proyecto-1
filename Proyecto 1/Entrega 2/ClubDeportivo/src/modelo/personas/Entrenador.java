package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.deportes.Deporte;

public class Entrenador extends Empleado implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String licencia;
    private List<Deporte> especialidades=new ArrayList<>();
    public Entrenador(String doc,String nom,LocalDate nac,String login,String clave,String codigo,String dato) throws ClubException
    {
        super(doc,nom,nac,login,clave,codigo);
        licencia=textoRequerido(dato);
    }
    @Override
    public String getTipo()
    {
        return "ENTRENADOR";
    }
    public String getLicencia()
    {
        return licencia;
    }
    public void agregarEspecialidad(Deporte d)
    {
        if(!especialidades.contains(d))especialidades.add(d);
    }
    public boolean puedeEntrenar(Deporte d)
    {
        return especialidades.contains(d);
    }
    public List<Deporte> getEspecialidades()
    {
        return copiaLectura(especialidades);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
