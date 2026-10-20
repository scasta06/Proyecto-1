package modelo.competencias;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.actividades.PartidoOficial;
import modelo.deportes.Deporte;
import modelo.tipos.Categoria;
import modelo.tipos.EstadoCompetencia;
import modelo.tipos.EstadoPartido;
import modelo.tipos.FormatoCompetencia;
import modelo.tipos.Resultado;
import modelo.tipos.TipoEstadistica;

public class Competencia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigo;
    private int limiteAmarillas = 2;
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
    private Ranking ranking;
    private Map<String,PosicionCompetencia> posiciones = new LinkedHashMap<>();
    private Set<String> resultadosRegistrados = new HashSet<>();
    private boolean eliminacionIniciada;
    private Set<String> crucesRealizados = new HashSet<>();
    private List<InscripcionCompetencia> inscripciones=new ArrayList<>();
    private List<PartidoOficial> partidos=new ArrayList<>();
    public Competencia(String cod,String nom,String org,Deporte d,Categoria cat,FormatoCompetencia formato,LocalDate ini,LocalDate fin,LocalDate cierre) throws ClubException
    {
        validar(!fin.isBefore(ini)&&!cierre.isAfter(ini),"Fechas inválidas");
        codigo=textoRequerido(cod);
        nombre=textoRequerido(nom);
        organizador=textoRequerido(org);
        deporte=d;
        if (d.getTipoEstadistica() == TipoEstadistica.BALONCESTO)
        {
            puntosVictoria = 2;
            puntosEmpate = 0;
            puntosDerrota = 1;
        }
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
        registrarParticipante(nombreDe(i),"A");
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
    public int getLimiteAmarillas() { return limiteAmarillas == 0 ? 2 : limiteAmarillas; }
    public void setLimiteAmarillas(int limite) throws ClubException
    {
        validar(limite > 0 && estado == EstadoCompetencia.INSCRIPCIONES_ABIERTAS,"Regla de tarjetas inválida o torneo iniciado");
        limiteAmarillas = limite;
    }

    public Ranking getRanking()
    {
        if (ranking == null) ranking = new Ranking(deporte);
        return ranking;
    }

    private void prepararTabla()
    {
        if (posiciones == null) posiciones = new LinkedHashMap<>();
        if (resultadosRegistrados == null) resultadosRegistrados = new HashSet<>();
        if (crucesRealizados == null) crucesRealizados = new HashSet<>();
    }
    public String nombreDe(InscripcionCompetencia inscripcion)
    {
        return inscripcion.getEquipo() == null ? inscripcion.getJugador().getNombre() : inscripcion.getEquipo().getNombre();
    }
    public void registrarParticipante(String nombre, String grupo) throws ClubException
    {
        prepararTabla();
        validar(estado == EstadoCompetencia.INSCRIPCIONES_ABIERTAS && !posiciones.containsKey(nombre),"Participante duplicado o torneo iniciado");
        posiciones.put(textoRequerido(nombre),new PosicionCompetencia(nombre,textoRequerido(grupo)));
    }
    public void asignarGrupo(String nombre, String grupo) throws ClubException
    {
        prepararTabla();
        validar(estado == EstadoCompetencia.INSCRIPCIONES_ABIERTAS && posiciones.containsKey(nombre),"No se puede cambiar el grupo");
        posiciones.put(nombre,new PosicionCompetencia(nombre,textoRequerido(grupo)));
    }
    public boolean tieneParticipante(String nombre) { prepararTabla(); return posiciones.containsKey(nombre); }
    public String getFaseActual()
    {
        return formato == FormatoCompetencia.GRUPOS_Y_ELIMINACION ? (eliminacionIniciada ? "ELIMINACION_DIRECTA" : "GRUPOS") : formato.name();
    }
    public void validarEnfrentamiento(String propio, String rival) throws ClubException
    {
        prepararTabla();
        validar(!propio.equals(rival) && posiciones.containsKey(propio) && posiciones.containsKey(rival),"Participante desconocido");
        PosicionCompetencia a = posiciones.get(propio), b = posiciones.get(rival);
        validar(!a.isEliminada() && !b.isEliminada(),"Participante eliminado");
        if (getFaseActual().equals("GRUPOS")) validar(a.getGrupo().equals(b.getGrupo()),"Rivales de grupos diferentes");
    }
    public void validarMarcador(int propio, int rival) throws ClubException
    {
        validar(propio >= 0 && rival >= 0 && (deporte.isPermiteEmpate() || propio != rival),"Marcador inválido");
        if (getFaseActual().equals("ELIMINACION_DIRECTA")) validar(propio != rival,"Eliminatoria sin ganador");
        if (deporte.getTipoEstadistica() == TipoEstadistica.VOLEIBOL)
            validar(Math.max(propio,rival) == 3 && Math.min(propio,rival) < 3,"Voleibol: marcador por sets, gana quien obtiene tres");
    }
    public int puntosMarcador(int propio, int rival)
    {
        if (deporte.getTipoEstadistica() == TipoEstadistica.VOLEIBOL)
            return propio > rival ? (rival == 2 ? 2 : 3) : (propio == 2 ? 1 : 0);
        return puntosPor(propio > rival ? Resultado.VICTORIA : propio == rival ? Resultado.EMPATE : Resultado.DERROTA);
    }
    public void contabilizar(String id, String propio, String rival, int a, int b) throws ClubException
    {
        validarEnfrentamiento(propio,rival);
        validarMarcador(a,b);
        validar(!resultadosRegistrados.contains(id),"Resultado duplicado");
        String cruce = claveCruce(propio,rival);
        validar(!getFaseActual().equals("GRUPOS") || !crucesRealizados.contains(cruce),"Enfrentamiento de grupos ya registrado");
        posiciones.get(propio).agregar(puntosMarcador(a,b),a,b);
        posiciones.get(rival).agregar(puntosMarcador(b,a),b,a);
        resultadosRegistrados.add(id);
        crucesRealizados.add(cruce);
        if (getFaseActual().equals("ELIMINACION_DIRECTA"))
        {
            String perdedor = a > b ? rival : propio;
            posiciones.get(perdedor).eliminar();
            for (InscripcionCompetencia inscripcion : inscripciones)
                if (nombreDe(inscripcion).equals(perdedor)) inscripcion.eliminar();
        }
        estado = EstadoCompetencia.EN_CURSO;
    }
    public List<PosicionCompetencia> getClasificacionCompleta()
    {
        prepararTabla();
        List<PosicionCompetencia> tabla = new ArrayList<>(posiciones.values());
        Collections.sort(tabla,new Comparator<PosicionCompetencia>()
        {
            @Override
            public int compare(PosicionCompetencia a, PosicionCompetencia b)
            {
                int orden = Integer.compare(b.getPuntos(),a.getPuntos());
                if (orden == 0) orden = Integer.compare(b.getDiferencia(),a.getDiferencia());
                if (orden == 0) orden = a.getNombre().compareTo(b.getNombre());
                return orden;
            }
        });
        return tabla;
    }
    public void iniciarEliminacion(int porGrupo) throws ClubException
    {
        validar(formato == FormatoCompetencia.GRUPOS_Y_ELIMINACION && !eliminacionIniciada && porGrupo > 0,"Transición de fase inválida");
        for (PartidoOficial partido : partidos) validar(partido.getEstado() != EstadoPartido.PROGRAMADO,"Hay partidos de grupos pendientes");
        Map<String,Integer> tamanos = new HashMap<>();
        for (PosicionCompetencia fila : getClasificacionCompleta())
        {
            Integer anterior = tamanos.get(fila.getGrupo());
            tamanos.put(fila.getGrupo(),anterior == null ? 1 : anterior+1);
        }
        validar(!tamanos.isEmpty(),"Sin grupos");
        int total = 0;
        for (Integer tamano : tamanos.values())
        {
            validar(porGrupo < tamano,"Deben existir eliminados en cada grupo");
            total += porGrupo;
        }
        int potencia = 2;
        while (potencia < total) potencia *= 2;
        validar(total >= 2 && potencia == total,"La eliminación requiere 2, 4, 8... clasificados");
        for (PosicionCompetencia fila : getClasificacionCompleta())
            validar(fila.getJugados() >= tamanos.get(fila.getGrupo())-1,"Faltan encuentros de grupos");
        Map<String,Integer> contados = new HashMap<>();
        for (PosicionCompetencia fila : getClasificacionCompleta())
        {
            Integer anterior = contados.get(fila.getGrupo());
            int puesto = anterior == null ? 1 : anterior+1;
            contados.put(fila.getGrupo(),puesto);
            if (puesto > porGrupo)
            {
                fila.eliminar();
                for (InscripcionCompetencia inscripcion : inscripciones)
                    if (nombreDe(inscripcion).equals(fila.getNombre())) inscripcion.eliminar();
            }
        }
        eliminacionIniciada = true;
    }

    private String claveCruce(String uno, String otro)
    {
        String primero = uno.compareTo(otro) < 0 ? uno : otro;
        String segundo = uno.compareTo(otro) < 0 ? otro : uno;
        return getFaseActual() + ":" + primero.length() + ":" + primero + segundo;
    }
    public void validarResultadoNuevo(String id, String propio, String rival, int a, int b) throws ClubException
    {
        validarEnfrentamiento(propio,rival);
        validarMarcador(a,b);
        validar(!resultadosRegistrados.contains(id),"Resultado duplicado");
        validar(!getFaseActual().equals("GRUPOS") || !crucesRealizados.contains(claveCruce(propio,rival)),"Enfrentamiento de grupos ya registrado");
    }

}
