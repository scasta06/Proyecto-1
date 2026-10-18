package modelo.comercio;

import excepciones.ClubException;
import excepciones.VentaException;
import java.io.Serializable;

public class CuentaFidelizacion implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int puntos;
    public static final int PESOS_POR_PUNTO=1000,VALOR_PUNTO=10;
    public int acumular(double pago) throws ClubException
    {
        int n=(int)(redondearDinero(pago)/PESOS_POR_PUNTO);
        puntos+=n;
        return n;
    }
    public boolean puedeRedimir(int p)
    {
        return p>=0&&p<=puntos;
    }
    public double redimir(int p) throws ClubException
    {
        if(!puedeRedimir(p))throw new VentaException("Puntos insuficientes");
        puntos-=p;
        return (double)p*VALOR_PUNTO;
    }
    public int getPuntos()
    {
        return puntos;
    }
    private static double redondearDinero(double valor) throws ClubException
    {
        if(!Double.isFinite(valor)||valor<0)throw new ClubException("Valor monetario inválido");
        return Math.round(valor*100.0)/100.0;
    }
}
