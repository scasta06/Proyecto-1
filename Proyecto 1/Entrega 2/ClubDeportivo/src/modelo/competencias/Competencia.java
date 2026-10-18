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
public class Competencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo,nombre,organizador;
    private Categoria categoria;
    private FormatoCompetencia formato;
    private EstadoCompetencia estado=EstadoCompetencia.INSCRIPCIONES_ABIERTAS;
    private LocalDate fechaInicio,fechaFin,cierreInscripciones;
    private int puntosVictoria=3,puntosEmpate=1,puntosDerrota=0;
    private Deporte deporte;
    private List<InscripcionCompetencia> inscripciones=new ArrayList<>();
    private List<PartidoOficial> partidos=new ArrayList<>();
    public Competencia(String cod,String nom,String org,Deporte d,Categoria cat,FormatoCompetencia formato,LocalDate ini,LocalDate fin,LocalDate cierre) {
        Validacion.exigir(!fin.isBefore(ini)&&!cierre.isAfter(ini),"Fechas inválidas");
        codigo=Validacion.texto(cod);
        nombre=Validacion.texto(nom);
        organizador=Validacion.texto(org);
        deporte=d;
        categoria=cat;
        this.formato=formato;
        fechaInicio=ini;
        fechaFin=fin;
        cierreInscripciones=cierre;
    }
    public boolean inscripcionesAbiertas(LocalDate f) {
        return estado==EstadoCompetencia.INSCRIPCIONES_ABIERTAS&&!f.isAfter(cierreInscripciones);
    }
    public int puntosPor(Resultado r) {
        return r==Resultado.VICTORIA?puntosVictoria:r==Resultado.EMPATE?puntosEmpate:puntosDerrota;
    }
    public void agregarInscripcion(InscripcionCompetencia i) {
        Validacion.exigir(i.getCompetencia()==this&&!inscripciones.contains(i),"Inscripción inválida");
        inscripciones.add(i);
    }
    public void agregarPartido(PartidoOficial p) {
        partidos.add(p);
        estado=EstadoCompetencia.EN_CURSO;
    }
    public List<InscripcionCompetencia> getTablaPosiciones() {
        return inscripciones.stream().sorted(Comparator.comparingInt(InscripcionCompetencia::getPuntosAcumulados).reversed().thenComparing(i->i.getEquipo()!=null?i.getEquipo().getNombre():i.getJugador().getNombre())).collect(Collectors.toUnmodifiableList());
    }
    public Deporte getDeporte() {
        return deporte;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public FormatoCompetencia getFormato() {
        return formato;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public List<InscripcionCompetencia> getInscripciones() {
        return Validacion.copia(inscripciones);
    }
    public List<PartidoOficial> getPartidos() {
        return Validacion.copia(partidos);
    }
    public EstadoCompetencia getEstado() {
        return estado;
    }
    public String getCodigo() {
        return codigo;
    }
    public void finalizar() {
        Validacion.exigir(partidos.stream().allMatch(p->p.getEstado()!=EstadoPartido.PROGRAMADO),"Hay partidos pendientes");
        estado=EstadoCompetencia.FINALIZADA;
    }
}
