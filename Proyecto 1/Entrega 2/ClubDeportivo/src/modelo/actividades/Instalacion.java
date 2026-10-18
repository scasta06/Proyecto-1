package modelo.actividades;
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
public class Instalacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean techada;
    public boolean isTechada() {
        return techada;
    }
    public void setTechada(boolean t) {
        techada=t;
    }
    public boolean restringeBebidaCaliente() {
        return deporte.getNombre().equalsIgnoreCase("Pádel")||(techada&&deporte.getNombre().equalsIgnoreCase("Tenis de mesa"));
    }
    private String codigo,nombre;
    private int capacidadMaxima;
    private LocalTime horaApertura,horaCierre;
    private boolean enMantenimiento;
    private Deporte deporte;
    private List<Evento> ocupaciones=new ArrayList<>();
    public Instalacion(String c,String n,int capacidad,LocalTime abre,LocalTime cierra,Deporte d) {
        codigo=Validacion.texto(c);
        nombre=Validacion.texto(n);
        capacidadMaxima=Validacion.positivo(capacidad);
        Validacion.exigir(abre.isBefore(cierra),"Horario inválido");
        horaApertura=abre;
        horaCierre=cierra;
        deporte=d;
    }
    public boolean estaDisponible(LocalDate f,LocalTime ini,LocalTime fin) {
        return !enMantenimiento&&ini.isBefore(fin)&&!ini.isBefore(horaApertura)&&!fin.isAfter(horaCierre)&&ocupaciones.stream().noneMatch(e->e.bloqueaInstalacion()&&e.seTraslapaCon(f,ini,fin));
    }
    public boolean admiteCantidad(int n) {
        return n>0&&n<=capacidadMaxima;
    }
    public void agregarOcupacion(Evento e) {
        if(e.getInstalacion()!=this||!estaDisponible(e.getFecha(),e.getHoraInicio(),e.getHoraFin()))throw new ReservaException("Instalación ocupada, en mantenimiento o fuera de horario");
        ocupaciones.add(e);
    }
    public void liberar(Evento e) {
        ocupaciones.remove(e);
    }
    public Deporte getDeporte() {
        return deporte;
    }
    public String getCodigo() {
        return codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setEnMantenimiento(boolean v) {
        enMantenimiento=v;
    }
    public boolean isEnMantenimiento() {
        return enMantenimiento;
    }
    public LocalTime getHoraApertura() {
        return horaApertura;
    }
    public LocalTime getHoraCierre() {
        return horaCierre;
    }
    public List<Evento> getOcupaciones() {
        return Validacion.copia(ocupaciones);
    }
}
