package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/** Reune los atributos privados y comportamientos comunes de las personas. */
public abstract class Persona implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String documento;
    private String nombre;
    private String telefono;
    private String correo;
    private String login;
    private String contrasena;
    private LocalDate fechaNacimiento;
    protected Persona(String doc,String nom,LocalDate nacimiento,String login,String clave) throws ClubException
    {
        documento=textoRequerido(doc);
        nombre=textoRequerido(nom);
        fechaNacimiento=Objects.requireNonNull(nacimiento);
        validar(!nacimiento.isAfter(LocalDate.now()),"Nacimiento futuro");
        this.login=textoRequerido(login);
        contrasena=textoRequerido(clave);
        telefono="";
        correo="";
    }
    public int getEdad(LocalDate fecha)
    {
        return Period.between(fechaNacimiento,fecha).getYears();
    }
    public boolean verificarContrasena(String clave)
    {
        return clave!=null&&contrasena.equals(clave);
    }
    public abstract String getTipo();
    public String getDocumento()
    {
        return documento;
    }
    public String getNombre()
    {
        return nombre;
    }
    public String getLogin()
    {
        return login;
    }
    public LocalDate getFechaNacimiento()
    {
        return fechaNacimiento;
    }
    public String getTelefono()
    {
        return telefono;
    }
    public String getCorreo()
    {
        return correo;
    }
    public void actualizarContacto(String tel,String mail) throws ClubException
    {
        telefono=textoRequerido(tel);
        correo=textoRequerido(mail);
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
}
