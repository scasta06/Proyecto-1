package persistencia;

import excepciones.ClubException;
import logica.ClubDeportivo;

public interface IPersistenciaClub
{
    ClubDeportivo cargar(String ruta) throws ClubException;
    void salvar(ClubDeportivo club,String ruta) throws ClubException;
}
