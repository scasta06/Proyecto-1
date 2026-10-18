package persistencia;

import excepciones.ClubException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Base64;
import logica.ClubDeportivo;

/**
 * Implementa el contrato de persistencia del modelo.
 * Combina la serializacion de objetos vista en clase con una envoltura JSON.
 * La serializacion conserva las referencias compartidas entre los objetos.
 */
public class PersistenciaJson implements IPersistenciaClub
{
    @Override
    public void salvar(ClubDeportivo club, String ruta) throws ClubException
    {
        if (club == null)
        {
            throw new ClubException("No hay un club para guardar");
        }
        File archivo = new File(ruta);
        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (!carpeta.exists() && !carpeta.mkdirs())
        {
            throw new ClubException("No se pudo crear la carpeta de datos");
        }
        try
        {
            ByteArrayOutputStream memoria = new ByteArrayOutputStream();
            try (ObjectOutputStream salida = new ObjectOutputStream(memoria))
            {
                salida.writeObject(club);
            }
            // Base64 permite representar los bytes del objeto como texto JSON.
            String datos = Base64.getEncoder().encodeToString(memoria.toByteArray());
            try (PrintWriter escritor = new PrintWriter(new OutputStreamWriter( new FileOutputStream(archivo), "UTF-8")))
            {
                escritor.println("{");
                escritor.println("  \"formato\": \"CDS-1\",");
                escritor.println("  \"datos\": \"" + datos + "\"");
                escritor.println("}");
                if (escritor.checkError())
                {
                    throw new IOException("No se pudo escribir el archivo completo");
                }
            }
        }
        catch (IOException e)
        {
            throw new ClubException("No fue posible guardar el club", e);
        }
    }
    @Override
    public ClubDeportivo cargar(String ruta) throws ClubException
    {
        try (BufferedReader lector = new BufferedReader(new InputStreamReader( new FileInputStream(ruta), "UTF-8")))
        {
            String apertura = lector.readLine();
            String formato = lector.readLine();
            String datos = lector.readLine();
            String cierre = lector.readLine();
            if (apertura == null || formato == null || datos == null || cierre == null || !apertura.trim().equals("{") || !formato.trim().equals("\"formato\": \"CDS-1\",") || !cierre.trim().equals("}") || lector.readLine() != null)
            {
                throw new ClubException("Formato de persistencia incorrecto");
            }
            datos = datos.trim();
            String prefijo = "\"datos\": \"";
            if (!datos.startsWith(prefijo) || !datos.endsWith("\""))
            {
                throw new ClubException("Falta el campo datos del archivo");
            }
            String contenido = datos.substring(prefijo.length(), datos.length() - 1);
            byte[] bytes = Base64.getDecoder().decode(contenido);
            try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes)))
            {
                Object objeto = entrada.readObject();
                if (!(objeto instanceof ClubDeportivo))
                {
                    throw new ClubException("El archivo no contiene un club deportivo");
                }
                return (ClubDeportivo) objeto;
            }
        }
        catch (IOException e)
        {
            throw new ClubException("No fue posible leer el archivo del club", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ClubException("No se encontro una clase guardada en el archivo", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new ClubException("Los datos del archivo no son validos", e);
        }
    }
}
