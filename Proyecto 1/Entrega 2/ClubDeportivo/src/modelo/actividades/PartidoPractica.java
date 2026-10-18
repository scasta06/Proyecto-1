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
public class PartidoPractica extends Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean nivelesVerificados;
    private Persona rival;
    private Deporte deporte;
    public PartidoPractica(String id,Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime h,Instalacion i) {
        super(id,f,h,d.getDuracionPartidoMin(),i);
        validar(sol!=rival&&(rival instanceof Socio||rival instanceof Entrenador),"Rival inválido");
        this.rival=rival;
        deporte=d;
        if(rival instanceof Socio)validar(d.nivelesCompatibles(sol.getNivel(d),((Socio)rival).getNivel(d)),"Niveles incompatibles");
        else validar(((Entrenador)rival).puedeEntrenar(d),"Entrenador sin especialidad");
        nivelesVerificados=true;
        agregarParticipante(sol);
        if(rival instanceof Socio)agregarParticipante((Socio)rival);
    }
    public boolean incluyeEntrenador() {
        return rival instanceof Entrenador;
    }
    public Deporte getDeporte() {
        return deporte;
    }
    public boolean isNivelesVerificados() {
        return nivelesVerificados;
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
}
