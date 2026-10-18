package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.deportes.Deporte;
import modelo.personas.Socio;
import modelo.tipos.EstadoPartido;
import modelo.tipos.Resultado;

public abstract class Partido extends Evento implements Serializable
{
    private static final long serialVersionUID = 1L;
    private EstadoPartido estado=EstadoPartido.PROGRAMADO;
    private int marcadorPropio;
    private int marcadorRival;
    protected List<Socio> participantes=new ArrayList<>();
    protected List<Estadistica> estadisticas=new ArrayList<>();
    protected Partido(String id,LocalDate f,LocalTime h,int dur,Instalacion i) throws ClubException
    {
        super(id,f,h,dur,i);
    }
    public void agregarParticipante(Socio s) throws ClubException
    {
        if(estado!=EstadoPartido.PROGRAMADO||!s.isActivo()||s.estaSuspendido(getDeporte(),getFecha())||participantes.contains(s))throw new ParticipacionException("Participante no habilitado");
        participantes.add(s);
    }
    public boolean participo(Socio s)
    {
        return participantes.contains(s);
    }
    public void registrarResultado(int propio,int rival) throws ClubException
    {
        if(estado!=EstadoPartido.PROGRAMADO||propio<0||rival<0||(!getDeporte().isPermiteEmpate()&&propio==rival))throw new ParticipacionException("Resultado inválido o ya registrado");
        marcadorPropio=propio;
        marcadorRival=rival;
        estado=EstadoPartido.FINALIZADO;
    }
    public Resultado getResultado() throws ClubException
    {
        validar(estado==EstadoPartido.FINALIZADO,"Partido sin resultado");
        return marcadorPropio>marcadorRival?Resultado.VICTORIA:marcadorPropio==marcadorRival?Resultado.EMPATE:Resultado.DERROTA;
    }
    public void agregarEstadistica(Estadistica e) throws ClubException
    {
        validar(e.getPartido() == this && participo(e.getJugador()), "Estadística de otro partido o jugador");
        for (Estadistica anterior : estadisticas)
        {
            validar(anterior.getJugador() != e.getJugador(), "Estadística duplicada");
        }
        estadisticas.add(e);
    }
    public abstract Deporte getDeporte();
    @Override
    public boolean bloqueaInstalacion()
    {
        return estado!=EstadoPartido.CANCELADO;
    }
    public EstadoPartido getEstado()
    {
        return estado;
    }
    public List<Socio> getParticipantes()
    {
        return copiaLectura(participantes);
    }
    public List<Estadistica> getEstadisticas()
    {
        return copiaLectura(estadisticas);
    }
    public int getMarcadorPropio()
    {
        return marcadorPropio;
    }
    public int getMarcadorRival()
    {
        return marcadorRival;
    }
    public void cancelar() throws ClubException
    {
        validar(estado==EstadoPartido.PROGRAMADO,"Partido finalizado");
        estado=EstadoPartido.CANCELADO;
        if(getInstalacion()!=null)getInstalacion().liberar(this);
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
