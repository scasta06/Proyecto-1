package excepciones;

public class InventarioException extends ClubException
{
    private static final long serialVersionUID=1L;
    public InventarioException(String msg)
    {
        super(msg);
    }
}
