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
        while(true) {
            System.out.println("1. Consultar resumen / 0. Salir");
            String opcion=pedirCadena("Opción: ");
            if(opcion.equals("0")||opcion.isEmpty())return;
            if(opcion.equals("1")) {
                System.out.println("Ventas del mes: "+club.getGestorReportes().generarReporteVentas(LocalDate.now().withDayOfMonth(1),LocalDate.now()).getTotal());
            }
            else System.out.println("Opción inválida");
        }
    }
}
