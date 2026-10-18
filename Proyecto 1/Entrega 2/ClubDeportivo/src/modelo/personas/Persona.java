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
public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String documento,nombre,telefono,correo,login,contrasena;
    private LocalDate fechaNacimiento;
    protected Persona(String doc,String nom,LocalDate nacimiento,String login,String clave) {
        documento=Validacion.texto(doc);
        nombre=Validacion.texto(nom);
        fechaNacimiento=Objects.requireNonNull(nacimiento);
        Validacion.exigir(!nacimiento.isAfter(LocalDate.now()),"Nacimiento futuro");
        this.login=Validacion.texto(login);
        contrasena=hash(Validacion.texto(clave));
        telefono="";
        correo="";
    }
    private static String hash(String s) {
        try {
            return HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        }
        catch(java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
    public int getEdad(LocalDate fecha) {
        return Period.between(fechaNacimiento,fecha).getYears();
    }
    public boolean verificarContrasena(String clave) {
        return clave!=null&&java.security.MessageDigest.isEqual(contrasena.getBytes(java.nio.charset.StandardCharsets.UTF_8),hash(clave).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    public abstract String getTipo();
    public String getDocumento() {
        return documento;
    }
    public String getNombre() {
        return nombre;
    }
    public String getLogin() {
        return login;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public void actualizarContacto(String tel,String mail) {
        telefono=Validacion.texto(tel);
        correo=Validacion.texto(mail);
    }
}
