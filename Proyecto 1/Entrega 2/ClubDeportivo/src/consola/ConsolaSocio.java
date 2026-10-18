package consola;

import logica.ClubDeportivo;
import modelo.actividades.Reserva;
import modelo.personas.Socio;

public class ConsolaSocio extends ConsolaBase
{
    private Socio socio;
    public ConsolaSocio(ClubDeportivo c,Socio p)
    {
        super(c);
        socio=p;
    }
    @Override
    public void mostrarMenu()
    {
        while(true)
        {
            System.out.println("1. Consultar resumen / 0. Salir");
            String opcion=pedirCadena("Opción: ");
            if(opcion.equals("0")||opcion.isEmpty())return;
            if(opcion.equals("1"))
            {
                System.out.println("Puntos: "+socio.getCuenta().getPuntos());
                int cantidad = 0;
                for (Reserva reserva : club.getGestorInstalaciones().getReservas())
                {
                    if (reserva.getSocio() == socio)
                    {
                        cantidad++;
                    }
                }
                System.out.println("Total: " + cantidad);
            }
            else System.out.println("Opción inválida");
        }
    }
}
