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
public class GestorUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String,Persona> personas=new LinkedHashMap<>();
    public Persona autenticar(String login,String clave) {
        return personas.values().stream().filter(p->p.getLogin().equals(login)&&p.verificarContrasena(clave)&&(!(p instanceof Socio)||((Socio)p).isActivo())).findFirst().orElseThrow(()->new ClubException("Credenciales inválidas"));
    }
    private void registrar(Persona p) {
        validar(!personas.containsKey(p.getDocumento())&&personas.values().stream().noneMatch(x->x.getLogin().equals(p.getLogin())),"Documento o login duplicado");
        personas.put(p.getDocumento(),p);
    }
    public void registrarSocio(Socio s) {
        registrar(s);
    }
    public void registrarEmpleado(Empleado e) {
        registrar(e);
    }
    public Socio buscarSocio(String d) {
        Persona p=personas.get(d);
        if(!(p instanceof Socio))throw new ClubException("Socio no encontrado");
        return (Socio)p;
    }
    public List<Entrenador> getEntrenadores() {
        return personas.values().stream().filter(p->p instanceof Entrenador).map(p->(Entrenador)p).collect(Collectors.toUnmodifiableList());
    }
    public List<Fisioterapeuta> getFisioterapeutas() {
        return personas.values().stream().filter(p->p instanceof Fisioterapeuta).map(p->(Fisioterapeuta)p).collect(Collectors.toUnmodifiableList());
    }
    public Sancion sancionar(Socio s,String motivo,TipoSancion t,LocalDate ini,LocalDate fin,Deporte d) {
        Sancion x=new Sancion(s,motivo,t,ini,fin,d);
        s.agregarSancion(x);
        return x;
    }
    public List<Persona> getPersonas() {
        return copiaLectura(personas.values());
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
