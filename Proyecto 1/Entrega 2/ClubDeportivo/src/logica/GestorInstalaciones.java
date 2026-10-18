package logica;
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
public class GestorInstalaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Instalacion> instalaciones=new ArrayList<>();
    private List<Reserva> reservas=new ArrayList<>();
    public void registrarInstalacion(Instalacion i) {
        validar(instalaciones.stream().noneMatch(x->x.getCodigo().equals(i.getCodigo())),"Código duplicado");
        instalaciones.add(i);
    }
    public List<Instalacion> getInstalaciones() {
        return copiaLectura(instalaciones);
    }
    public List<Reserva> getReservas() {
        return copiaLectura(reservas);
    }
    public Instalacion buscarDisponible(Deporte d,LocalDate f,LocalTime ini,int dur,int n) {
        validar(dur>0&&ini.toSecondOfDay()+dur*60L<86400,"Duración inválida");
        return instalaciones.stream().filter(i->i.getDeporte()==d&&i.admiteCantidad(n)&&i.estaDisponible(f,ini,ini.plusMinutes(dur))).findFirst().orElseThrow(()->new ReservaException("No hay instalación disponible"));
    }
    public Reserva reservar(Socio s,Deporte d,LocalDate f,LocalTime ini,int dur,int n,ModalidadReserva m) {
        if(!s.isActivo()||s.estaSuspendido(d,f)||!d.cantidadReservaValida(n)||m==null||(m==ModalidadReserva.INDIVIDUAL&&n!=2)||(m==ModalidadReserva.DOBLES&&n!=4))throw new ReservaException("Reserva incompatible con el socio, modalidad o cupo");
        Instalacion i=buscarDisponible(d,f,ini,dur,n);
        Reserva r=new Reserva(UUID.randomUUID().toString(),s,f,ini,dur,i,n,m);
        i.agregarOcupacion(r);
        reservas.add(r);
        return r;
    }
    public void cancelarReserva(Reserva r) {
        validar(reservas.contains(r),"Reserva desconocida");
        r.cancelar();
    }
    public Map<Instalacion,List<LocalTime>> consultarDisponibilidad(Deporte d,LocalDate f) {
        Map<Instalacion,List<LocalTime>> res=new LinkedHashMap<>();
        for(Instalacion i:instalaciones)if(i.getDeporte()==d) {
            List<LocalTime> horas=new ArrayList<>();
            for(int m=i.getHoraApertura().toSecondOfDay()/60;m+d.getDuracionPartidoMin()<=i.getHoraCierre().toSecondOfDay()/60;m+=30) {
                LocalTime h=LocalTime.of(m/60,m%60);
                if(i.estaDisponible(f,h,h.plusMinutes(d.getDuracionPartidoMin())))horas.add(h);
            }
            res.put(i,copiaLectura(horas));
        }
        return Collections.unmodifiableMap(res);
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static <T> List<T> copiaLectura(Collection<T> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
