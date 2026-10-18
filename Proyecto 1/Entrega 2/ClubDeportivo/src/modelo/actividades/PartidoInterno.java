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
public class PartidoInterno extends Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    private Equipo equipo,rival;
    public PartidoInterno(String id,Equipo e,Equipo r,LocalDate f,LocalTime h,Instalacion i) {
        super(id,f,h,e.getDeporte().getDuracionPartidoMin(),i);
        Validacion.exigir(e!=r&&e.isActivo()&&r.isActivo()&&e.getDeporte()==r.getDeporte()&&e.getCategoria()==r.getCategoria(),"Equipos incompatibles");
        equipo=e;
        rival=r;
    }
    public void agregarParticipante(Socio s) {
        Validacion.exigir(equipo.esIntegrante(s)||rival.esIntegrante(s),"Jugador ajeno a los equipos");
        super.agregarParticipante(s);
    }
    public Deporte getDeporte() {
        return equipo.getDeporte();
    }
    public Equipo getEquipo() {
        return equipo;
    }
    public Equipo getRival() {
        return rival;
    }
    public void registrarResultado(int propio,int otro) {
        long a=participantes.stream().filter(equipo::esIntegrante).filter(s->s.isActivo()&&!s.estaSuspendido(getDeporte(),fecha)).count();
        long b=participantes.stream().filter(rival::esIntegrante).filter(s->s.isActivo()&&!s.estaSuspendido(getDeporte(),fecha)).count();
        Validacion.exigir(a>=getDeporte().getJugadoresEnCampo()&&b>=getDeporte().getJugadoresEnCampo(),"Equipo sin mínimo habilitado");
        super.registrarResultado(propio,otro);
    }
}
