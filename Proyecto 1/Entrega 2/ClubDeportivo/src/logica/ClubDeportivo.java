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
public class ClubDeportivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre,nit,direccion;
    private LocalTime horaApertura,horaCierre;
    private GestorUsuarios usuarios;
    private GestorInstalaciones instalaciones;
    private GestorDeportivo deportivo;
    private GestorCompetencias competencias;
    private GestorComercio comercio;
    private GestorPersonal personal;
    private GestorReportes reportes;
    public ClubDeportivo(String n,String nit,String dir,LocalTime ini,LocalTime fin) {
        validar(ini.isBefore(fin),"Horario inválido");
        nombre=n;
        this.nit=nit;
        direccion=dir;
        horaApertura=ini;
        horaCierre=fin;
        usuarios=new GestorUsuarios();
        instalaciones=new GestorInstalaciones();
        deportivo=new GestorDeportivo(instalaciones);
        competencias=new GestorCompetencias(instalaciones);
        comercio=new GestorComercio(new Tienda(new Ubicacion("T","Tienda",TipoUbicacion.TIENDA)),new Cafeteria(new Ubicacion("C","Cafetería",TipoUbicacion.CAFETERIA)),usuarios);
        personal=new GestorPersonal(ini,fin);
        reportes=new GestorReportes(comercio);
    }
    public Persona iniciarSesion(String login,String clave) {
        return usuarios.autenticar(login,clave);
    }
    public GestorUsuarios getGestorUsuarios() {
        return usuarios;
    }
    public GestorInstalaciones getGestorInstalaciones() {
        return instalaciones;
    }
    public GestorDeportivo getGestorDeportivo() {
        return deportivo;
    }
    public GestorCompetencias getGestorCompetencias() {
        return competencias;
    }
    public GestorComercio getGestorComercio() {
        return comercio;
    }
    public GestorPersonal getGestorPersonal() {
        return personal;
    }
    public GestorReportes getGestorReportes() {
        return reportes;
    }
    public String getNombre() {
        return nombre;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
}
