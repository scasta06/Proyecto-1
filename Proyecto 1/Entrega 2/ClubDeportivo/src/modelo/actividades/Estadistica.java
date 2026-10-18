package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import modelo.personas.Socio;

public abstract class Estadistica implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected LocalDate fechaRegistro=LocalDate.now();
    private Socio jugador;
    private Partido partido;
    private Map<String,Integer> valores;
    protected Estadistica(Socio j,Partido p,Map<String,Integer> v) throws ClubException
    {
        jugador=j;
        partido=p;
        Objects.requireNonNull(v);
        valores=new LinkedHashMap<>(v);
        for (Integer valor : v.values())
        {
            validar(valor != null && valor >= 0, "Estadísticas negativas o nulas");
        }
    }
    public Map<String,Integer> getValores()
    {
        return new HashMap<String, Integer>(valores);
    }
    public abstract int calcularPuntosRanking();
    public Socio getJugador()
    {
        return jugador;
    }
    public Partido getPartido()
    {
        return partido;
    }
    protected int valor(String k)
    {
        Integer dato = valores.get(k);
        return dato == null ? 0 : dato;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
