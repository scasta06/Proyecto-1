package modelo.actividades;

import excepciones.ClubException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import modelo.deportes.Deporte;
import modelo.personas.Entrenador;
import modelo.personas.Persona;
import modelo.personas.Socio;

public class PartidoPractica extends Partido implements Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean nivelesVerificados;
    private Persona rival;
    private Deporte deporte;
    public PartidoPractica(String id,Socio sol,Persona rival,Deporte d,LocalDate f,LocalTime h,Instalacion i) throws ClubException
    {
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
    public boolean incluyeEntrenador()
    {
        return rival instanceof Entrenador;
    }
    @Override
    public Deporte getDeporte()
    {
        return deporte;
    }
    public boolean isNivelesVerificados()
    {
        return nivelesVerificados;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
}
