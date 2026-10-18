package excepciones;

public class ReservaException extends ClubException
{
    private static final long serialVersionUID=1L;
    public ReservaException(String msg)
    {
        super(msg);
    }
}
