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
        System.out.println("Entrenamientos asignados: "+club.getGestorDeportivo().getSesiones().stream().filter(s->s.getEntrenador()==entrenador).count());
    }
}
