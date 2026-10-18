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
public abstract class ConsolaBase {
    protected ClubDeportivo club;
    protected static final Scanner ENTRADA=new Scanner(System.in);
    protected ConsolaBase(ClubDeportivo c) {
        club=c;
    }
    protected String pedirCadena(String msg) {
        System.out.print(msg);
        return ENTRADA.hasNextLine()?ENTRADA.nextLine().trim():"";
    }
    protected int pedirEntero(String msg) {
        while(true) {
            try {
                return Integer.parseInt(pedirCadena(msg));
            }
            catch(NumberFormatException e) {
                System.out.println("Ingrese un entero");
            }
        }
    }
    protected LocalDate pedirFecha(String msg) {
        return LocalDate.parse(pedirCadena(msg));
    }
    public abstract void mostrarMenu();
}
