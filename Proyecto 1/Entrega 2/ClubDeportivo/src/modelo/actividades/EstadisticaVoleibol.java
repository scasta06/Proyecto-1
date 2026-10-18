package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.Map;
import modelo.personas.Socio;

public class EstadisticaVoleibol extends Estadistica implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int aces;
    private int bloqueos;
    public EstadisticaVoleibol(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        super(j,p,v);
        aces=valor("aces");
        bloqueos=valor("bloqueos");
    }
    @Override
    public int calcularPuntosRanking()
    {
        return valor("aces")*2+valor("bloqueos");
    }
}
