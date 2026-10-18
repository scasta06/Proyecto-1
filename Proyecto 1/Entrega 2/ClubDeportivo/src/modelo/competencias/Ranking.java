package modelo.competencias;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.actividades.Estadistica;
import modelo.deportes.Deporte;
import modelo.personas.Socio;

public class Ranking implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Deporte deporte;
    private LocalDate ultimaActualizacion;
    private Map<Socio,EntradaRanking> entradas=new LinkedHashMap<>();
    private Set<Estadistica> procesadas=new HashSet<>();
    public Ranking(Deporte d)
    {
        deporte=d;
    }
    public void actualizar(Estadistica e) throws ClubException
    {
        validar(e.getPartido().getDeporte()==deporte&&!procesadas.contains(e),"Estadística repetida o de otra disciplina");
        getEntrada(e.getJugador()).acumular(e);
        procesadas.add(e);
        ultimaActualizacion=LocalDate.now();
    }
    public EntradaRanking getEntrada(Socio s)
    {
        EntradaRanking entrada = entradas.get(s);
        if (entrada == null)
        {
            entrada = new EntradaRanking(s);
            entradas.put(s, entrada);
        }
        return entrada;
    }
    public List<EntradaRanking> getPosiciones()
    {
        List<EntradaRanking> posiciones = new ArrayList<EntradaRanking>(entradas.values());
        Collections.sort(posiciones, new Comparator<EntradaRanking>()
        {
            @Override
            public int compare(EntradaRanking primera, EntradaRanking segunda)
            {
                int diferencia = Integer.compare(segunda.getPuntosRanking(), primera.getPuntosRanking());
                if (diferencia == 0)
                {
                    diferencia = primera.getJugador().getDocumento().compareTo(segunda.getJugador().getDocumento());
                }
                return diferencia;
            }
        }
        );
        return posiciones;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
