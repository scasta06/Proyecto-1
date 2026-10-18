package modelo.actividades;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import modelo.competencias.Competencia;
import modelo.competencias.InscripcionCompetencia;
import modelo.deportes.Deporte;
import modelo.personas.Socio;
import modelo.tipos.EstadoPartido;
import modelo.tipos.FormatoCompetencia;

public class PartidoOficial extends Partido implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String rival;
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
        fase=c.getFormato()==FormatoCompetencia.GRUPOS_Y_ELIMINACION?"GRUPOS":c.getFormato().name();
    }
    @Override
    public void agregarParticipante(Socio s) throws ClubException
    {
        if(!inscripcion.estaEnNomina(s))throw new ParticipacionException("Jugador fuera de nómina oficial");
        super.agregarParticipante(s);
    }
    public int getPuntosObtenidos() throws ClubException
    {
        return competencia.puntosPor(getResultado());
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
        validar(getEstado()==EstadoPartido.PROGRAMADO&&new HashSet<String>(Arrays.asList("LIGA","GRUPOS","ELIMINACION_DIRECTA")).contains(f),"Fase inválida");
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
}
