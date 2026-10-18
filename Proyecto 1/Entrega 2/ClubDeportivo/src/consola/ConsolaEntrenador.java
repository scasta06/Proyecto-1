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
public class ConsolaEntrenador extends ConsolaBase {
    private Entrenador entrenador;
    public ConsolaEntrenador(ClubDeportivo c,Entrenador p) {
        super(c);
        entrenador=p;
    }
    public void mostrarMenu() {
        while(true) {
            System.out.println("1. Consultar resumen / 0. Salir");
            String opcion=pedirCadena("Opción: ");
            if(opcion.equals("0")||opcion.isEmpty())return;
            if(opcion.equals("1")) {
                System.out.println("Sesiones: "+club.getGestorDeportivo().getSesiones().stream().filter(s->s.getEntrenador()==entrenador).count());
            }
            else System.out.println("Opción inválida");
        }
    }
}
