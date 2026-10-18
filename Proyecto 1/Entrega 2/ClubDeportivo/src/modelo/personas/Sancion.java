package modelo.personas;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import modelo.deportes.Deporte;
import modelo.tipos.TipoSancion;

public class Sancion implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private String motivo;
    private TipoSancion tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Deporte deporte;
    public Sancion(Socio s,String m,TipoSancion t,LocalDate ini,LocalDate fin,Deporte d) throws ClubException
    {
        validar(!fin.isBefore(ini),"Fechas de sanción invertidas");
        socio=Objects.requireNonNull(s);
        motivo=textoRequerido(m);
        tipo=Objects.requireNonNull(t);
        fechaInicio=ini;
        fechaFin=fin;
        deporte=d;
        validar(t!=TipoSancion.SUSPENSION_DEPORTIVA||d!=null,"Falta deporte");
    }
    public boolean estaVigente(LocalDate f)
    {
        return tipo!=TipoSancion.AMONESTACION&&!f.isBefore(fechaInicio)&&!f.isAfter(fechaFin);
    }
    public boolean aplicaA(Deporte d)
    {
        return tipo==TipoSancion.SUSPENSION_TOTAL||deporte==d;
    }
    public Socio getSocio()
    {
        return socio;
    }
    public String getMotivo()
    {
        return motivo;
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
}
