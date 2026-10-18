package modelo.comercio;
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
public class ArticuloDeportivo extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String marca,talla;
    public ArticuloDeportivo(String c,String n,double p,String marca,String talla) {
        super(c,n,p);
        this.marca=marca;
        this.talla=talla;
    }
    public Rubro getRubro() {
        return Rubro.ARTICULOS_DEPORTIVOS;
    }
    public String getMarca() {
        return marca;
    }
    public String getTalla() {
        return talla;
    }
}
