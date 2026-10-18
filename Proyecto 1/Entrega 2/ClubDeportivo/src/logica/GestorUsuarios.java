package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.deportes.Deporte;
import modelo.personas.Empleado;
import modelo.personas.Entrenador;
import modelo.personas.Fisioterapeuta;
import modelo.personas.Persona;
import modelo.personas.Sancion;
import modelo.personas.Socio;
import modelo.tipos.TipoSancion;

/** Registra personas, autentica sus credenciales y administra sanciones. */
public class GestorUsuarios implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Map<String,Persona> personas=new LinkedHashMap<>();
    public Persona autenticar(String login,String clave) throws ClubException
    {
        for (Persona persona : personas.values())
        {
            boolean habilitado = !(persona instanceof Socio) || ((Socio) persona).isActivo();
            if (habilitado && persona.getLogin().equals(login) && persona.verificarContrasena(clave))
            {
                return persona;
            }
        }
        throw new ClubException("Credenciales inválidas");
    }
    private void registrar(Persona p) throws ClubException
    {
        validar(!personas.containsKey(p.getDocumento()), "Documento duplicado");
        for (Persona registrada : personas.values())
        {
            validar(!registrada.getLogin().equals(p.getLogin()), "Login duplicado");
        }
        personas.put(p.getDocumento(), p);
    }
    public void registrarSocio(Socio s) throws ClubException
    {
        registrar(s);
    }
    public void registrarEmpleado(Empleado e) throws ClubException
    {
        registrar(e);
    }
    public Socio buscarSocio(String d) throws ClubException
    {
        Persona p=personas.get(d);
        if(!(p instanceof Socio))throw new ClubException("Socio no encontrado");
        return (Socio)p;
    }
    public List<Entrenador> getEntrenadores()
    {
        List<Entrenador> resultado = new ArrayList<Entrenador>();
        for (Persona persona : personas.values())
        {
            if (persona instanceof Entrenador)
            {
                resultado.add((Entrenador) persona);
            }
        }
        return resultado;
    }
    public List<Fisioterapeuta> getFisioterapeutas()
    {
        List<Fisioterapeuta> resultado = new ArrayList<Fisioterapeuta>();
        for (Persona persona : personas.values())
        {
            if (persona instanceof Fisioterapeuta)
            {
                resultado.add((Fisioterapeuta) persona);
            }
        }
        return resultado;
    }
    public Sancion sancionar(Socio s,String motivo,TipoSancion t,LocalDate ini,LocalDate fin,Deporte d) throws ClubException
    {
        Sancion x=new Sancion(s,motivo,t,ini,fin,d);
        s.agregarSancion(x);
        return x;
    }
    public List<Persona> getPersonas()
    {
        return copiaLectura(personas.values());
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
