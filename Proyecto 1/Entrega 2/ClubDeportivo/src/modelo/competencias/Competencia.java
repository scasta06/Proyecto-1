package modelo.competencias;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import modelo.actividades.PartidoOficial;
import modelo.deportes.Deporte;
import modelo.tipos.Categoria;
import modelo.tipos.EstadoCompetencia;
import modelo.tipos.EstadoPartido;
import modelo.tipos.FormatoCompetencia;
import modelo.tipos.Resultado;

public class Competencia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private String organizador;
    private Categoria categoria;
    private FormatoCompetencia formato;
    private EstadoCompetencia estado=EstadoCompetencia.INSCRIPCIONES_ABIERTAS;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate cierreInscripciones;
    private int puntosVictoria=3,puntosEmpate=1,puntosDerrota=0;
    private Deporte deporte;
    private List<InscripcionCompetencia> inscripciones=new ArrayList<>();
    private List<PartidoOficial> partidos=new ArrayList<>();
    public Competencia(String cod,String nom,String org,Deporte d,Categoria cat,FormatoCompetencia formato,LocalDate ini,LocalDate fin,LocalDate cierre) throws ClubException
    {
        validar(!fin.isBefore(ini)&&!cierre.isAfter(ini),"Fechas inválidas");
        codigo=textoRequerido(cod);
        nombre=textoRequerido(nom);
        organizador=textoRequerido(org);
        deporte=d;
        categoria=cat;
        this.formato=formato;
        fechaInicio=ini;
        fechaFin=fin;
        cierreInscripciones=cierre;
    }
    public boolean inscripcionesAbiertas(LocalDate f)
    {
        return estado==EstadoCompetencia.INSCRIPCIONES_ABIERTAS&&!f.isAfter(cierreInscripciones);
    }
    public int puntosPor(Resultado r)
    {
        return r==Resultado.VICTORIA?puntosVictoria:r==Resultado.EMPATE?puntosEmpate:puntosDerrota;
    }
    public void agregarInscripcion(InscripcionCompetencia i) throws ClubException
    {
        validar(i.getCompetencia()==this&&!inscripciones.contains(i),"Inscripción inválida");
        inscripciones.add(i);
    }
    public void agregarPartido(PartidoOficial p)
    {
        partidos.add(p);
        estado=EstadoCompetencia.EN_CURSO;
    }
    public List<InscripcionCompetencia> getTablaPosiciones()
    {
        List<InscripcionCompetencia> posiciones = new ArrayList<InscripcionCompetencia>(inscripciones);
        Collections.sort(posiciones, new Comparator<InscripcionCompetencia>()
        {
            @Override
            public int compare(InscripcionCompetencia primera, InscripcionCompetencia segunda)
            {
                int diferencia = Integer.compare(segunda.getPuntosAcumulados(), primera.getPuntosAcumulados());
                if (diferencia == 0)
                {
                    String nombrePrimera = primera.getEquipo() != null ? primera.getEquipo().getNombre() : primera.getJugador().getNombre();
                    String nombreSegunda = segunda.getEquipo() != null ? segunda.getEquipo().getNombre() : segunda.getJugador().getNombre();
                    diferencia = nombrePrimera.compareTo(nombreSegunda);
                }
                return diferencia;
            }
        }
        );
        return posiciones;
    }
    public Deporte getDeporte()
    {
        return deporte;
    }
    public Categoria getCategoria()
    {
        return categoria;
    }
    public FormatoCompetencia getFormato()
    {
        return formato;
    }
    public LocalDate getFechaInicio()
    {
        return fechaInicio;
    }
    public LocalDate getFechaFin()
    {
        return fechaFin;
    }
    public List<InscripcionCompetencia> getInscripciones()
    {
        return copiaLectura(inscripciones);
    }
    public List<PartidoOficial> getPartidos()
    {
        return copiaLectura(partidos);
    }
    public EstadoCompetencia getEstado()
    {
        return estado;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public void finalizar() throws ClubException
    {
        for (PartidoOficial partido : partidos)
        {
            validar(partido.getEstado() != EstadoPartido.PROGRAMADO, "Hay partidos pendientes");
        }
        estado = EstadoCompetencia.FINALIZADA;
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
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
}
