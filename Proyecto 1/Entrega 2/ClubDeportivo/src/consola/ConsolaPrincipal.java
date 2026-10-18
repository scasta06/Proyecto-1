package consola;
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
public class ConsolaPrincipal {
    private ClubDeportivo club;
    public static void main(String[] args) {
        if(args.length>0&&args[0].equals("--demostracion")) {
            pruebas.PruebasSistema.main(new String[0]);
            return;
        }
        String ruta=args.length>0?args[0]:"datos/club-ejemplo.json";
        persistencia.IPersistenciaClub p=new persistencia.PersistenciaJson();
        try {
            ConsolaPrincipal c=new ConsolaPrincipal();
            c.club=p.cargar(ruta);
            c.iniciarSesion();
            p.salvar(c.club,ruta);
        }
        catch(ClubException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    private void iniciarSesion() {
        Scanner s=ConsolaBase.ENTRADA;
        System.out.println("Club Deportivo Séneca. Datos demo: admin / demo123");
        System.out.print("Login: ");
        if(!s.hasNextLine())return;
        String login=s.nextLine();
        System.out.print("Contraseña: ");
        if(!s.hasNextLine())return;
        abrirConsola(club.iniciarSesion(login,s.nextLine()));
    }
    private void abrirConsola(Persona p) {
        ConsolaBase c;
        if(p instanceof Socio)c=new ConsolaSocio(club,(Socio)p);
        else if(p instanceof Entrenador)c=new ConsolaEntrenador(club,(Entrenador)p);
        else if(p instanceof Fisioterapeuta)c=new ConsolaFisioterapeuta(club,(Fisioterapeuta)p);
        else c=new ConsolaAdministrador(club,(Administrador)p);
        c.mostrarMenu();
    }
}
