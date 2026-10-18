package excepciones;

public class VentaException extends ClubException
{
    private static final long serialVersionUID=1L;
    public VentaException(String msg)
    {
        super(msg);
    }
}
