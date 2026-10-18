package pruebas;

import excepciones.ClubException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import logica.ClubDeportivo;
import logica.GestorComercio;
import logica.GestorDeportivo;
import logica.GestorUsuarios;
import modelo.actividades.Instalacion;
import modelo.comercio.Alergeno;
import modelo.comercio.ArticuloDeportivo;
import modelo.comercio.Ingrediente;
import modelo.comercio.Producto;
import modelo.comercio.Snack;
import modelo.deportes.Academia;
import modelo.deportes.Deporte;
import modelo.deportes.Equipo;
import modelo.personal.Turno;
import modelo.personas.Administrador;
import modelo.personas.Empleado;
import modelo.personas.Entrenador;
import modelo.personas.Fisioterapeuta;
import modelo.personas.Socio;
import modelo.tipos.Categoria;
import modelo.tipos.TipoEstadistica;
import persistencia.PersistenciaJson;

/** Construye datos ficticios reproducibles para ejecutar el proyecto. */
public final class DatosPrueba
{
    private DatosPrueba()
    {
    }
    public static ClubDeportivo crear() throws ClubException
    {
        ClubDeportivo c=new ClubDeportivo("Club Deportivo Séneca","NIT-DEMO","Bogotá",LocalTime.of(6,0),LocalTime.of(22,0));
        GestorUsuarios u=c.getGestorUsuarios();
        GestorDeportivo g=c.getGestorDeportivo();
        Administrador a=new Administrador("A1","Administración",LocalDate.of(1985,1,1),"admin","demo123","A1","Administrador");
        u.registrarEmpleado(a);
        Entrenador e=new Entrenador("E1","Entrenadora Ana",LocalDate.of(1990,1,1),"entrenador","demo123","E1","LIC-1");
        Entrenador e2=new Entrenador("E2","Entrenador Luis",LocalDate.of(1990,1,1),"entrenador2","demo123","E2","LIC-2");
        u.registrarEmpleado(e);
        u.registrarEmpleado(e2);
        Fisioterapeuta fis=new Fisioterapeuta("F1","Fisioterapeuta",LocalDate.of(1990,1,1),"fisio","demo123","F1","TP-1");
        u.registrarEmpleado(fis);
        Deporte futbol=new Deporte("Fútbol",true,7,14,22,7,22,90,true,1,Arrays.asList("Portero","Defensa","Medio","Delantero"),TipoEstadistica.FUTBOL);
        Deporte basket=new Deporte("Baloncesto",true,5,10,20,5,15,60,false,1,Arrays.asList("Base","Escolta","Alero","Pívot"),TipoEstadistica.BALONCESTO);
        Deporte voley=new Deporte("Voleibol",true,6,12,24,6,18,90,false,1,Arrays.asList("Armador","Atacante","Líbero"),TipoEstadistica.VOLEIBOL);
        for(Deporte d:Arrays.asList(futbol,basket,voley))
        {
            g.registrarDeporte(d);
            e.agregarEspecialidad(d);
            e2.agregarEspecialidad(d);
        }
        for(String nombre:Arrays.asList("Tenis","Pádel","Tenis de mesa"))
        {
            Deporte d=new Deporte(nombre,false,1,2,4,1,1,60,false,1,Arrays.asList(),TipoEstadistica.INDIVIDUAL);
            g.registrarDeporte(d);
            e.agregarEspecialidad(d);
            e2.agregarEspecialidad(d);
        }
        for(Deporte d:g.getDeportes())c.getGestorInstalaciones().registrarInstalacion(new Instalacion(d.getNombre(),"Escenario "+d.getNombre(),d.isEsDeConjunto()?30:4,LocalTime.of(6,0),LocalTime.of(22,0),d));
        Equipo equipo=new Equipo("Séneca Mayores",Categoria.MAYORES,22,futbol,e);
        g.crearEquipo(equipo);
        for(int k=1;k<=16;k++)
        {
            Socio s=new Socio("S"+k,"Socio "+k,LocalDate.of(2000,1,1),"socio"+k,"demo123","SOC-"+k);
            u.registrarSocio(s);
            for(Deporte d:g.getDeportes())if(!d.isEsDeConjunto())s.asignarNivel(d,k==16?8:3);
            if(k<=14)g.inscribirEnEquipo(s,equipo,"Defensa",k);
        }
        Deporte tenis=g.getDeportes().get(3);
        Academia academia=new Academia("Academia de tenis",tenis,e,1,5,20,"Lunes 10:00",150000);
        g.registrarAcademia(academia);
        g.inscribirEnAcademia(u.buscarSocio("S1"),academia);
        GestorComercio com=c.getGestorComercio();
        Producto balon=new ArticuloDeportivo("BAL","Balón",100000,"Demo","5");
        com.registrarProducto(balon);
        com.getInventario().reabastecer(balon,com.getTienda().getUbicacion(),10,a);
        Alergeno gluten=new Alergeno("Gluten","Cereales con gluten");
        Snack snack=new Snack("GAL","Galleta",5000,100,Arrays.asList(new Ingrediente("Harina",Arrays.asList(gluten))));
        com.registrarProducto(snack);
        com.getInventario().reabastecer(snack,com.getCafeteria().getUbicacion(),20,a);
        LocalDate f=LocalDate.now().plusDays(1);
        for(Empleado em:Arrays.asList(e,e2,fis))c.getGestorPersonal().asignarTurno(new Turno(em.getDocumento(),f,LocalTime.of(6,0),LocalTime.of(22,0),em));
        return c;
    }
    public static void main(String[] args) throws ClubException
    {
        new persistencia.PersistenciaJson().salvar(crear(),args.length==0?"datos/club-ejemplo.json":args[0]);
        System.out.println("Datos de ejemplo creados");
    }
}
