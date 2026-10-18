package pruebas;
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
/** Programas de demostración y regresión sin dependencias externas. */
public final class PruebasSistema {
    private static int exitos;
    private static void comprobar(boolean c,String n) {
        if(!c)throw new AssertionError(n);
        exitos++;
        System.out.println("OK "+exitos+" - "+n);
    }
    private static void rechaza(Runnable r,String n) {
        try {
            r.run();
        }
        catch(ClubException e) {
            comprobar(true,n);
            return;
        }
        throw new AssertionError("Debía rechazar: "+n);
    }
    public static void main(String[] args) {
        ClubDeportivo c=DatosPrueba.crear();
        GestorUsuarios u=c.getGestorUsuarios();
        GestorDeportivo g=c.getGestorDeportivo();
        GestorInstalaciones ins=c.getGestorInstalaciones();
        GestorComercio com=c.getGestorComercio();
        Socio s=u.buscarSocio("S1"),s2=u.buscarSocio("S2");
        Entrenador e=u.getEntrenadores().get(0);
        Administrador a=(Administrador)c.iniciarSesion("admin","demo123");
        Deporte tenis=g.getDeportes().get(3),futbol=g.getDeportes().get(0);
        LocalDate f=LocalDate.now().plusDays(1);
        LocalTime h=LocalTime.of(8,0);
        comprobar(c.iniciarSesion("socio1","demo123")==s,"Autenticación");
        rechaza(()->c.iniciarSesion("socio1","incorrecta"),"Clave incorrecta");
        rechaza(()->u.registrarSocio(s),"Usuario duplicado");
        comprobar(Categoria.paraEdad(13)==Categoria.SUB_13&&Categoria.paraEdad(14)==Categoria.SUB_15&&Categoria.paraEdad(18)==Categoria.MAYORES,"Límites de categorías del modelo");
        rechaza(()->s.asignarNivel(tenis,11),"Nivel fuera de 1..10");
        Reserva reserva=ins.reservar(s,tenis,f,h,60,2,ModalidadReserva.INDIVIDUAL);
        comprobar(reserva.bloqueaInstalacion(),"Reserva ocupa instalación");
        rechaza(()->ins.reservar(s2,tenis,f,h,60,2,ModalidadReserva.INDIVIDUAL),"Reserva superpuesta");
        Reserva adyacente=ins.reservar(s2,tenis,f,h.plusHours(1),60,2,ModalidadReserva.INDIVIDUAL);
        comprobar(adyacente.bloqueaInstalacion(),"Reservas contiguas permitidas");
        ins.cancelarReserva(reserva);
        comprobar(reserva.getEstado()==EstadoReserva.CANCELADA&&reserva.getInstalacion().estaDisponible(f,h,h.plusHours(1)),"Cancelación libera instalación");
        rechaza(()->ins.cancelarReserva(reserva),"Cancelación repetida");
        rechaza(()->ins.reservar(s,tenis,f,LocalTime.of(23,0),60,2,ModalidadReserva.INDIVIDUAL),"Reserva fuera de horario");
        rechaza(()->ins.reservar(s,tenis,f,h,60,3,ModalidadReserva.DOBLES),"Modalidad y cupo incompatibles");
        PartidoPractica practica=g.programarPartidoPractica(s,s2,tenis,f,h);
        comprobar(practica.participo(s)&&practica.participo(s2),"Partido de práctica");
        rechaza(()->g.programarPartidoPractica(s,u.buscarSocio("S16"),tenis,f,h.plusHours(3)),"Niveles incompatibles");
        practica.registrarResultado(2,0);
        g.registrarEstadistica(e,practica,s,Map.of("gano",1,"sets",2,"games",12,"aces",3));
        comprobar(g.consultarRanking(tenis).get(0).getPuntosRanking()==1020,"Ranking individual actualizado");
        rechaza(()->g.registrarEstadistica(e,practica,s,Map.of("gano",1)),"Estadística duplicada");
        rechaza(()->practica.registrarResultado(1,0),"Resultado repetido");
        SesionEntrenamiento sesion=g.programarEntrenamiento(e,f,LocalTime.of(12,0),60,practica.getInstalacion(),List.of(s,s2));
        g.registrarAsistencia(e,sesion,s,EstadoAsistencia.ASISTIO,5,"Buen desempeño");
        comprobar(sesion.getRegistros().get(0).tieneNota(),"Asistencia con evaluación");
        rechaza(()->g.registrarAsistencia(e,sesion,u.buscarSocio("S3"),EstadoAsistencia.ASISTIO,null,null),"Jugador no convocado");
        rechaza(()->g.registrarAsistencia(e,sesion,s,EstadoAsistencia.ASISTIO,6,null),"Nota fuera de rango");
        rechaza(()->g.inscribirEnEquipo(s,g.getEquipos().get(0),"Defensa",20),"Un único equipo por deporte");
        Producto balon=com.getProductos().get(0);
        Venta v=com.iniciarVenta(com.getTienda(),s);
        com.agregarProducto(v,balon,1);
        com.aplicarCodigo(v,e.getCodigoDescuento());
        com.finalizarVenta(v,0,0);
        comprobar(v.getTotalPagado()==109480&&v.getDescuento()==8000,"Descuento compartido e IVA");
        comprobar(com.getInventario().getCantidad(balon,com.getTienda().getUbicacion())==9,"Descuento de inventario");
        comprobar(s.getCuenta().getPuntos()==92,"Fidelización sobre base sin impuesto");
        rechaza(()->com.finalizarVenta(v,0,0),"Venta no se cobra dos veces");
        Venta empleado=com.iniciarVenta(com.getTienda(),e);
        com.agregarProducto(empleado,balon,1);
        com.finalizarVenta(empleado,0,0);
        comprobar(empleado.getTotalPagado()==101150,"Descuento empleado del 15 por ciento");
        Venta sinStock=com.iniciarVenta(com.getTienda(),s);
        rechaza(()->com.agregarProducto(sinStock,balon,100),"Stock insuficiente");
        Snack snack=(Snack)com.getProductos().get(1);
        Venta cafe=com.iniciarVenta(com.getCafeteria(),s);
        com.agregarProducto(cafe,snack,1);
        rechaza(()->com.finalizarVenta(cafe,0,0),"Alérgenos requieren informar");
        comprobar(com.getInventario().getCantidad(snack,com.getCafeteria().getUbicacion())==20,"Rechazo no modifica inventario");
        cafe.marcarAlergenosInformados();
        com.finalizarVenta(cafe,500,10);
        comprobar(cafe.getTotalPagado()==5800,"Impuesto, propina y redención");
        Ubicacion bodega=new Ubicacion("B","Bodega",TipoUbicacion.BODEGA);
        com.getInventario().trasladar(balon,com.getTienda().getUbicacion(),bodega,2,a);
        comprobar(com.getInventario().getCantidad(balon,bodega)==2,"Traslado de existencias");
        rechaza(()->com.getInventario().trasladar(balon,bodega,com.getTienda().getUbicacion(),3,a),"Traslado sin stock");
        SugerenciaProducto sugerencia=com.sugerirProducto(e,new ArticuloDeportivo("RAQ","Raqueta",200000,"Demo","Única"));
        com.responderSugerencia(sugerencia,a,true);
        comprobar(sugerencia.getEstado()==EstadoSolicitud.APROBADA,"Aprobación de sugerencia");
        GestorCompetencias gc=c.getGestorCompetencias();
        Competencia competencia=new Competencia("CERROS","Cerros","Organizador",futbol,Categoria.MAYORES,FormatoCompetencia.LIGA,f,f.plusDays(30),LocalDate.now());
        gc.registrarCompetencia(competencia);
        InscripcionCompetencia ic=gc.inscribirEquipo(competencia,g.getEquipos().get(0),g.getEquipos().get(0).getJugadoresActivos());
        PartidoOficial oficial=gc.programarPartido(competencia,ic,"Universidad rival",f,LocalTime.of(14,0),true);
        gc.registrarResultado(oficial,2,1);
        g.registrarEstadistica(e,oficial,s,Map.of("goles",2,"amarillas",2));
        comprobar(ic.getPuntosAcumulados()==3,"Tabla de posiciones");
        PartidoOficial segundo=gc.programarPartido(competencia,ic,"Otro rival",f.plusDays(1),h,false);
        comprobar(!segundo.participo(s),"Suspensión por dos amarillas");
        gc.registrarResultado(segundo,0,1);
        PartidoOficial tercero=gc.programarPartido(competencia,ic,"Tercer rival",f.plusDays(2),h,false);
        comprobar(tercero.participo(s),"Suspensión cumplida en siguiente partido");
        GestorPersonal personal=c.getGestorPersonal();
        comprobar(personal.coberturaMinimaCumplida(personal.getTurnos(),f),"Cobertura mínima");
        Turno actual=personal.getTurnos().get(0);
        SolicitudCambioTurno cambio=personal.solicitarCambio(e,actual,new Turno("CAMBIO",f,LocalTime.of(7,0),LocalTime.of(22,0),e),"Cambio de horario");
        rechaza(()->personal.aprobarCambio(cambio,a),"Cambio deja hueco de cobertura");
        comprobar(cambio.getEstado()==EstadoSolicitud.PENDIENTE&&personal.getTurnos().contains(actual),"Rechazo conserva turno y solicitud");
        personal.rechazarCambio(cambio,a,"Sin cobertura");
        com.registrarMensualidad(s,YearMonth.now(),ConceptoMensualidad.AFILIACION,50000);
        rechaza(()->com.registrarMensualidad(s,YearMonth.now(),ConceptoMensualidad.AFILIACION,50000),"Mensualidad duplicada");
        ReporteVentas reporte=c.getGestorReportes().generarReporteVentas(LocalDate.now(),LocalDate.now());
        comprobar(reporte.getTransacciones()==4&&reporte.getTotal()==266430,"Reporte por rubro y periodo");
        Deporte padel=g.getDeportes().get(4);
        Reserva rp=ins.reservar(s,padel,f,h,60,2,ModalidadReserva.INDIVIDUAL);
        ins.ingresar(s,rp.getInstalacion(),f,h);
        Bebida bebida=new Bebida("CAFE","Café",3000,true);
        com.registrarProducto(bebida);
        com.getInventario().reabastecer(bebida,com.getCafeteria().getUbicacion(),4,a);
        Venta caliente=com.iniciarVenta(com.getCafeteria(),s);
        com.agregarProducto(caliente,bebida,1);
        rechaza(()->com.finalizarVenta(caliente,0,0),"No se vende bebida caliente dentro de pádel");
        ins.salir(s);
        com.finalizarVenta(caliente,0,0);
        rechaza(()->ins.ingresar(s,rp.getInstalacion(),f,h),"No se entra con bebida caliente");
        s.terminarBebida();
        ins.ingresar(s,rp.getInstalacion(),f,h);
        comprobar(true,"Ingreso después de terminar bebida");
        try {
            java.nio.file.Path temp=java.nio.file.Files.createTempDirectory("cds-prueba-");
            persistencia.IPersistenciaClub p=new persistencia.PersistenciaJson();
            String ruta=temp.resolve("club.json").toString();
            p.salvar(c,ruta);
            ClubDeportivo copia=p.cargar(ruta);
            Socio recuperado=copia.getGestorUsuarios().buscarSocio("S1");
            comprobar(copia.iniciarSesion("socio1","demo123")==recuperado,"Persistencia de credenciales");
            comprobar(copia.getGestorDeportivo().getEquipos().get(0).esIntegrante(recuperado),"Persistencia conserva relaciones");
            comprobar(copia.getGestorComercio().getVentas(LocalDate.now(),LocalDate.now()).size()==4,"Persistencia conserva ventas");
            java.nio.file.Files.writeString(temp.resolve("mal.json"),"{}");
            rechaza(()->p.cargar(temp.resolve("mal.json").toString()),"Archivo inválido rechazado");
            java.nio.file.Files.delete(temp.resolve("mal.json"));
            java.nio.file.Files.delete(temp.resolve("club.json"));
            java.nio.file.Files.delete(temp);
        }
        catch(IOException ex) {
            throw new AssertionError(ex);
        }
        Equipo rivalInterno=new Equipo("Séneca B",Categoria.MAYORES,15,futbol,e);
        g.crearEquipo(rivalInterno);
        rechaza(()->g.programarPartidoInterno(g.getEquipos().get(0),rivalInterno,f,LocalTime.of(18,0)),"Partido interno exige mínimo por equipo");
        for(int k=1;k<=7;k++) {
            Socio nuevo=new Socio("N"+k,"Nuevo "+k,LocalDate.of(2001,1,1),"nuevo"+k,"demo123","NS"+k);
            u.registrarSocio(nuevo);
            g.inscribirEnEquipo(nuevo,rivalInterno,"Defensa",k);
        }
        PartidoInterno interno=g.programarPartidoInterno(g.getEquipos().get(0),rivalInterno,f,LocalTime.of(18,0));
        g.registrarResultadoInterno(interno,1,0);
        comprobar(interno.getResultado()==Resultado.VICTORIA,"Partido interno con ambos equipos habilitados");
        SolicitudCambioTurno igual=personal.solicitarCambio(e,actual,new Turno("IGUAL",f,LocalTime.of(6,0),LocalTime.of(22,0),e),"Misma cobertura");
        personal.aprobarCambio(igual,a);
        comprobar(igual.getEstado()==EstadoSolicitud.APROBADA,"Cambio con cobertura se aprueba");
        Competencia copa=new Competencia("ASCUN","ASCUN","Organizador",futbol,Categoria.MAYORES,FormatoCompetencia.GRUPOS_Y_ELIMINACION,f,f.plusDays(30),LocalDate.now());
        gc.registrarCompetencia(copa);
        InscripcionCompetencia copaIns=gc.inscribirEquipo(copa,g.getEquipos().get(0),g.getEquipos().get(0).getJugadoresActivos());
        PartidoOficial eliminatoria=gc.programarPartido(copa,copaIns,"Rival copa",f.plusDays(4),h,false);
        eliminatoria.setFase("ELIMINACION_DIRECTA");
        rechaza(()->gc.registrarResultado(eliminatoria,1,1),"Eliminación no admite empate");
        gc.registrarResultado(eliminatoria,0,1);
        comprobar(copaIns.isEliminada(),"Derrota elimina al equipo");
        rechaza(()->gc.programarPartido(copa,copaIns,"Otro rival",f.plusDays(5),h,false),"Equipo eliminado no vuelve a jugar");
        System.out.println("RESULTADO: "+exitos+" verificaciones correctas.");
    }
}
