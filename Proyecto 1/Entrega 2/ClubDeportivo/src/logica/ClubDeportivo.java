package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalTime;
import modelo.comercio.Cafeteria;
import modelo.comercio.Tienda;
import modelo.inventario.Ubicacion;
import modelo.personas.Persona;
import modelo.tipos.TipoUbicacion;

/** Compone los gestores definidos por el modelo del club. */
public class ClubDeportivo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String nit;
    private String direccion;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private GestorUsuarios usuarios;
    private GestorInstalaciones instalaciones;
    private GestorDeportivo deportivo;
    private GestorCompetencias competencias;
    private GestorComercio comercio;
    private GestorPersonal personal;
    private GestorReportes reportes;
    public ClubDeportivo(String n,String nit,String dir,LocalTime ini,LocalTime fin) throws ClubException
    {
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
    public Persona iniciarSesion(String login,String clave) throws ClubException
    {
        return usuarios.autenticar(login,clave);
    }
    public GestorUsuarios getGestorUsuarios()
    {
        return usuarios;
    }
    public GestorInstalaciones getGestorInstalaciones()
    {
        return instalaciones;
    }
    public GestorDeportivo getGestorDeportivo()
    {
        return deportivo;
    }
    public GestorCompetencias getGestorCompetencias()
    {
        return competencias;
    }
    public GestorComercio getGestorComercio()
    {
        return comercio;
    }
    public GestorPersonal getGestorPersonal()
    {
        return personal;
    }
    public GestorReportes getGestorReportes()
    {
        return reportes;
    }
    public String getNombre()
    {
        return nombre;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
