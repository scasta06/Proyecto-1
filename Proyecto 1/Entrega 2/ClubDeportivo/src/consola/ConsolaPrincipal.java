package consola;

import excepciones.ClubException;
import java.util.Scanner;
import logica.ClubDeportivo;
import modelo.personas.Administrador;
import modelo.personas.Entrenador;
import modelo.personas.Fisioterapeuta;
import modelo.personas.Persona;
import modelo.personas.Socio;
import persistencia.IPersistenciaClub;
import persistencia.PersistenciaJson;

public class ConsolaPrincipal
{
    private ClubDeportivo club;
    public static void main(String[] args)
    {
        String ruta=args.length>0?args[0]:"datos/club-ejemplo.json";
        persistencia.IPersistenciaClub p=new persistencia.PersistenciaJson();
        try
        {
            ConsolaPrincipal c=new ConsolaPrincipal();
            c.club=p.cargar(ruta);
            c.iniciarSesion();
            p.salvar(c.club,ruta);
        }
        catch(ClubException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    private void iniciarSesion() throws ClubException
    {
        Scanner s=ConsolaBase.ENTRADA;
        System.out.println("Club Deportivo Séneca. Datos demo: admin / demo123");
        System.out.print("Login: ");
        if(!s.hasNextLine())return;
        String login=s.nextLine();
        System.out.print("Contraseña: ");
        if(!s.hasNextLine())return;
        abrirConsola(club.iniciarSesion(login,s.nextLine()));
    }
    private void abrirConsola(Persona p) throws ClubException
    {
        ConsolaBase c;
        if(p instanceof Socio)c=new ConsolaSocio(club,(Socio)p);
        else if(p instanceof Entrenador)c=new ConsolaEntrenador(club,(Entrenador)p);
        else if(p instanceof Fisioterapeuta)c=new ConsolaFisioterapeuta(club,(Fisioterapeuta)p);
        else c=new ConsolaAdministrador(club,(Administrador)p);
        c.mostrarMenu();
    }
}
