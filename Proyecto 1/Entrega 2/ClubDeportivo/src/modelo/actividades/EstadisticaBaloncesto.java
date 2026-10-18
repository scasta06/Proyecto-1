package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.Map;
import modelo.personas.Socio;

public class EstadisticaBaloncesto extends Estadistica implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int puntos;
    private int rebotes;
    public EstadisticaBaloncesto(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        super(j,p,v);
        puntos=valor("puntos");
        rebotes=valor("rebotes");
    }
    @Override
    public int calcularPuntosRanking()
    {
        return valor("puntos")+valor("rebotes");
    }
}
