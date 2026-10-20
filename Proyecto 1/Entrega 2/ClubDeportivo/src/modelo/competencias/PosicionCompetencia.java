package modelo.competencias;

import java.io.Serializable;

/** Fila de la tabla, tanto para el club como para instituciones externas. */
public class PosicionCompetencia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String grupo;
    private int puntos;
    private int jugados;
    private int aFavor;
    private int enContra;
    private boolean eliminada;
    public PosicionCompetencia(String nombre, String grupo)
    {
        this.nombre = nombre;
        this.grupo = grupo;
    }
    void agregar(int puntos, int propio, int rival)
    {
        this.puntos += puntos;
        jugados++;
        aFavor += propio;
        enContra += rival;
    }
    void eliminar() { eliminada = true; }
    public String getNombre() { return nombre; }
    public String getGrupo() { return grupo; }
    public int getPuntos() { return puntos; }
    public int getJugados() { return jugados; }
    public int getDiferencia() { return aFavor-enContra; }
    public boolean isEliminada() { return eliminada; }
}
