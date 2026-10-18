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
public class ConsolaAdministrador extends ConsolaBase {
    private Administrador admin;
    public ConsolaAdministrador(ClubDeportivo c,Administrador p) {
        super(c);
        admin=p;
    }
    public void mostrarMenu() {
        ReporteVentas r=club.getGestorReportes().generarReporteVentas(LocalDate.now().withDayOfMonth(1),LocalDate.now());
        System.out.println("Ventas del mes: "+r.getTotal()+" / "+r.getTotalesPorRubro());
        System.out.println("Equipos: "+club.getGestorDeportivo().getEquipos().size());
    }
}
