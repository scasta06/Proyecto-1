package logica;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import modelo.actividades.Partido;
import modelo.actividades.RegistroAsistencia;
import modelo.actividades.SesionEntrenamiento;
import modelo.comercio.Mensualidad;
import modelo.comercio.Venta;
import modelo.competencias.Competencia;
import modelo.personas.Socio;
import modelo.tipos.EstadoPartido;
import modelo.tipos.Rubro;

/** Construye los reportes de ventas a partir del gestor de comercio. */
public class GestorReportes implements Serializable
{
    private static final long serialVersionUID = 1L;
    private GestorComercio comercio;
    private GestorDeportivo deportivo;
    private GestorCompetencias competencias;
    public GestorReportes(GestorComercio c)
    {
        comercio=c;
    }
    public ReporteVentas generarReporteVentas(LocalDate ini,LocalDate fin) throws ClubException
    {
        ReporteVentas r=new ReporteVentas(ini,fin);
        for(Venta v:comercio.getVentas(ini,fin))r.agregar(v.getItems().get(0).getProducto().getRubro(),YearMonth.from(v.getFechaHora()),v.getTotalPagado());
        for(Mensualidad m:comercio.getMensualidades(ini,fin))r.agregar(Rubro.MENSUALIDADES,YearMonth.from(m.getFechaPago()),m.getValor());
        return r;
    }
    public void configurarDeportivo(GestorDeportivo deportivo, GestorCompetencias competencias)
    {
        this.deportivo = deportivo;
        this.competencias = competencias;
    }
    public List<RegistroAsistencia> consultarAsistencia(Socio socio, LocalDate inicio, LocalDate fin) throws ClubException
    {
        if (deportivo == null || fin.isBefore(inicio)) throw new ClubException("Periodo o gestor inválido");
        List<RegistroAsistencia> resultado = new ArrayList<>();
        for (SesionEntrenamiento sesion : deportivo.getSesiones())
            if (!sesion.getFecha().isBefore(inicio) && !sesion.getFecha().isAfter(fin))
                for (RegistroAsistencia registro : sesion.getRegistros())
                    if (registro.getJugador() == socio) resultado.add(registro);
        return resultado;
    }
    public List<Partido> consultarResultados(LocalDate inicio, LocalDate fin) throws ClubException
    {
        if (deportivo == null || competencias == null || fin.isBefore(inicio)) throw new ClubException("Periodo o gestores inválidos");
        List<Partido> partidos = new ArrayList<>();
        partidos.addAll(deportivo.getPartidosPractica());
        partidos.addAll(deportivo.getPartidosInternos());
        for (Competencia competencia : competencias.getCompetencias()) partidos.addAll(competencia.getPartidos());
        List<Partido> resultado = new ArrayList<>();
        for (Partido partido : partidos)
            if (partido.getEstado() == EstadoPartido.FINALIZADO && !partido.getFecha().isBefore(inicio) && !partido.getFecha().isAfter(fin)) resultado.add(partido);
        return resultado;
    }

}
