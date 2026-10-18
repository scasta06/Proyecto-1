package modelo.competencias;

import excepciones.ClubException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.actividades.Estadistica;
import modelo.personas.Socio;

public class EntradaRanking implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio jugador;
    private int partidosJugados,puntosRanking=1000;
    private Map<String,Integer> acumulados=new LinkedHashMap<>();
    public EntradaRanking(Socio s)
    {
        jugador=s;
    }
    public void acumular(Estadistica e) throws ClubException
    {
        validar(e.getJugador() == jugador, "Estadística de otro jugador");
        partidosJugados++;
        puntosRanking = Math.max(0, puntosRanking + e.calcularPuntosRanking());
        for (String clave : e.getValores().keySet())
        {
            Integer anterior = acumulados.get(clave);
            if (anterior == null)
            {
                anterior = 0;
            }
            acumulados.put(clave, anterior + e.getValores().get(clave));
        }
    }
    public Socio getJugador()
    {
        return jugador;
    }
    public int getPartidosJugados()
    {
        return partidosJugados;
    }
    public int getPuntosRanking()
    {
        return puntosRanking;
    }
    public Map<String,Integer> getAcumulados()
    {
        return new HashMap<String, Integer>(acumulados);
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
