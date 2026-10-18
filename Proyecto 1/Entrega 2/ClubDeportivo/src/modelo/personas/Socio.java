package modelo.personas;
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
public class Socio extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numeroSocio;
    private LocalDate fechaAfiliacion;
    private boolean activo=true;
    private Map<Deporte,NivelDeportivo> niveles=new LinkedHashMap<>();
    private List<Sancion> sanciones=new ArrayList<>();
    private CuentaFidelizacion cuenta=new CuentaFidelizacion();
    public Socio(String doc,String nom,LocalDate nac,String login,String clave,String numero) {
        super(doc,nom,nac,login,clave);
        numeroSocio=textoRequerido(numero);
        fechaAfiliacion=LocalDate.now();
    }
    public int getNivel(Deporte d) {
        NivelDeportivo n=niveles.get(d);
        if(n==null)throw new InscripcionException("Socio sin nivel en "+d.getNombre());
        return n.getNivel();
    }
    public void asignarNivel(Deporte d,int nivel) {
        niveles.put(Objects.requireNonNull(d),new NivelDeportivo(this,d,nivel));
    }
    public boolean estaSuspendido(Deporte d,LocalDate fecha) {
        return sanciones.stream().anyMatch(s->s.estaVigente(fecha)&&s.aplicaA(d));
    }
    public void agregarSancion(Sancion s) {
        validar(s.getSocio()==this,"Sanción de otro socio");
        sanciones.add(s);
    }
    public CuentaFidelizacion getCuenta() {
        return cuenta;
    }
    public String getTipo() {
        return "SOCIO";
    }
    public boolean isActivo() {
        return activo;
    }
    public void darDeBaja() {
        activo=false;
    }
    public String getNumeroSocio() {
        return numeroSocio;
    }
    public LocalDate getFechaAfiliacion() {
        return fechaAfiliacion;
    }
    public List<Sancion> getSanciones() {
        return copiaLectura(sanciones);
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
