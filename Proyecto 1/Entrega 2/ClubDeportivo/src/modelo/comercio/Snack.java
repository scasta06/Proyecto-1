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
public class Snack extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int calorias;
    private List<Ingrediente> ingredientes;
    public Snack(String c,String n,double p,int cal,List<Ingrediente> ing) {
        super(c,n,p);
        Validacion.exigir(cal>=0&&!ing.isEmpty(),"Snack sin ingredientes o calorías negativas");
        calorias=cal;
        ingredientes=new ArrayList<>(ing);
    }
    public Rubro getRubro() {
        return Rubro.CAFETERIA;
    }
    public List<Ingrediente> getIngredientes() {
        return Validacion.copia(ingredientes);
    }
    public List<Alergeno> getAlergenos() {
        return ingredientes.stream().flatMap(i->i.getAlergenos().stream()).distinct().collect(Collectors.toUnmodifiableList());
    }
    public boolean contieneAlergeno(Alergeno a) {
        return getAlergenos().contains(a);
    }
}
