package excepciones;

/** Excepcion verificada del club. Se captura o se propaga con throws. */
public class ClubException extends Exception
{
    private static final long serialVersionUID=1L;
    public ClubException(String msg)
    {
        super(msg);
    }
    public ClubException(String mensaje, Throwable causa)
    {
        super(mensaje, causa);
    }
}
