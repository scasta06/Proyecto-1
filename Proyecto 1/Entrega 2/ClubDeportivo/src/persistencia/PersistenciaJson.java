package persistencia;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import modelo.personas.*;
import modelo.deportes.*;
import modelo.actividades.*;
import modelo.competencias.*;
import modelo.personal.*;
import modelo.inventario.*;
import modelo.comercio.*;
import modelo.tipos.*;
import logica.*;
import excepciones.*;
/** Archivo JSON versionado con una instantánea binaria Base64 del grafo de objetos. */
public class PersistenciaJson implements IPersistenciaClub {
    public void salvar(ClubDeportivo club,String ruta) {
        Objects.requireNonNull(club);
        java.nio.file.Path destino=java.nio.file.Path.of(ruta).toAbsolutePath();
        try {
            java.nio.file.Files.createDirectories(destino.getParent());
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            try(ObjectOutputStream out=new ObjectOutputStream(bytes)) {
                out.writeObject(club);
            }
            String texto="{\n  \"formato\": \"CDS-1\",\n  \"datos\": \""+Base64.getEncoder().encodeToString(bytes.toByteArray())+"\"\n}\n";
            java.nio.file.Path temp=java.nio.file.Files.createTempFile(destino.getParent(),"club-",".tmp");
            try {
                java.nio.file.Files.writeString(temp,texto,java.nio.charset.StandardCharsets.UTF_8);
                try {
                    java.nio.file.Files.move(temp,destino,java.nio.file.StandardCopyOption.ATOMIC_MOVE,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                catch(java.nio.file.AtomicMoveNotSupportedException e) {
                    java.nio.file.Files.move(temp,destino,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }
            finally {
                java.nio.file.Files.deleteIfExists(temp);
            }
        }
        catch(IOException e) {
            throw new ClubException("No fue posible salvar: "+e.getMessage());
        }
    }
    public ClubDeportivo cargar(String ruta) {
        try {
            String texto=java.nio.file.Files.readString(java.nio.file.Path.of(ruta),java.nio.charset.StandardCharsets.UTF_8);
            java.util.regex.Matcher m=java.util.regex.Pattern.compile("\\A\\s*\\{\\s*\"formato\"\\s*:\\s*\"CDS-1\"\\s*,\\s*\"datos\"\\s*:\\s*\"([A-Za-z0-9+/=]+)\"\\s*\\}\\s*\\z").matcher(texto);
            if(!m.matches())throw new ClubException("Formato de persistencia inválido");
            byte[] datos=Base64.getDecoder().decode(m.group(1));
            try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(datos))) {
                in.setObjectInputFilter(info-> {
                    if(info.depth()>100||info.references()>200000||info.arrayLength()>2000000)return ObjectInputFilter.Status.REJECTED;Class<?> c=info.serialClass();if(c==null)return ObjectInputFilter.Status.UNDECIDED;while(c.isArray())c=c.getComponentType();String n=c.getName();return c.isPrimitive()||n.startsWith("modelo.")||n.startsWith("logica.")||n.startsWith("java.lang.")||n.startsWith("java.util.")||n.startsWith("java.time.")?ObjectInputFilter.Status.ALLOWED:ObjectInputFilter.Status.REJECTED;
                }
                );
                Object o=in.readObject();
                if(!(o instanceof ClubDeportivo))throw new ClubException("Archivo no contiene un club");
                return (ClubDeportivo)o;
            }
        }
        catch(IOException|ClassNotFoundException|IllegalArgumentException e) {
            throw new ClubException("No fue posible cargar: "+e.getMessage());
        }
    }
}
