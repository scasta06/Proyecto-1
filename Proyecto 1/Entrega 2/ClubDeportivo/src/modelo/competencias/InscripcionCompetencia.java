package modelo.competencias;
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
public class InscripcionCompetencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate fechaInscripcion=LocalDate.now();
    private int puntosAcumulados,partidosJugados;
    private boolean eliminada;
    private Competencia competencia;
    private Equipo equipo;
    private Socio jugador;
    private List<Socio> nomina;
    public InscripcionCompetencia(Competencia c,Equipo e,Socio s,List<Socio> n) {
        competencia=c;
        equipo=e;
        jugador=s;
        nomina=new ArrayList<>(n);
    }
    public boolean estaEnNomina(Socio s) {
        return nomina.contains(s);
    }
    public void sumarPuntos(int p) {
        puntosAcumulados+=p;
        partidosJugados++;
    }
    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }
    public int getPartidosJugados() {
        return partidosJugados;
    }
    public Equipo getEquipo() {
        return equipo;
    }
    public Socio getJugador() {
        return jugador;
    }
    public Competencia getCompetencia() {
        return competencia;
    }
    public List<Socio> getNomina() {
        return copiaLectura(nomina);
    }
    public boolean isEliminada() {
        return eliminada;
    }
    public void eliminar() {
        eliminada=true;
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
