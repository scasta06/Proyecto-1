package pruebas;

import excepciones.ClubException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import logica.ClubDeportivo;
import logica.GestorComercio;
import logica.GestorCompetencias;
import logica.GestorDeportivo;
import logica.GestorInstalaciones;
import logica.GestorPersonal;
import logica.GestorUsuarios;
import logica.ReporteVentas;
import modelo.actividades.PartidoOficial;
import modelo.actividades.PartidoPractica;
import modelo.actividades.Reserva;
import modelo.actividades.SesionEntrenamiento;
import modelo.comercio.Mensualidad;
import modelo.comercio.Producto;
import modelo.comercio.Snack;
import modelo.comercio.Venta;
import modelo.competencias.Competencia;
import modelo.competencias.InscripcionCompetencia;
import modelo.deportes.Academia;
import modelo.deportes.Deporte;
import modelo.inventario.Inventario;
import modelo.inventario.Traslado;
import modelo.inventario.Ubicacion;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Turno;
import modelo.personas.Administrador;
import modelo.personas.Entrenador;
import modelo.personas.Sancion;
import modelo.personas.Socio;
import modelo.tipos.Categoria;
import modelo.tipos.ConceptoMensualidad;
import modelo.tipos.EstadoAsistencia;
import modelo.tipos.EstadoReserva;
import modelo.tipos.EstadoSolicitud;
import modelo.tipos.FormatoCompetencia;
import modelo.tipos.ModalidadReserva;
import modelo.tipos.Resultado;
import modelo.tipos.TipoSancion;
import modelo.tipos.TipoUbicacion;
import persistencia.IPersistenciaClub;
import persistencia.PersistenciaJson;

/** Demuestra las operaciones del modelo sin solicitar entrada manual. */
public final class PruebasSistema
{
    private static int correctas;
    private interface OperacionPrueba
    {
        void ejecutar() throws ClubException;
    }
    private static void verificar(boolean condicion,String caso)
    {
        if(!condicion)throw new AssertionError(caso);
        System.out.println("OK "+(++correctas)+" - "+caso);
    }
    private static void rechazar(OperacionPrueba operacion,String caso)
    {
        try
        {
            operacion.ejecutar();
        }
        catch(ClubException e)
        {
            verificar(true,caso);
            return;
        }
        throw new AssertionError("Se esperaba rechazo: "+caso);
    }
    public static void main(String[] args) throws ClubException
    {
        ClubDeportivo club=DatosPrueba.crear();
        GestorUsuarios usuarios=club.getGestorUsuarios();
        GestorDeportivo deportivo=club.getGestorDeportivo();
        GestorInstalaciones instalaciones=club.getGestorInstalaciones();
        GestorComercio comercio=club.getGestorComercio();
        GestorCompetencias competencias=club.getGestorCompetencias();
        Socio socio=usuarios.buscarSocio("S1"),rival=usuarios.buscarSocio("S2");
        Entrenador entrenador=usuarios.getEntrenadores().get(0);
        Administrador admin=(Administrador)club.iniciarSesion("admin","demo123");
        Deporte tenis=deportivo.getDeportes().get(3),futbol=deportivo.getDeportes().get(0);
        LocalDate fecha=LocalDate.now().plusDays(1);
        LocalTime hora=LocalTime.of(8,0);
        verificar(club.iniciarSesion("socio1","demo123")==socio,"Autenticar socio");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                club.iniciarSesion("socio1","otra");
            }
        }
        ,"Contraseña incorrecta");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                usuarios.registrarSocio(socio);
            }
        }
        ,"Documento y login duplicados");
        verificar(Categoria.paraEdad(13)==Categoria.SUB_13&&Categoria.paraEdad(14)==Categoria.SUB_15&&Categoria.paraEdad(18)==Categoria.MAYORES,"Categorías del PDF");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                socio.asignarNivel(tenis,11);
            }
        }
        ,"Nivel fuera de 1..3");
        Reserva reserva=instalaciones.reservar(socio,tenis,fecha,hora,60,2,ModalidadReserva.INDIVIDUAL);
        verificar(reserva.bloqueaInstalacion(),"Reserva confirmada");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                instalaciones.reservar(rival,tenis,fecha,hora,60,2,ModalidadReserva.INDIVIDUAL);
            }
        }
        ,"Reservas superpuestas");
        Reserva contigua=instalaciones.reservar(rival,tenis,fecha,hora.plusHours(1),60,2,ModalidadReserva.INDIVIDUAL);
        verificar(contigua.bloqueaInstalacion(),"Reservas contiguas");
        instalaciones.cancelarReserva(reserva);
        verificar(reserva.getEstado()==EstadoReserva.CANCELADA&&reserva.getInstalacion().estaDisponible(fecha,hora,hora.plusHours(1)),"Cancelación libera instalación");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                instalaciones.reservar(socio,tenis,fecha,LocalTime.of(23,0),60,2,ModalidadReserva.INDIVIDUAL);
            }
        }
        ,"Reserva fuera de horario");
        verificar(!instalaciones.consultarDisponibilidad(tenis,fecha).isEmpty(),"Consultar disponibilidad");
        Sancion sancion=usuarios.sancionar(socio,"Sanción de prueba",TipoSancion.SUSPENSION_DEPORTIVA,fecha,fecha,tenis);
        verificar(sancion.estaVigente(fecha)&&sancion.aplicaA(tenis)&&!socio.estaSuspendido(futbol,fecha),"Sanción por deporte y fecha");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                instalaciones.reservar(socio,tenis,fecha,hora,60,2,ModalidadReserva.INDIVIDUAL);
            }
        }
        ,"Socio suspendido no reserva");
        PartidoPractica practica=deportivo.programarPartidoPractica(socio,rival,tenis,fecha.plusDays(1),hora);
        verificar(practica.participo(socio)&&practica.participo(rival),"Participantes de práctica");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                deportivo.programarPartidoPractica(socio,usuarios.buscarSocio("S16"),tenis,fecha.plusDays(2),hora);
            }
        }
        ,"Niveles incompatibles");
        practica.registrarResultado(2,0);
        deportivo.registrarEstadistica(entrenador,practica,socio,crearValores(new String[]
        {
            "puntosAnotados", "gano"
        }
        , new int[]
        {
            12, 1
        }
        ));
        verificar(deportivo.consultarRanking(tenis).get(0).getPuntosRanking()==1016,"Actualizar ranking");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                deportivo.registrarEstadistica(entrenador,practica,socio,crearValores(new String[]
                {
                    "gano"
                }
                , new int[]
                {
                    1
                }
                ));
            }
        }
        ,"Estadística repetida");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                tenis.crearEstadistica(rival,practica,crearValores(new String[]
                {
                    "otro"
                }
                , new int[]
                {
                    2
                }
                ));
            }
        }
        ,"Estadística ajena a atributos del PDF");
        SesionEntrenamiento sesion=deportivo.programarEntrenamiento(entrenador,fecha.plusDays(1),LocalTime.of(12,0),60,practica.getInstalacion(),Arrays.asList(socio,rival));
        deportivo.registrarAsistencia(entrenador,sesion,socio,EstadoAsistencia.ASISTIO,5,"Buen desempeño");
        verificar(sesion.getRegistros().get(0).tieneNota(),"Asistencia con evaluación opcional");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                deportivo.registrarAsistencia(entrenador,sesion,rival,EstadoAsistencia.FALTO,6,null);
            }
        }
        ,"Nota fuera de 1..5");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                deportivo.inscribirEnEquipo(socio,deportivo.getEquipos().get(0),"Defensa",20);
            }
        }
        ,"Una inscripción en equipo por deporte");
        verificar(deportivo.getAcademias().get(0).admiteNivel(socio.getNivel(tenis)),"Academia admite nivel");
        Producto balon=comercio.getProductos().get(0);
        Venta venta=comercio.iniciarVenta(comercio.getTienda(),socio);
        comercio.agregarProducto(venta,balon,1);
        comercio.aplicarCodigoDescuento(venta,entrenador.getCodigoDescuento());
        comercio.finalizarVenta(venta,0,0);
        verificar(venta.getDescuento()==8000&&venta.getImpuesto()==17480&&venta.getTotalPagado()==109480,"Descuento de socio e IVA del modelo");
        verificar(socio.getCuenta().getPuntos()==2189,"Acumulación de fidelización");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                comercio.finalizarVenta(venta,0,0);
            }
        }
        ,"Venta cerrada no se procesa dos veces");
        Venta empleado=comercio.iniciarVenta(comercio.getTienda(),entrenador);
        comercio.agregarProducto(empleado,balon,1);
        comercio.finalizarVenta(empleado,0,0);
        verificar(empleado.getDescuento()==15000&&empleado.getTotalPagado()==101150,"Descuento de empleado");
        verificar(comercio.getInventario().getCantidad(balon,comercio.getTienda().getUbicacion())==8,"Inventario tras ventas");
        Venta sinStock=comercio.iniciarVenta(comercio.getTienda(),socio);
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                comercio.agregarProducto(sinStock,balon,100);
            }
        }
        ,"Cantidad supera inventario");
        Snack snack=(Snack)comercio.getProductos().get(1);
        Venta cafeteria=comercio.iniciarVenta(comercio.getCafeteria(),socio);
        comercio.agregarProducto(cafeteria,snack,1);
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                comercio.finalizarVenta(cafeteria,0,0);
            }
        }
        ,"Informar alérgenos antes de venta");
        cafeteria.marcarAlergenosInformados();
        comercio.finalizarVenta(cafeteria,500,10);
        verificar(cafeteria.getTotalPagado()==5890,"Impoconsumo, propina y redención");
        verificar(comercio.consultarIngredientes(snack).size()==1&&comercio.buscarSnacksConAlergeno(snack.getAlergenos().get(0)).contains(snack),"Ingredientes y alérgenos");
        Ubicacion bodega=new Ubicacion("B","Bodega",TipoUbicacion.BODEGA);
        comercio.getInventario().trasladar(balon,comercio.getTienda().getUbicacion(),bodega,2,admin);
        verificar(comercio.getInventario().getCantidad(balon,bodega)==2,"Traslado de inventario");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                comercio.getInventario().trasladar(balon,bodega,comercio.getTienda().getUbicacion(),3,admin);
            }
        }
        ,"Traslado sin existencias");
        Competencia competencia=new Competencia("C1","Competencia de prueba","Organizador",futbol,Categoria.MAYORES,FormatoCompetencia.LIGA,fecha,fecha.plusDays(10),LocalDate.now());
        competencias.registrarCompetencia(competencia);
        competencias.registrarRival(competencia,"Rival externo","A");
        InscripcionCompetencia inscripcion=competencias.inscribirEquipo(competencia,deportivo.getEquipos().get(0),deportivo.getEquipos().get(0).getJugadoresActivos());
        PartidoOficial partido=competencias.programarPartido(competencia,inscripcion,"Rival externo",fecha,LocalTime.of(14,0),true);
        competencias.registrarResultado(partido,2,1);
        deportivo.registrarEstadistica(entrenador,partido,socio,crearValores(new String[]
        {
            "goles", "asistencias"
        }
        , new int[]
        {
            2, 1
        }
        ));
        verificar(partido.getPuntosObtenidos()==3&&competencias.consultarTabla(competencia).get(0).getPuntosAcumulados()==3,"Resultado oficial y tabla");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                competencias.registrarResultado(partido,0,0);
            }
        }
        ,"Resultado oficial duplicado");
        GestorPersonal personal=club.getGestorPersonal();
        verificar(personal.coberturaMinimaCumplida(personal.getTurnos(),fecha),"Cobertura mínima de personal");
        Turno actual=personal.getTurnos().get(0);
        SolicitudCambioTurno solicitud=personal.solicitarCambio(entrenador,actual,new Turno("T2",fecha,LocalTime.of(7,0),LocalTime.of(22,0),entrenador),"Cambio de prueba");
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                personal.aprobarCambio(solicitud,admin);
            }
        }
        ,"Cambio con cobertura insuficiente");
        verificar(solicitud.getEstado()==EstadoSolicitud.PENDIENTE&&personal.getTurnos().contains(actual),"Solicitud rechazada no altera turnos");
        personal.rechazarCambio(solicitud,admin,"Sin cobertura");
        verificar(solicitud.getEstado()==EstadoSolicitud.RECHAZADA,"Respuesta de solicitud");
        SolicitudCambioTurno valida=personal.solicitarCambio(entrenador,actual,new Turno("T3",fecha,LocalTime.of(6,0),LocalTime.of(22,0),entrenador),"Cobertura igual");
        personal.aprobarCambio(valida,admin);
        verificar(valida.getEstado()==EstadoSolicitud.APROBADA,"Cambio válido aprobado");
        comercio.registrarMensualidad(socio,YearMonth.now(),ConceptoMensualidad.AFILIACION,50000);
        rechazar(new OperacionPrueba()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                comercio.registrarMensualidad(socio,YearMonth.now(),ConceptoMensualidad.AFILIACION,50000);
            }
        }
        ,"Mensualidad duplicada");
        ReporteVentas reporte=club.getGestorReportes().generarReporteVentas(LocalDate.now(),LocalDate.now());
        verificar(reporte.getTransacciones()==4&&reporte.getTotal()==266520,"Reporte por rubro y periodo");
        try
        {
            File carpeta = File.createTempFile("modelo-pdf-", "");
            if (!carpeta.delete() || !carpeta.mkdir())
            {
                throw new IOException("No se pudo crear la carpeta temporal de prueba");
            }
            String archivo=new File(carpeta, "club.json").getPath();
            IPersistenciaClub persistencia=new PersistenciaJson();
            persistencia.salvar(club,archivo);
            ClubDeportivo recuperado=persistencia.cargar(archivo);
            Socio guardado=recuperado.getGestorUsuarios().buscarSocio("S1");
            verificar(recuperado.iniciarSesion("socio1","demo123")==guardado,"Carga de usuarios y credenciales");
            verificar(recuperado.getGestorDeportivo().getEquipos().get(0).esIntegrante(guardado),"Carga conserva relaciones");
            verificar(recuperado.getGestorComercio().getVentas(LocalDate.now(),LocalDate.now()).size()==3,"Carga conserva ventas");
            try (PrintWriter escritor = new PrintWriter(new File(carpeta, "invalido.json")))
            {
                escritor.println("{}");
            }
            rechazar(new OperacionPrueba()
            {
                @Override
                public void ejecutar() throws ClubException
                {
                    persistencia.cargar(new File(carpeta, "invalido.json").getPath());
                }
            }
            ,"Archivo de formato incorrecto");
            if (!new File(carpeta, "invalido.json").delete()) throw new IOException("No se eliminó el archivo temporal");
            if (!new File(carpeta, "club.json").delete()) throw new IOException("No se eliminó el archivo temporal");
            if (!carpeta.delete()) throw new IOException("No se eliminó la carpeta temporal");
        }
        catch(IOException e)
        {
            throw new AssertionError(e);
        }
        System.out.println("RESULTADO BASE: "+correctas+" verificaciones correctas.");
        PruebasRevision.main(args);
    }
    private static Map<String, Integer> crearValores(String[] nombres, int[] valores)
    {
        Map<String, Integer> resultado = new HashMap<String, Integer>();
        for (int i = 0; i < nombres.length; i++)
        {
            resultado.put(nombres[i], valores[i]);
        }
        return resultado;
    }
}
