package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import modelo.deportes.Deporte;
import modelo.deportes.Equipo;
import modelo.personas.Socio;

/** Practica entre dos equipos del club, de igual deporte y categoria. */
public class PartidoInterno extends Partido
{
    private static final long serialVersionUID = 1L;
    private Equipo local;
    private Equipo visitante;
    private Map<Socio,Equipo> alineacion = new HashMap<>();
    public PartidoInterno(String id, Equipo local, Equipo visitante, LocalDate fecha, LocalTime hora, Instalacion instalacion) throws ClubException
    {
        super(id,fecha,hora,local.getDeporte().getDuracionPartidoMin(),instalacion);
        if (local == visitante || !local.isActivo() || !visitante.isActivo() || local.getDeporte() != visitante.getDeporte() || local.getCategoria() != visitante.getCategoria())
            throw new ParticipacionException("Equipos incompatibles");
        this.local = local;
        this.visitante = visitante;
        for (Equipo equipo : new Equipo[] {local,visitante})
            for (Socio socio : equipo.getJugadoresActivos())
                if (!socio.estaSuspendido(getDeporte(),fecha) && !equipo.suspendidoPorTarjetas(socio,"INTERNOS")) agregarParticipante(socio);
        validarAlineacion();
    }
    public Equipo getEquipoDe(Socio socio)
    {
        Equipo original = alineacion.get(socio);
        return original != null ? original : local.esIntegrante(socio) ? local : visitante.esIntegrante(socio) ? visitante : null;
    }
    @Override
    public Deporte getDeporte() { return local.getDeporte(); }
    @Override
    public void agregarParticipante(Socio socio) throws ClubException
    {
        Equipo equipo = getEquipoDe(socio);
        if (equipo == null || equipo.suspendidoPorTarjetas(socio,"INTERNOS")) throw new ParticipacionException("Jugador no habilitado para su equipo");
        super.agregarParticipante(socio);
        alineacion.put(socio,equipo);
    }
    @Override
    protected void validarAlineacion() throws ClubException
    {
        super.validarAlineacion();
        for (Equipo equipo : new Equipo[] {local,visitante})
        {
            equipo.validarOrdenPartido("INTERNOS",getFecha().atTime(getHoraInicio()));
            int cantidad = 0;
            for (Socio socio : getParticipantes())
            {
                if (getEquipoDe(socio) == equipo)
                {
                    if (!equipo.esIntegrante(socio)) throw new ParticipacionException("Jugador retirado de su equipo original");
                }
                if (getEquipoDe(socio) == equipo && equipo.esIntegrante(socio))
                {
                    if (equipo.suspendidoPorTarjetas(socio,"INTERNOS")) throw new ParticipacionException("Jugador suspendido por tarjetas");
                    cantidad++;
                }
            }
            if (!equipo.isActivo() || cantidad < getDeporte().getJugadoresEnCampo()) throw new ParticipacionException("Equipo sin el mínimo de jugadores habilitados");
        }
    }
    @Override
    protected void cumplirSuspensiones()
    {
        local.registrarPartidoDisputado("INTERNOS",getFecha().atTime(getHoraInicio()));
        visitante.registrarPartidoDisputado("INTERNOS",getFecha().atTime(getHoraInicio()));
        local.cumplirSuspensiones("INTERNOS");
        visitante.cumplirSuspensiones("INTERNOS");
    }
    @Override
    protected void validarRegistroEstadisticas() throws ClubException
    {
        local.validarFechaEstadisticas("INTERNOS",getFecha().atTime(getHoraInicio()));
        visitante.validarFechaEstadisticas("INTERNOS",getFecha().atTime(getHoraInicio()));
    }
    @Override
    public void registrarTarjetas(Socio socio, int amarillas, int rojas) throws ClubException
    {
        validarRegistroEstadisticas();
        if (!participo(socio) || getEstado() != modelo.tipos.EstadoPartido.FINALIZADO)
            throw new ParticipacionException("Jugador ajeno o partido sin resultado");
        getEquipoDe(socio).registrarTarjetas(socio,"INTERNOS",amarillas,rojas,2);
    }
}
