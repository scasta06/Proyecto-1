package modelo.actividades;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import modelo.personas.*;
import modelo.deportes.*;
import modelo.actividades.*;
import modelo.competencias.*;
import modelo.personal.*;
import modelo.inventario.*;
import modelo.comercio.*;
import modelo.tipos.*;
import logica.*;
import excepciones.*;
public abstract class Partido extends Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    protected EstadoPartido estado=EstadoPartido.PROGRAMADO;
    protected int marcadorPropio,marcadorRival;
    protected List<Socio> participantes=new ArrayList<>();
    protected List<Estadistica> estadisticas=new ArrayList<>();
    protected Partido(String id,LocalDate f,LocalTime h,int dur,Instalacion i) {
        super(id,f,h,dur,i);
    }
    public void agregarParticipante(Socio s) {
        if(estado!=EstadoPartido.PROGRAMADO||!s.isActivo()||s.estaSuspendido(getDeporte(),fecha)||participantes.contains(s))throw new ParticipacionException("Participante no habilitado");
        participantes.add(s);
    }
    public boolean participo(Socio s) {
        return participantes.contains(s);
    }
    public void registrarResultado(int propio,int rival) {
        if(estado!=EstadoPartido.PROGRAMADO||propio<0||rival<0||(!getDeporte().isPermiteEmpate()&&propio==rival))throw new ParticipacionException("Resultado inválido o ya registrado");
        marcadorPropio=propio;
        marcadorRival=rival;
        estado=EstadoPartido.FINALIZADO;
    }
    public Resultado getResultado() {
        validar(estado==EstadoPartido.FINALIZADO,"Partido sin resultado");
        return marcadorPropio>marcadorRival?Resultado.VICTORIA:marcadorPropio==marcadorRival?Resultado.EMPATE:Resultado.DERROTA;
    }
    public void agregarEstadistica(Estadistica e) {
        validar(e.getPartido()==this&&participo(e.getJugador())&&estadisticas.stream().noneMatch(x->x.getJugador()==e.getJugador()),"Estadística duplicada o de otro partido/jugador");
        estadisticas.add(e);
    }
    public abstract Deporte getDeporte();
    public boolean bloqueaInstalacion() {
        return estado!=EstadoPartido.CANCELADO;
    }
    public EstadoPartido getEstado() {
        return estado;
    }
    public List<Socio> getParticipantes() {
        return copiaLectura(participantes);
    }
    public List<Estadistica> getEstadisticas() {
        return copiaLectura(estadisticas);
    }
    public int getMarcadorPropio() {
        return marcadorPropio;
    }
    public int getMarcadorRival() {
        return marcadorRival;
    }
    public void cancelar() {
        validar(estado==EstadoPartido.PROGRAMADO,"Partido finalizado");
        estado=EstadoPartido.CANCELADO;
        if(instalacion!=null)instalacion.liberar(this);
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
