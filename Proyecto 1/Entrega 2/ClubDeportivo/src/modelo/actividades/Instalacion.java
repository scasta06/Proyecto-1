package modelo.actividades;

import excepciones.ClubException;
import excepciones.ReservaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.deportes.Deporte;

public class Instalacion implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private int capacidadMaxima;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean enMantenimiento;
    private boolean techada;
    private Deporte deporte;
    private List<Evento> ocupaciones=new ArrayList<>();
    public Instalacion(String c,String n,int capacidad,LocalTime abre,LocalTime cierra,Deporte d) throws ClubException
    {
        codigo=textoRequerido(c);
        nombre=textoRequerido(n);
        capacidadMaxima=enteroPositivo(capacidad);
        validar(abre.isBefore(cierra),"Horario inválido");
        horaApertura=abre;
        horaCierre=cierra;
        deporte=d;
    }
    public boolean estaDisponible(LocalDate f,LocalTime ini,LocalTime fin)
    {
        if (enMantenimiento || !ini.isBefore(fin) || ini.isBefore(horaApertura) || fin.isAfter(horaCierre))
        {
            return false;
        }
        for (Evento evento : ocupaciones)
        {
            if (evento.bloqueaInstalacion() && evento.seTraslapaCon(f, ini, fin))
            {
                return false;
            }
        }
        return true;
    }
    public boolean admiteCantidad(int n)
    {
        return n>0&&n<=capacidadMaxima;
    }
    public void agregarOcupacion(Evento e) throws ClubException
    {
        if(e.getInstalacion()!=this||!estaDisponible(e.getFecha(),e.getHoraInicio(),e.getHoraFin()))throw new ReservaException("Instalación ocupada, en mantenimiento o fuera de horario");
        ocupaciones.add(e);
    }
    public void liberar(Evento e)
    {
        ocupaciones.remove(e);
    }
    public Deporte getDeporte()
    {
        return deporte;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public String getNombre()
    {
        return nombre;
    }
    public void setEnMantenimiento(boolean v)
    {
        enMantenimiento=v;
    }
    public boolean isEnMantenimiento()
    {
        return enMantenimiento;
    }
    public LocalTime getHoraApertura()
    {
        return horaApertura;
    }
    public LocalTime getHoraCierre()
    {
        return horaCierre;
    }
    public List<Evento> getOcupaciones()
    {
        return copiaLectura(ocupaciones);
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
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
    public void setTechada(boolean valor) { techada = valor; }
    public boolean restringeBebidaCaliente()
    {
        return deporte.esPadel() || (deporte.esTenisMesa() && techada);
    }

}
