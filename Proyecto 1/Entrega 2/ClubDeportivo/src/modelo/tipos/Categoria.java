package modelo.tipos;
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
public enum Categoria {
    SUB_13(0,13), SUB_15(14,15), SUB_17(16,17), MAYORES(18,120);
    private final int edadMinima, edadMaxima;
    Categoria(int min,int max) {
        edadMinima=min;
        edadMaxima=max;
    }
    public int getEdadMinima() {
        return edadMinima;
    }
    public int getEdadMaxima() {
        return edadMaxima;
    }
    public static Categoria paraEdad(int edad) {
        for(Categoria c:values()) if(edad>=c.edadMinima && edad<=c.edadMaxima)return c;
        throw new InscripcionException("Edad fuera de rango");
    }
}
