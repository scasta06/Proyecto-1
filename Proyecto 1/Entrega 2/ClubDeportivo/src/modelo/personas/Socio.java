package modelo.personas;

import excepciones.ClubException;
import excepciones.InscripcionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import modelo.comercio.CuentaFidelizacion;
import modelo.deportes.Deporte;

public class Socio extends Persona implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String numeroSocio;
    private LocalDate fechaAfiliacion;
    private boolean activo=true;
    private Map<Deporte,NivelDeportivo> niveles=new LinkedHashMap<>();
    private List<Sancion> sanciones=new ArrayList<>();
    private CuentaFidelizacion cuenta=new CuentaFidelizacion();
    public Socio(String doc,String nom,LocalDate nac,String login,String clave,String numero) throws ClubException
    {
        super(doc,nom,nac,login,clave);
        numeroSocio=textoRequerido(numero);
        fechaAfiliacion=LocalDate.now();
    }
    public int getNivel(Deporte d) throws ClubException
    {
        NivelDeportivo n=niveles.get(d);
        if(n==null)throw new InscripcionException("Socio sin nivel en "+d.getNombre());
        return n.getNivel();
    }
    public void asignarNivel(Deporte d,int nivel) throws ClubException
    {
        niveles.put(Objects.requireNonNull(d),new NivelDeportivo(this,d,nivel));
    }
    public boolean estaSuspendido(Deporte d,LocalDate fecha)
    {
        for (Sancion sancion : sanciones)
        {
            if (sancion.estaVigente(fecha) && sancion.aplicaA(d))
            {
                return true;
            }
        }
        return false;
    }
    public void agregarSancion(Sancion s) throws ClubException
    {
        validar(s.getSocio()==this,"Sanción de otro socio");
        sanciones.add(s);
    }
    public CuentaFidelizacion getCuenta()
    {
        return cuenta;
    }
    @Override
    public String getTipo()
    {
        return "SOCIO";
    }
    public boolean isActivo()
    {
        return activo;
    }
    public void darDeBaja()
    {
        activo=false;
    }
    public String getNumeroSocio()
    {
        return numeroSocio;
    }
    public LocalDate getFechaAfiliacion()
    {
        return fechaAfiliacion;
    }
    public List<Sancion> getSanciones()
    {
        return copiaLectura(sanciones);
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
