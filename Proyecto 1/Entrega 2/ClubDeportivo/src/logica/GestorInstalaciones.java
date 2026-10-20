package logica;

import excepciones.ClubException;
import excepciones.ParticipacionException;
import excepciones.ReservaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import modelo.actividades.Instalacion;
import modelo.actividades.Reserva;
import modelo.deportes.Deporte;
import modelo.personas.Socio;
import modelo.tipos.ModalidadReserva;

/** Consulta instalaciones y coordina las reservas y cancelaciones. */
public class GestorInstalaciones implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Instalacion> instalaciones=new ArrayList<>();
    private List<Reserva> reservas=new ArrayList<>();
    public void registrarInstalacion(Instalacion i) throws ClubException
    {
        for (Instalacion registrada : instalaciones)
        {
            validar(!registrada.getCodigo().equals(i.getCodigo()), "Código de instalación duplicado");
        }
        instalaciones.add(i);
    }
    public List<Instalacion> getInstalaciones()
    {
        return copiaLectura(instalaciones);
    }
    public List<Reserva> getReservas()
    {
        return copiaLectura(reservas);
    }
    public Instalacion buscarDisponible(Deporte d,LocalDate f,LocalTime ini,int dur,int n) throws ClubException
    {
        validar(dur > 0 && ini.toSecondOfDay() + dur * 60L < 86400, "Duración inválida");
        for (Instalacion instalacion : instalaciones)
        {
            if (instalacion.getDeporte() == d && instalacion.admiteCantidad(n) && instalacion.estaDisponible(f, ini, ini.plusMinutes(dur)))
            {
                return instalacion;
            }
        }
        throw new ReservaException("No hay instalación disponible");
    }
    public Reserva reservar(Socio s,Deporte d,LocalDate f,LocalTime ini,int dur,int n,ModalidadReserva m) throws ClubException
    {
        if (!s.isActivo() || s.estaSuspendido(d,f)) throw new ParticipacionException("Socio suspendido o inactivo");
        if (!d.cantidadReservaValida(n) || m == null ||
            (d.isEsDeConjunto() && m != ModalidadReserva.GRUPAL) ||
            (!d.isEsDeConjunto() && m == ModalidadReserva.GRUPAL) ||
            (m == ModalidadReserva.INDIVIDUAL && n != 2) ||
            (m == ModalidadReserva.DOBLES && n != 4) || (d.esPadel() && m != ModalidadReserva.DOBLES))
            throw new ReservaException("Modalidad o cantidad de jugadores inválida");
        Instalacion i=buscarDisponible(d,f,ini,dur,n);
        Reserva r=new Reserva(UUID.randomUUID().toString(),s,f,ini,dur,i,n,m);
        i.agregarOcupacion(r);
        reservas.add(r);
        return r;
    }
    public void cancelarReserva(Reserva r) throws ClubException
    {
        validar(reservas.contains(r),"Reserva desconocida");
        r.cancelar();
    }
    public Map<Instalacion,List<LocalTime>> consultarDisponibilidad(Deporte d,LocalDate f)
    {
        Map<Instalacion,List<LocalTime>> res=new LinkedHashMap<>();
        for(Instalacion i:instalaciones)if(i.getDeporte()==d)
        {
            List<LocalTime> horas=new ArrayList<>();
            for(int m=i.getHoraApertura().toSecondOfDay()/60;m+d.getDuracionPartidoMin()<=i.getHoraCierre().toSecondOfDay()/60;m+=30)
            {
                LocalTime h=LocalTime.of(m/60,m%60);
                if(i.estaDisponible(f,h,h.plusMinutes(d.getDuracionPartidoMin())))horas.add(h);
            }
            res.put(i,copiaLectura(horas));
        }
        return new LinkedHashMap<Instalacion, List<LocalTime>>(res);
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores)
    {
        return new ArrayList<T>(valores);
    }
    public void ingresar(Socio socio, Instalacion instalacion) throws ClubException
    {
        if (!instalaciones.contains(instalacion) || instalacion.isEnMantenimiento() || socio.estaSuspendido(instalacion.getDeporte(),LocalDate.now()))
            throw new ReservaException("Instalación o socio no habilitado");
        socio.entrarInstalacion(instalacion);
    }
    public void salir(Socio socio) { socio.salirInstalacion(); }

}
