package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.personas.Entrenador;
import modelo.personas.Socio;
import modelo.tipos.EstadoAsistencia;

public class SesionEntrenamiento extends Evento implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String objetivo="Entrenamiento";
    private boolean cancelada;
    private Entrenador entrenador;
    private List<Socio> convocados;
    private Map<Socio,RegistroAsistencia> registros=new LinkedHashMap<>();
    public SesionEntrenamiento(String id,LocalDate f,LocalTime h,int dur,Instalacion i,Entrenador en,List<Socio> socios) throws ClubException
    {
        super(id,f,h,dur,i);
        entrenador=en;
        convocados=new ArrayList<>(socios);
    }
    public boolean estaConvocado(Socio s)
    {
        return convocados.contains(s);
    }
    public RegistroAsistencia registrarAsistencia(Socio s,EstadoAsistencia e,Integer nota,String com) throws ClubException
    {
        validar(!cancelada&&estaConvocado(s),"Jugador no convocado o sesión cancelada");
        RegistroAsistencia r=new RegistroAsistencia(s,e,nota,com);
        registros.put(s,r);
        return r;
    }
    @Override
    public boolean bloqueaInstalacion()
    {
        return !cancelada;
    }
    public Entrenador getEntrenador()
    {
        return entrenador;
    }
    public List<Socio> getConvocados()
    {
        return copiaLectura(convocados);
    }
    public List<RegistroAsistencia> getRegistros()
    {
        return copiaLectura(registros.values());
    }
    public void cancelar()
    {
        cancelada=true;
        getInstalacion().liberar(this);
    }
    public void setObjetivo(String o) throws ClubException
    {
        objetivo=textoRequerido(o);
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
}
