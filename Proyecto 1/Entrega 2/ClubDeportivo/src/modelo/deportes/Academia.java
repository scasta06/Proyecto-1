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
public class Academia implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre,horario;
    private int nivelMinimo,nivelMaximo,cupoMaximo;
    private double valorMensualidad;
    private Deporte deporte;
    private Entrenador entrenador;
    private List<InscripcionAcademia> inscripciones=new ArrayList<>();
    public Academia(String n,Deporte d,Entrenador en,int min,int max,int cupo,String h,double valor) {
        validar(!d.isEsDeConjunto()&&en.puedeEntrenar(d)&&min>=1&&max<=10&&max>=min,"Academia inválida");
        nombre=n;
        deporte=d;
        entrenador=en;
        nivelMinimo=min;
        nivelMaximo=max;
        cupoMaximo=enteroPositivo(cupo);
        horario=h;
        valorMensualidad=redondearDinero(valor);
    }
    public boolean tieneCupo() {
        return inscripciones.stream().filter(i->i.isActiva()&&i.getAlumno().isActivo()).count()<cupoMaximo;
    }
    public boolean admiteNivel(int n) {
        return n>=nivelMinimo&&n<=nivelMaximo;
    }
    public InscripcionAcademia inscribir(Socio s,LocalDate f) {
        if(!s.isActivo()||!tieneCupo()||!admiteNivel(s.getNivel(deporte))||inscripciones.stream().anyMatch(i->i.isActiva()&&i.getAlumno()==s))throw new InscripcionException("Inscripción de academia inválida");
        InscripcionAcademia i=new InscripcionAcademia(s,this,f);
        inscripciones.add(i);
        return i;
    }
    public Deporte getDeporte() {
        return deporte;
    }
    public String getNombre() {
        return nombre;
    }
    public double getValorMensualidad() {
        return valorMensualidad;
    }
    public Entrenador getEntrenador() {
        return entrenador;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static int enteroPositivo(int valor) {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    private static double redondearDinero(double valor) {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
