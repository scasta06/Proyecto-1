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
public class PartidoOficial extends Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    private String rival,fase;
    private boolean esLocal;
    private Competencia competencia;
    private InscripcionCompetencia inscripcion;
    public PartidoOficial(String id,Competencia c,InscripcionCompetencia i,String rival,LocalDate f,LocalTime h,boolean local,Instalacion inst) {
        super(id,f,h,c.getDeporte().getDuracionPartidoMin(),inst);
        competencia=c;
        inscripcion=i;
        this.rival=Validacion.texto(rival);
        esLocal=local;
        fase=c.getFormato()==FormatoCompetencia.GRUPOS_Y_ELIMINACION?"GRUPOS":c.getFormato().name();
    }
    public void agregarParticipante(Socio s) {
        if(!inscripcion.estaEnNomina(s))throw new ParticipacionException("Jugador fuera de nómina oficial");
        super.agregarParticipante(s);
    }
    public int getPuntosObtenidos() {
        return competencia.puntosPor(getResultado());
    }
    public Deporte getDeporte() {
        return competencia.getDeporte();
    }
    public InscripcionCompetencia getInscripcion() {
        return inscripcion;
    }
    public Competencia getCompetencia() {
        return competencia;
    }
    public String getFase() {
        return fase;
    }
    public void setFase(String f) {
        Validacion.exigir(estado==EstadoPartido.PROGRAMADO&&Set.of("LIGA","GRUPOS","ELIMINACION_DIRECTA").contains(f),"Fase inválida");
        fase=f;
    }
    public String getRival() {
        return rival;
    }
}
