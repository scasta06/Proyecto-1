package consola;

import logica.ClubDeportivo;
import modelo.personal.Turno;
import modelo.personas.Fisioterapeuta;

public class ConsolaFisioterapeuta extends ConsolaBase
{
    private Fisioterapeuta fisio;
    public ConsolaFisioterapeuta(ClubDeportivo c,Fisioterapeuta p)
    {
        super(c);
        fisio=p;
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
                int cantidad = 0;
                for (Turno turno : club.getGestorPersonal().getTurnos())
                {
                    if (turno.getEmpleado() == fisio)
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
