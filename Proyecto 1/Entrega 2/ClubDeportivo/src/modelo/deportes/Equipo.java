package modelo.deportes;

import excepciones.ClubException;
import excepciones.InscripcionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.personas.Entrenador;
import modelo.personas.Socio;
import modelo.tipos.Categoria;

public class Equipo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private Categoria categoria;
    private int cupoMaximo;
    private Deporte deporte;
    private Entrenador entrenador;
    private List<InscripcionEquipo> inscripciones=new ArrayList<>();
    public Equipo(String n,Categoria c,int cupo,Deporte d,Entrenador en) throws ClubException
    {
        validar(d.isEsDeConjunto()&&en.puedeEntrenar(d),"Deporte o entrenador no válido");
        nombre=textoRequerido(n);
        categoria=c;
        cupoMaximo=enteroPositivo(cupo);
        deporte=d;
        entrenador=en;
    }
    public boolean tieneCupo()
    {
        return getJugadoresActivos().size()<cupoMaximo;
    }
    public boolean esIntegrante(Socio s)
    {
        return getJugadoresActivos().contains(s);
    }
    public InscripcionEquipo inscribir(Socio s,String pos,int num,LocalDate f) throws ClubException
    {
        if (!s.isActivo() || !tieneCupo() || esIntegrante(s) || Categoria.paraEdad(s.getEdad(f)) != categoria || !deporte.esPosicionValida(pos) || num < 0 || num > 99)
        {
            throw new InscripcionException("Inscripción de equipo inválida");
        }
        for (InscripcionEquipo anterior : inscripciones)
        {
            if (anterior.isActiva() && anterior.getNumeroCamiseta() == num)
            {
                throw new InscripcionException("Número de camiseta ocupado");
            }
        }
        InscripcionEquipo inscripcion = new InscripcionEquipo(s, this, pos, num, f);
        inscripciones.add(inscripcion);
        return inscripcion;
    }
    public List<Socio> getJugadoresActivos()
    {
        List<Socio> jugadores = new ArrayList<Socio>();
        for (InscripcionEquipo inscripcion : inscripciones)
        {
            if (inscripcion.isActiva() && inscripcion.getJugador().isActivo())
            {
                jugadores.add(inscripcion.getJugador());
            }
        }
        return jugadores;
    }
    public Deporte getDeporte()
    {
        return deporte;
    }
    public Categoria getCategoria()
    {
        return categoria;
    }
    public String getNombre()
    {
        return nombre;
    }
    public Entrenador getEntrenador()
    {
        return entrenador;
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
}
