package consola;

import excepciones.ClubException;
import java.time.LocalDate;
import logica.ClubDeportivo;
import modelo.personas.Administrador;

public class ConsolaAdministrador extends ConsolaBase
{
    private Administrador admin;
    public ConsolaAdministrador(ClubDeportivo c,Administrador p)
    {
        super(c);
        admin=p;
    }
    @Override
    public void mostrarMenu() throws ClubException
    {
        while(true)
        {
            System.out.println("1. Consultar resumen / 0. Salir");
            String opcion=pedirCadena("Opción: ");
            if(opcion.equals("0")||opcion.isEmpty())return;
            if(opcion.equals("1"))
            {
                System.out.println("Ventas del mes: "+club.getGestorReportes().generarReporteVentas(LocalDate.now().withDayOfMonth(1),LocalDate.now()).getTotal());
            }
            else System.out.println("Opción inválida");
        }
    }
}
