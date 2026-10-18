package modelo.competencias;
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
public class Ranking implements Serializable {
    private static final long serialVersionUID = 1L;
    private Deporte deporte;
    private LocalDate ultimaActualizacion;
    private Map<Socio,EntradaRanking> entradas=new LinkedHashMap<>();
    private Set<Estadistica> procesadas=new HashSet<>();
    public Ranking(Deporte d) {
        deporte=d;
    }
    public void actualizar(Estadistica e) {
        validar(e.getPartido().getDeporte()==deporte&&!procesadas.contains(e),"Estadística repetida o de otra disciplina");
        getEntrada(e.getJugador()).acumular(e);
        procesadas.add(e);
        ultimaActualizacion=LocalDate.now();
    }
    public EntradaRanking getEntrada(Socio s) {
        return entradas.computeIfAbsent(s,EntradaRanking::new);
    }
    public List<EntradaRanking> getPosiciones() {
        return entradas.values().stream().sorted(Comparator.comparingInt(EntradaRanking::getPuntosRanking).reversed().thenComparing(e->e.getJugador().getDocumento())).collect(Collectors.toUnmodifiableList());
    }
    private static void validar(boolean condicion,String mensaje) {
        if(!condicion)throw new ClubException(mensaje);
    }
}
