package modelo.deportes;

import excepciones.ClubException;
import excepciones.InscripcionException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.personas.Entrenador;
import modelo.personas.Socio;

public class Academia implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String horario;
    private int nivelMinimo;
    private int nivelMaximo;
    private int cupoMaximo;
    private double valorMensualidad;
    private Deporte deporte;
    private Entrenador entrenador;
    private List<InscripcionAcademia> inscripciones=new ArrayList<>();
    public Academia(String n,Deporte d,Entrenador en,int min,int max,int cupo,String h,double valor) throws ClubException
    {
        validar(!d.isEsDeConjunto()&&en.puedeEntrenar(d)&&min>=1&&max<=3&&max>=min,"Academia inválida");
        nombre=n;
        deporte=d;
        entrenador=en;
        nivelMinimo=min;
        nivelMaximo=max;
        cupoMaximo=enteroPositivo(cupo);
        horario=h;
        valorMensualidad=redondearDinero(valor);
    }
    public boolean tieneCupo()
    {
        int inscritos = 0;
        for (InscripcionAcademia inscripcion : inscripciones)
        {
            if (inscripcion.isActiva() && inscripcion.getAlumno().isActivo())
            {
                inscritos++;
            }
        }
        return inscritos < cupoMaximo;
    }
    public boolean admiteNivel(int n)
    {
        return n>=nivelMinimo&&n<=nivelMaximo;
    }
    public InscripcionAcademia inscribir(Socio s,LocalDate f) throws ClubException
    {
        if (!s.isActivo() || s.estaSuspendido(deporte,f) || !tieneCupo() || !admiteNivel(s.getNivel(deporte)))
        {
            throw new InscripcionException("Inscripción de academia inválida");
        }
        for (InscripcionAcademia anterior : inscripciones)
        {
            if (anterior.isActiva() && anterior.getAlumno() == s)
            {
                throw new InscripcionException("El socio ya está inscrito en la academia");
            }
        }
        InscripcionAcademia inscripcion = new InscripcionAcademia(s, this, f);
        inscripciones.add(inscripcion);
        return inscripcion;
    }
    public Deporte getDeporte()
    {
        return deporte;
    }
    public String getNombre()
    {
        return nombre;
    }
    public double getValorMensualidad()
    {
        return valorMensualidad;
    }
    public Entrenador getEntrenador()
    {
        return entrenador;
    }
    private static void validar(boolean condicion,String mensaje) throws ClubException
    {
        if(!condicion)throw new ClubException(mensaje);
    }
    private static int enteroPositivo(int valor) throws ClubException
    {
        if(valor<=0)throw new ClubException("Debe ser positivo");
        return valor;
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
