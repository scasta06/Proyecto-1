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
    private Map<Socio,Instalacion> presentes=new LinkedHashMap<>();
    public void ingresar(Socio s,Instalacion i,LocalDate fecha,LocalTime hora) {
        Validacion.exigir(s.isActivo()&&!s.estaSuspendido(i.getDeporte(),fecha)&&!(s.tieneBebidaCaliente()&&i.restringeBebidaCaliente()),"Ingreso no permitido");
        Validacion.exigir(reservas.stream().anyMatch(r->r.getSocio()==s&&r.getInstalacion()==i&&r.getEstado()==EstadoReserva.CONFIRMADA&&r.getFecha().equals(fecha)&&!hora.isBefore(r.getHoraInicio())&&hora.isBefore(r.getHoraFin())),"No hay reserva vigente");
        presentes.put(s,i);
    }
    public void salir(Socio s) {
        presentes.remove(s);
    }
    public boolean admiteBebidaCaliente(Socio s) {
        Instalacion i=presentes.get(s);
        return i==null||!i.restringeBebidaCaliente();
    }
    private List<Instalacion> instalaciones=new ArrayList<>();
    private List<Reserva> reservas=new ArrayList<>();
    public void registrarInstalacion(Instalacion i) {
        Validacion.exigir(instalaciones.stream().noneMatch(x->x.getCodigo().equals(i.getCodigo())),"Código duplicado");
        instalaciones.add(i);
    }
    public List<Instalacion> getInstalaciones() {
        return Validacion.copia(instalaciones);
    }
    public List<Reserva> getReservas() {
        return Validacion.copia(reservas);
    }
    public Instalacion buscarDisponible(Deporte d,LocalDate f,LocalTime ini,int dur,int n) {
        Validacion.exigir(dur>0&&ini.toSecondOfDay()+dur*60L<86400,"Duración inválida");
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
        Validacion.exigir(reservas.contains(r),"Reserva desconocida");
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
            res.put(i,Validacion.copia(horas));
        }
        return Collections.unmodifiableMap(res);
    }
}
