package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.competencias.Ranking;
import modelo.deportes.Deporte;
import modelo.personas.Entrenador;
import modelo.personas.Persona;
import modelo.personas.Socio;
import modelo.tipos.EstadoPartido;

public class PartidoPractica extends Partido implements Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean nivelesVerificados;
    private Persona rival;
    private Deporte deporte;
    private List<Socio> ladoPropio = new ArrayList<>();
    private Map<Socio,Integer> cambiosRanking = new HashMap<>();
    public PartidoPractica(String id,Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime h,Instalacion i) throws ClubException
    {
        super(id,f,h,d.getDuracionPartidoMin(),i);
        validar(!d.isEsDeConjunto() && !d.esPadel(), "La práctica individual requiere tenis o tenis de mesa");
        validar(sol!=rival&&(rival instanceof Socio||rival instanceof Entrenador),"Rival inválido");
        this.rival=rival;
        deporte=d;
        if(rival instanceof Socio)validar(d.nivelesCompatibles(sol.getNivel(d),((Socio)rival).getNivel(d)),"Niveles incompatibles");
        else validar(((Entrenador)rival).puedeEntrenar(d),"Entrenador sin especialidad");
        ladoPropio.add(sol);
        agregarParticipante(sol);
        if(rival instanceof Socio)agregarParticipante((Socio)rival);
        nivelesVerificados=true;
    }
    @Override
    public void agregarParticipante(Socio socio) throws ClubException
    {
        validar(!nivelesVerificados, "Los jugadores de la práctica ya están definidos");
        super.agregarParticipante(socio);
    }
    @Override
    public void quitarParticipante(Socio socio) throws ClubException
    {
        throw new ParticipacionException("Cancele y reprograme la práctica para cambiar sus jugadores");
    }
    public boolean incluyeEntrenador()
    {
        return rival instanceof Entrenador;
    }
    @Override
    public Deporte getDeporte()
    {
        return deporte;
    }
    public boolean isNivelesVerificados()
    {
        return nivelesVerificados;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ParticipacionException(mensaje);
    }
    public PartidoPractica(String id, List<Socio> propios, List<Socio> rivales, Deporte deporte, LocalDate fecha, LocalTime hora, Instalacion instalacion) throws ClubException
    {
        super(id,fecha,hora,deporte.getDuracionPartidoMin(),instalacion);
        validar(propios.size() == 2 && rivales.size() == 2 && !deporte.isEsDeConjunto(), "Dobles requiere dos parejas");
        this.deporte = deporte;
        this.rival = rivales.get(0);
        ladoPropio.addAll(propios);
        List<Socio> todos = new ArrayList<>(propios);
        todos.addAll(rivales);
        for (Socio uno : todos)
        {
            for (Socio otro : todos) validar(deporte.nivelesCompatibles(uno.getNivel(deporte),otro.getNivel(deporte)),"Niveles incompatibles");
            agregarParticipante(uno);
        }
        nivelesVerificados = true;
    }
    public boolean gano(Socio socio) throws ClubException
    {
        validar(participo(socio) && getEstado() == EstadoPartido.FINALIZADO,"Partido sin resultado o jugador ajeno");
        boolean propio = ladoPropio == null ? getParticipantes().get(0) == socio : ladoPropio.contains(socio);
        return propio == (getMarcadorPropio() > getMarcadorRival());
    }
    // Se toma una instantanea antes de actualizar a cualquiera de los participantes.
    public int cambioRanking(Socio jugador, Ranking ranking) throws ClubException
    {
        if (cambiosRanking == null) cambiosRanking = new HashMap<>();
        if (cambiosRanking.isEmpty())
        {
            for (Socio socio : getParticipantes())
            {
                double sumaRivales = 0;
                int cantidadRivales = 0;
                for (Socio otro : getParticipantes())
                {
                    if (gano(socio) != gano(otro))
                    {
                        sumaRivales += ranking.getEntrada(otro).getPuntosRanking();
                        cantidadRivales++;
                    }
                }
                double promedio = cantidadRivales == 0 ? 1000 : sumaRivales/cantidadRivales;
                double esperado = 1.0/(1.0+Math.pow(10,(promedio-ranking.getEntrada(socio).getPuntosRanking())/400.0));
                cambiosRanking.put(socio,(int)Math.round(32*((gano(socio)?1:0)-esperado)));
            }
        }
        return cambiosRanking.get(jugador);
    }

}
