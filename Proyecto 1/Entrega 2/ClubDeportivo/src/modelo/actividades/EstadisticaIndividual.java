package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.Map;
import modelo.personas.Socio;

public class EstadisticaIndividual extends Estadistica implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int puntosAnotados;
    private boolean gano;
    public EstadisticaIndividual(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        super(j,p,v);
        puntosAnotados=valor("puntosAnotados");
        gano=valor("gano")==1;
    }
    @Override
    public int calcularPuntosRanking()
    {
        return valor("gano")==1?20:-10;
    }
}
