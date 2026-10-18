package modelo.deportes;
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
public class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private Categoria categoria;
    private int cupoMaximo;
    private Deporte deporte;
    private Entrenador entrenador;
    private List<InscripcionEquipo> inscripciones=new ArrayList<>();
    public Equipo(String n,Categoria c,int cupo,Deporte d,Entrenador en) {
        validar(d.isEsDeConjunto()&&en.puedeEntrenar(d),"Deporte o entrenador no válido");
        nombre=textoRequerido(n);
        categoria=c;
        cupoMaximo=enteroPositivo(cupo);
        deporte=d;
        entrenador=en;
    }
    public boolean tieneCupo() {
        return getJugadoresActivos().size()<cupoMaximo;
    }
    public boolean esIntegrante(Socio s) {
        return getJugadoresActivos().contains(s);
    }
    public InscripcionEquipo inscribir(Socio s,String pos,int num,LocalDate f) {
        if(!s.isActivo()||!tieneCupo()||esIntegrante(s)||Categoria.paraEdad(s.getEdad(f))!=categoria||!deporte.esPosicionValida(pos)||num<0||num>99||inscripciones.stream().anyMatch(i->i.isActiva()&&i.getNumeroCamiseta()==num))throw new InscripcionException("Inscripción de equipo inválida");
        InscripcionEquipo i=new InscripcionEquipo(s,this,pos,num,f);
        inscripciones.add(i);
        return i;
    }
    public List<Socio> getJugadoresActivos() {
        return inscripciones.stream().filter(i->i.isActiva()&&i.getJugador().isActivo()).map(InscripcionEquipo::getJugador).collect(Collectors.toUnmodifiableList());
    }
    public Deporte getDeporte() {
        return deporte;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public String getNombre() {
        return nombre;
    }
    public Entrenador getEntrenador() {
        return entrenador;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static String textoRequerido(String valor) {
        if(valor==null||valor.isBlank())throw new ClubException("Texto obligatorio");
        return valor.trim();
    }
    private static int enteroPositivo(int valor) {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
}
