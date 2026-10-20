package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import modelo.competencias.Competencia;
import modelo.competencias.InscripcionCompetencia;
import modelo.deportes.Deporte;
import modelo.personas.Socio;
import modelo.tipos.EstadoPartido;

public class PartidoOficial extends Partido implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String rival;
    private String escenario;
    private String fase;
    private boolean esLocal;
    private Competencia competencia;
    private InscripcionCompetencia inscripcion;
    public PartidoOficial(String id,Competencia c,InscripcionCompetencia i,String rival,LocalDate f,LocalTime h,boolean local,Instalacion inst) throws ClubException
    {
        super(id,f,h,c.getDeporte().getDuracionPartidoMin(),inst);
        competencia=c;
        inscripcion=i;
        this.rival=textoRequerido(rival);
        esLocal=local;
        escenario = local && inst != null ? inst.getNombre() : "Escenario de " + rival;
        fase=c.getFaseActual();
    }
    @Override
    public void agregarParticipante(Socio s) throws ClubException
    {
        if(!inscripcion.estaEnNomina(s) || (inscripcion.getEquipo() != null && inscripcion.getEquipo().suspendidoPorTarjetas(s,competencia.getCodigo())))throw new ParticipacionException("Jugador fuera de nómina oficial");
        super.agregarParticipante(s);
    }
    public int getPuntosObtenidos() throws ClubException
    {
        return competencia.puntosMarcador(getMarcadorPropio(),getMarcadorRival());
    }
    @Override
    public Deporte getDeporte()
    {
        return competencia.getDeporte();
    }
    public InscripcionCompetencia getInscripcion()
    {
        return inscripcion;
    }
    public Competencia getCompetencia()
    {
        return competencia;
    }
    public String getFase()
    {
        return fase;
    }
    public void setFase(String f) throws ClubException
    {
        validar(getEstado()==EstadoPartido.PROGRAMADO && competencia.getFaseActual().equals(f),"Fase incompatible con la competencia");
        fase=f;
    }
    public String getRival()
    {
        return rival;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) throws ClubException
    {
        if(valor==null||valor.trim().isEmpty())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    @Override
    protected void validarAlineacion() throws ClubException
    {
        super.validarAlineacion();
        int minimo = getDeporte().isEsDeConjunto() ? getDeporte().getJugadoresEnCampo() : 1;
        if (getParticipantes().size() < minimo || inscripcion.isEliminada()) throw new ParticipacionException("Nómina insuficiente o eliminada");
        for (Socio socio : getParticipantes())
            if (!inscripcion.estaEnNomina(socio) || (inscripcion.getEquipo() != null && (!inscripcion.getEquipo().esIntegrante(socio) || inscripcion.getEquipo().suspendidoPorTarjetas(socio,competencia.getCodigo()))))
                throw new ParticipacionException("Jugador no habilitado en la competencia");
    }
    @Override
    protected void cumplirSuspensiones()
    {
        if (inscripcion.getEquipo() != null) inscripcion.getEquipo().cumplirSuspensiones(competencia.getCodigo());
    }
    @Override
    public void registrarTarjetas(Socio socio, int amarillas, int rojas) throws ClubException
    {
        if (inscripcion.getEquipo() != null) inscripcion.getEquipo().registrarTarjetas(socio,competencia.getCodigo(),amarillas,rojas,competencia.getLimiteAmarillas());
    }
    public boolean isEsLocal() { return esLocal; }

    public String getEscenario() { return escenario; }
    public void setEscenario(String nombre) throws ClubException { escenario = textoRequerido(nombre); }

}
