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
public class ConsolaFisioterapeuta extends ConsolaBase {
    private Fisioterapeuta fisio;
    public ConsolaFisioterapeuta(ClubDeportivo c,Fisioterapeuta p) {
        super(c);
        fisio=p;
    }
    public void mostrarMenu() {
        while(true) {
            System.out.println("1. Consultar resumen / 0. Salir");
            String opcion=pedirCadena("Opción: ");
            if(opcion.equals("0")||opcion.isEmpty())return;
            if(opcion.equals("1")) {
                System.out.println("Turnos: "+club.getGestorPersonal().getTurnos().stream().filter(t->t.getEmpleado()==fisio).count());
            }
            else System.out.println("Opción inválida");
        }
    }
}
