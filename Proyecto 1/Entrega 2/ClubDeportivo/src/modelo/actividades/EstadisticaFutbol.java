package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.Map;
import modelo.personas.Socio;

public class EstadisticaFutbol extends Estadistica implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int goles;
    private int asistencias;
    public EstadisticaFutbol(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        super(j,p,v);
        goles=valor("goles");
        asistencias=valor("asistencias");
    }
    @Override
    public int calcularPuntosRanking()
    {
        return valor("goles")*3+valor("asistencias");
    }
}
