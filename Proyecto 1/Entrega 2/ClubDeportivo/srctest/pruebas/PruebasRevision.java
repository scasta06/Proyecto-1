package pruebas;

import excepciones.ClubException;
import excepciones.InscripcionException;
import excepciones.InventarioException;
import excepciones.ParticipacionException;
import excepciones.ReservaException;
import excepciones.TurnoException;
import excepciones.VentaException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logica.ClubDeportivo;
import logica.GestorComercio;
import logica.GestorCompetencias;
import logica.GestorDeportivo;
import logica.GestorInstalaciones;
import logica.GestorPersonal;
import modelo.actividades.Instalacion;
import modelo.actividades.PartidoInterno;
import modelo.actividades.PartidoOficial;
import modelo.actividades.PartidoPractica;
import modelo.actividades.SesionEntrenamiento;
import modelo.comercio.Alergeno;
import modelo.comercio.ArticuloDeportivo;
import modelo.comercio.Bebida;
import modelo.comercio.Ingrediente;
import modelo.comercio.Producto;
import modelo.comercio.Snack;
import modelo.comercio.SugerenciaProducto;
import modelo.comercio.Venta;
import modelo.competencias.Competencia;
import modelo.competencias.InscripcionCompetencia;
import modelo.deportes.Deporte;
import modelo.deportes.Equipo;
import modelo.inventario.Ubicacion;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Turno;
import modelo.personas.Administrador;
import modelo.personas.Entrenador;
import modelo.personas.Fisioterapeuta;
import modelo.personas.Socio;
import modelo.tipos.Categoria;
import modelo.tipos.ConceptoMensualidad;
import modelo.tipos.EstadoAsistencia;
import modelo.tipos.EstadoPartido;
import modelo.tipos.EstadoSolicitud;
import modelo.tipos.FormatoCompetencia;
import modelo.tipos.ModalidadReserva;
import modelo.tipos.TipoSancion;
import modelo.tipos.TipoUbicacion;
import persistencia.PersistenciaJson;

/** Pruebas de regresion por rol, reglas del enunciado y escenarios SD01 a SD11.
 * Ejecutar desde PruebasSistema o directamente como Java Application en Eclipse.
 */
public final class PruebasRevision
{
    private static int correctas;
    private interface Accion { void ejecutar() throws ClubException; }
    private static void verificar(boolean condicion, String caso)
    {
        if (!condicion) throw new AssertionError(caso);
        System.out.println("REV " + (++correctas) + " OK - " + caso);
    }
    private static void rechazar(Class<? extends ClubException> tipo, Accion accion, String caso)
    {
        try { accion.ejecutar(); }
        catch (ClubException error)
        {
            if (!tipo.isInstance(error)) throw new AssertionError(caso + ": tipo inesperado",error);
            verificar(true,caso);
            return;
        }
        throw new AssertionError("Debió rechazar: " + caso);
    }
    private static Socio socio(ClubDeportivo c, int numero) throws ClubException { return c.getGestorUsuarios().buscarSocio("S"+numero); }
    private static Administrador admin(ClubDeportivo c) throws ClubException { return (Administrador)c.iniciarSesion("admin","demo123"); }
    private static Map<String,Integer> valores(String clave, int valor)
    {
        Map<String,Integer> mapa = new HashMap<>();
        mapa.put(clave,valor);
        return mapa;
    }
    private static Equipo equipo(ClubDeportivo club, String nombre, int desde) throws ClubException
    {
        GestorDeportivo g = club.getGestorDeportivo();
        Equipo equipo = new Equipo(nombre,Categoria.MAYORES,22,g.getDeportes().get(0),club.getGestorUsuarios().getEntrenadores().get(0));
        g.crearEquipo(equipo);
        for (int n=desde; n<desde+8; n++)
        {
            Socio socio = new Socio("S"+n,"Jugador "+n,LocalDate.of(2000,1,1),"socio"+n,"demo123","SOC-"+n);
            club.getGestorUsuarios().registrarSocio(socio);
            g.inscribirEnEquipo(socio,equipo,"Defensa",n%100);
        }
        return equipo;
    }
    public static void main(String[] args) throws ClubException
    {
        correctas=0;
        reservas(); deportivo(); comercio(); inventario(); personal(); competencias(); otrosDeportes(); persistencia();
        System.out.println("RESULTADO REVISION: " + correctas + " verificaciones correctas.");
    }
    private static void reservas() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();
        GestorInstalaciones g=c.getGestorInstalaciones();
        Deporte tenis=c.getGestorDeportivo().getDeportes().get(3),padel=c.getGestorDeportivo().getDeportes().get(4);
        Socio s=socio(c,1);
        LocalDate f=LocalDate.now().plusDays(1);
        Instalacion i=g.getInstalaciones().get(3);

        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.reservar(s,padel,f,LocalTime.NOON,60,2,ModalidadReserva.INDIVIDUAL);
            }
        },"SD01: pádel no admite sencillos");
        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.reservar(s,tenis,f,LocalTime.NOON,60,3,ModalidadReserva.GRUPAL);
            }
        },"SD01: tenis no admite tres jugadores");
        i.setEnMantenimiento(true);
        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.reservar(s,tenis,f,LocalTime.NOON,60,2,ModalidadReserva.INDIVIDUAL);
            }
        },"SD01: mantenimiento bloquea reserva");
        i.setEnMantenimiento(false);
        g.reservar(s,tenis,f,LocalTime.NOON,60,2,ModalidadReserva.INDIVIDUAL);
        Instalacion segunda=new Instalacion("T2","Segunda cancha",4,LocalTime.of(6,0),LocalTime.of(22,0),tenis);
        g.registrarInstalacion(segunda);
        verificar(g.reservar(socio(c,2),tenis,f,LocalTime.NOON,60,2,ModalidadReserva.INDIVIDUAL).getInstalacion()==segunda,"SD01: usa otra instalación si la primera está ocupada");
        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.reservar(socio(c,3),tenis,f,LocalTime.NOON,60,2,ModalidadReserva.INDIVIDUAL);
            }
        },"SD01: rechaza al agotar todas las instalaciones");
        c.getGestorUsuarios().sancionar(s,"Suspensión",TipoSancion.SUSPENSION_TOTAL,f,f,null);
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.reservar(s,padel,f,LocalTime.of(14,0),60,4,ModalidadReserva.DOBLES);
            }
        },"SD01: suspensión total produce ParticipacionException");
        verificar(g.getReservas().size()==2,"SD01: rechazos no crean reservas");
    }
    private static void deportivo() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();
        GestorDeportivo g=c.getGestorDeportivo();
        Socio s=socio(c,1), rival=socio(c,2), principiante=socio(c,16);
        Entrenador en=c.getGestorUsuarios().getEntrenadores().get(0),otro=c.getGestorUsuarios().getEntrenadores().get(1);
        Deporte tenis=g.getDeportes().get(3),padel=g.getDeportes().get(4),futbol=g.getDeportes().get(0);
        LocalDate f=LocalDate.now().plusDays(1);
        Instalacion i=c.getGestorInstalaciones().getInstalaciones().get(3);

        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarPartidoPractica(s,principiante,tenis,f,LocalTime.of(8,0));
            }
        },"SD02: intermedio no juega contra principiante");
        PartidoPractica conEntrenador=g.programarPartidoPractica(principiante,en,tenis,f,LocalTime.of(8,0));
        verificar(conEntrenador.incluyeEntrenador(),"SD02: entrenador exento de compatibilidad de nivel");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarPartidoPractica(s,admin(c),tenis,f,LocalTime.of(10,0));
            }
        },"SD02: administrador no juega");
        PartidoPractica dobles=g.programarDobles(Arrays.asList(s,rival),Arrays.asList(socio(c,3),socio(c,4)),padel,f,LocalTime.of(10,0));
        verificar(dobles.getParticipantes().size()==4,"Pádel: partido de dobles con cuatro socios");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarDobles(Arrays.asList(s,s),Arrays.asList(socio(c,3),socio(c,4)),padel,f,LocalTime.of(12,0));
            }
        },"Dobles rechaza jugadores repetidos");
        SesionEntrenamiento sesion=g.programarEntrenamiento(en,f,LocalTime.of(12,0),60,i,Arrays.asList(s,rival));
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarAsistencia(otro,sesion,s,EstadoAsistencia.ASISTIO,3,"Bien");
            }
        },"SD04: solo entrenador responsable");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarAsistencia(en,sesion,principiante,EstadoAsistencia.ASISTIO,3,"Bien");
            }
        },"SD04: rechaza no convocado");
        g.registrarAsistencia(en,sesion,s,EstadoAsistencia.FALTO,null,null);
        g.registrarAsistencia(en,sesion,s,EstadoAsistencia.LLEGO_TARDE,4,"Corrección");
        verificar(sesion.getRegistros().size()==1 && sesion.getRegistros().get(0).getEstado()==EstadoAsistencia.LLEGO_TARDE,"SD04: reemplaza asistencia sin duplicar");
        verificar(c.getGestorReportes().consultarAsistencia(s,f,f).size()==1,"Administrador: reporte de asistencia por periodo");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarEntrenamiento(en,f,LocalTime.of(12,30),60,i,Arrays.asList(s));
            }
        },"SD03: rechaza sesión superpuesta");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarEntrenamiento(en,f,LocalTime.of(14,0),60,i,new ArrayList<Socio>());
            }
        },"SD03: rechaza convocatoria vacía");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarEntrenamiento(en,f,LocalTime.of(14,0),60,i,Arrays.asList(s,s));
            }
        },"SD03: rechaza convocatoria repetida");
        c.getGestorUsuarios().sancionar(s,"Prueba",TipoSancion.SUSPENSION_DEPORTIVA,LocalDate.now(),f,futbol);
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.inscribirEnEquipo(s,g.getEquipos().get(0),"Defensa",30);
            }
        },"SD07: valida sanción antes de inscribir");
        Socio libre=socio(c,15);
        rechazar(InscripcionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.inscribirEnEquipo(libre,g.getEquipos().get(0),"Defensa",1);
            }
        },"SD07: camiseta ocupada");
        g.inscribirEnEquipo(libre,g.getEquipos().get(0),"Defensa",30);
        verificar(g.getEquipos().get(0).esIntegrante(libre),"SD07: inscripción válida mantiene relación");
        PartidoPractica p=g.programarPartidoPractica(s,rival,tenis,f.plusDays(1),LocalTime.of(8,0));
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarEstadistica(en,p,s,valores("aces",1));
            }
        },"SD08: no registra estadísticas antes del resultado");
        p.registrarResultado(2,0);
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarEstadistica(en,p,s,valores("gano",0));
            }
        },"SD08: ganador debe coincidir con marcador");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarEstadistica(en,p,s,valores("aces",-1));
            }
        },"SD08: rechaza valores negativos");
        Map<String,Integer> datos=valores("aces",4);
        datos.put("setsGanados",2);datos.put("gamesGanados",12);datos.put("erroresNoForzados",3);
        g.registrarEstadistica(en,p,s,datos);
        g.registrarEstadistica(en,p,rival,valores("setsGanados",0));
        verificar(g.consultarRanking(tenis).get(0).getPuntosRanking()==1016 && g.consultarRanking(tenis).get(1).getPuntosRanking()==984,"Ranking ELO usa la misma puntuación previa para ambos jugadores");
        verificar(g.consultarRanking(tenis).get(0).getAcumulados().get("gamesGanados")==12,"Estadísticas de raqueta conservan games y sets");
        verificar(g.consultarRanking(tenis).get(0).getHistorialPuntos().size()==1,"Administrador: historial de evolución de ranking");
        verificar(c.getGestorReportes().consultarResultados(f,f.plusDays(2)).size()==1,"Administrador: resultados de partidos por periodo");
        Equipo local=g.getEquipos().get(0),visitante=equipo(c,"Segundo equipo",20);
        PartidoInterno interno=g.programarPartidoInterno(local,visitante,f.plusDays(3),LocalTime.NOON);
        interno.registrarResultado(2,1);
        g.registrarEstadistica(en,interno,s,valores("rojas",1));
        verificar(local.suspendidoPorTarjetas(s,"INTERNOS"),"Fútbol: roja suspende el siguiente partido interno");
        PartidoInterno siguiente=g.programarPartidoInterno(local,visitante,f.plusDays(4),LocalTime.NOON);
        verificar(!siguiente.participo(s),"Fútbol: suspendido no entra a la alineación");
        siguiente.cancelar();
        verificar(local.suspendidoPorTarjetas(s,"INTERNOS"),"Cancelar partido no cumple sanción");
        PartidoInterno cumplido=g.programarPartidoInterno(local,visitante,f.plusDays(5),LocalTime.NOON);
        cumplido.registrarResultado(0,0);
        verificar(!local.suspendidoPorTarjetas(s,"INTERNOS"),"Sanción se cumple al disputarse el siguiente partido");
        g.darDeBajaEquipo(visitante);
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarPartidoInterno(local,visitante,f.plusDays(6),LocalTime.NOON);
            }
        },"Equipo dado de baja no disputa partidos");
        c.getGestorUsuarios().darDeBajaSocio("S15");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                c.iniciarSesion("socio15","demo123");
            }
        },"Socio dado de baja no inicia sesión");
        verificar(!local.esIntegrante(libre),"Baja de socio lo excluye de jugadores activos");
    }
    private static void comercio() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();
        GestorComercio g=c.getGestorComercio();
        Socio s=socio(c,1);
        Entrenador en=c.getGestorUsuarios().getEntrenadores().get(0);
        Producto balon=g.getProductos().get(0);
        Venta normal=g.iniciarVenta(g.getTienda(),s);
        g.agregarProducto(normal,balon,1);
        g.finalizarVenta(normal,0,0);

        verificar(normal.getDescuento()==0 && normal.getTotalPagado()==119000,"Socio sin código paga precio e IVA completos");
        verificar(s.getCuenta().getPuntos()==2380,"Fidelidad: 2 por ciento del valor pagado, un punto vale un peso");
        Venta compartida=g.iniciarVenta(g.getTienda(),s);g.agregarProducto(compartida,balon,1);
        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.aplicarCodigoDescuento(compartida,"INVENTADO");
            }
        },"Código compartido debe pertenecer a empleado");
        g.aplicarCodigoDescuento(compartida,en.getCodigoDescuento());
        g.finalizarVenta(compartida,0,1000);
        verificar(compartida.getDescuento()==8000 && compartida.getTotalPagado()==108480 && compartida.getValorRedimido()==1000,"Código aplica 8 por ciento, redime un peso por punto");
        verificar(s.getCuenta().getPuntos()==3549,"Puntos redimidos se descuentan y nueva compra acumula parte entera del 2 por ciento");
        Venta empleado=g.iniciarVenta(g.getTienda(),en);g.agregarProducto(empleado,balon,1);g.finalizarVenta(empleado,0,0);
        verificar(empleado.getDescuento()==15000 && en.getCuenta().getPuntos()==2023,"Empleado recibe descuento y también fidelidad");
        Venta fallida=g.iniciarVenta(g.getTienda(),s);g.agregarProducto(fallida,balon,1);
        int stock=g.getInventario().getCantidad(balon,g.getTienda().getUbicacion()),saldo=s.getCuenta().getPuntos();
        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.finalizarVenta(fallida,0,999999);
            }
        },"Rechaza redención sin saldo");
        verificar(!fallida.isCerrada() && s.getCuenta().getPuntos()==saldo && g.getInventario().getCantidad(balon,g.getTienda().getUbicacion())==stock,"Compra rechazada conserva saldo e inventario");
        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.finalizarVenta(fallida,10,0);
            }
        },"Tienda no admite propina");
        Snack sinAlergenos=new Snack("FRUTA","Fruta",2000,50,Arrays.asList(new Ingrediente("Manzana",new ArrayList<Alergeno>())));
        g.registrarProducto(sinAlergenos);g.getInventario().reabastecer(sinAlergenos,g.getCafeteria().getUbicacion(),5,admin(c));
        Venta fruta=g.iniciarVenta(g.getCafeteria(),s);g.agregarProducto(fruta,sinAlergenos,1);
        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.finalizarVenta(fruta,0,0);
            }
        },"SD09: informa ingredientes aunque no haya alérgenos");
        fruta.marcarAlergenosInformados();g.finalizarVenta(fruta,200,0);
        verificar(fruta.getTotalPagado()==2360 && fruta.getDescuento()==0,"SD09: cafetería cobra 8 por ciento y propina sin descuento de empleado");
        Bebida caliente=new Bebida("CAFE","Café",3000,true),fria=new Bebida("AGUA","Agua",1000,false);
        g.registrarProducto(caliente);g.registrarProducto(fria);
        g.getInventario().reabastecer(caliente,g.getCafeteria().getUbicacion(),5,admin(c));
        g.getInventario().reabastecer(fria,g.getCafeteria().getUbicacion(),5,admin(c));
        Instalacion padel=c.getGestorInstalaciones().getInstalaciones().get(4),mesa=c.getGestorInstalaciones().getInstalaciones().get(5);
        mesa.setTechada(true);
        c.getGestorInstalaciones().ingresar(s,padel);
        Venta cafe=g.iniciarVenta(g.getCafeteria(),s);g.agregarProducto(cafe,caliente,1);

        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.finalizarVenta(cafe,0,0);
            }
        },"Bebida caliente no se despacha a socio dentro de pádel");
        verificar(g.getInventario().getCantidad(caliente,g.getCafeteria().getUbicacion())==5,"Rechazo de bebida conserva stock");
        c.getGestorInstalaciones().salir(s);g.finalizarVenta(cafe,0,0);
        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                c.getGestorInstalaciones().ingresar(s,padel);
            }
        },"Socio con bebida caliente no entra a pádel");
        rechazar(ReservaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                c.getGestorInstalaciones().ingresar(s,mesa);
            }
        },"Socio con bebida caliente no entra a tenis de mesa techado");
        s.terminarBebida();c.getGestorInstalaciones().ingresar(s,mesa);
        Venta agua=g.iniciarVenta(g.getCafeteria(),s);g.agregarProducto(agua,fria,1);g.finalizarVenta(agua,0,0);
        verificar(agua.isCerrada() && !s.tieneBebidaCaliente(),"Bebida fría sí se permite en la instalación");
        Producto propuesto=new ArticuloDeportivo("RAQ","Raqueta",50000,"Marca","Única");
        SugerenciaProducto sugerencia=g.sugerirProducto(en,propuesto);

        verificar(!g.getProductos().contains(propuesto),"Sugerencia pendiente no se vende");
        g.resolverSugerencia(sugerencia,admin(c),true);
        verificar(g.getProductos().contains(propuesto) && sugerencia.getEstado()==EstadoSolicitud.APROBADA,"Administrador aprueba sugerencia e incorpora producto");
        rechazar(VentaException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.resolverSugerencia(sugerencia,admin(c),true);
            }
        },"Sugerencia no se resuelve dos veces");
        double total=0;for(Venta venta:g.getVentas(LocalDate.now(),LocalDate.now())) total+=venta.getTotalPagado();
        verificar(Math.abs(c.getGestorReportes().generarReporteVentas(LocalDate.now(),LocalDate.now()).getTotal()-total)<0.01,"SD06: reporte concilia con todos los importes cobrados");
        verificar(c.getGestorReportes().generarReporteVentas(LocalDate.now().minusYears(1),LocalDate.now().minusMonths(1)).estaVacio(),"SD06: informa periodo sin ventas");
    }
    private static void inventario() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();GestorComercio g=c.getGestorComercio();
        Producto balon=g.getProductos().get(0),ajeno=new ArticuloDeportivo("X","Ajeno",1000,"Marca","Talla");
        Ubicacion origen=g.getTienda().getUbicacion(),destino=new Ubicacion("B","Bodega",TipoUbicacion.BODEGA);

        rechazar(InventarioException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.getInventario().reabastecer(ajeno,destino,2,admin(c));
            }
        },"SD10: rechaza producto fuera del catálogo");
        rechazar(InventarioException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.getInventario().trasladar(balon,origen,destino,11,admin(c));
            }
        },"SD10: traslado no permite saldo negativo");
        verificar(g.getInventario().getCantidad(balon,origen)==10 && g.getInventario().consultar(destino).isEmpty(),"Traslado rechazado no crea existencias ni descuenta origen");
        g.getInventario().trasladar(balon,origen,destino,4,admin(c));
        verificar(g.getInventario().getCantidad(balon,origen)==6 && g.getInventario().getCantidad(balon,destino)==4,"SD10: traslado conserva total y cambia ubicaciones");
        rechazar(InventarioException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.getInventario().reabastecer(balon,destino,-1,admin(c));
            }
        },"SD10: rechaza reabastecimiento negativo");
        rechazar(InventarioException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.getInventario().reabastecer(balon,destino,Integer.MAX_VALUE,admin(c));
            }
        },"Inventario controla desbordamiento");
    }
    private static void personal() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();GestorPersonal g=c.getGestorPersonal();
        Entrenador en=c.getGestorUsuarios().getEntrenadores().get(0);
        Turno actual=g.getTurnos().get(0);LocalDate f=actual.getFecha();
        Turno tarde=new Turno("TARDE",f,LocalTime.of(6,1),LocalTime.of(22,0),en);
        SolicitudCambioTurno solicitud=g.solicitarCambio(en,actual,tarde,"Un minuto");

        rechazar(TurnoException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.aprobarCambio(solicitud,admin(c));
            }
        },"SD05: detecta hueco de un minuto, no solo franjas de media hora");
        verificar(solicitud.getEstado()==EstadoSolicitud.PENDIENTE && g.getTurnos().contains(actual),"SD05: rechazo conserva solicitud pendiente y turno original");
        SolicitudCambioTurno duplicado=g.solicitarCambio(en,actual,new Turno("E2",f,LocalTime.of(6,0),LocalTime.of(22,0),en),"Id repetido");
        rechazar(TurnoException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.aprobarCambio(duplicado,admin(c));
            }
        },"SD05: no permite identificador duplicado");
        SolicitudCambioTurno otroDia=g.solicitarCambio(en,actual,new Turno("OTRO",f.plusDays(1),LocalTime.of(6,0),LocalTime.of(22,0),en),"Cambio de día");
        rechazar(TurnoException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.aprobarCambio(otroDia,admin(c));
            }
        },"SD05: revisa también la fecha del turno que se elimina");
    }
    private static void competencias() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();GestorCompetencias g=c.getGestorCompetencias();GestorDeportivo deportivo=c.getGestorDeportivo();
        Deporte futbol=deportivo.getDeportes().get(0);Equipo equipo=deportivo.getEquipos().get(0);
        LocalDate f=LocalDate.now().plusDays(1);Socio s=socio(c,1);
        Entrenador en=c.getGestorUsuarios().getEntrenadores().get(0);
        Competencia torneo=new Competencia("CERROS","Cerros","Circuito",futbol,Categoria.MAYORES,FormatoCompetencia.LIGA,f,f.plusDays(20),LocalDate.now());
        g.registrarCompetencia(torneo);g.registrarRival(torneo,"Universidad B","A");g.registrarRival(torneo,"Universidad C","A");g.registrarRival(torneo,"Universidad D","A");

        rechazar(InscripcionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.inscribirEquipo(torneo,equipo,Arrays.asList(s));
            }
        },"SD11: nómina no alcanza mínimo");
        List<Socio> repetidos=new ArrayList<>(equipo.getJugadoresActivos());repetidos.set(1,s);
        rechazar(InscripcionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.inscribirEquipo(torneo,equipo,repetidos);
            }
        },"SD11: nómina no permite duplicados");
        InscripcionCompetencia ins=g.inscribirEquipo(torneo,equipo,equipo.getJugadoresActivos());
        PartidoOficial primero=g.programarPartido(torneo,ins,"Universidad B",f,LocalTime.NOON,false);
        primero.setEscenario("Estadio visitante");
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                primero.agregarParticipante(socio(c,16));
            }
        },"Oficial: jugador ajeno a nómina no participa");
        g.registrarResultado(primero,2,0);deportivo.registrarEstadistica(en,primero,s,valores("amarillas",1));
        verificar(torneo.getClasificacionCompleta().size()==4 && torneo.getClasificacionCompleta().get(0).getPuntos()==3,"Liga: tabla incluye instituciones externas y puntos del club");
        verificar(torneo.getRanking().getEntrada(s).getPartidosJugados()==1 && primero.getEscenario().equals("Estadio visitante"),"Competencia conserva estadísticas propias y escenario externo");
        PartidoOficial segundo=g.programarPartido(torneo,ins,"Universidad C",f.plusDays(1),LocalTime.NOON,false);
        g.registrarResultado(segundo,1,0);deportivo.registrarEstadistica(en,segundo,s,valores("amarillas",1));
        verificar(equipo.suspendidoPorTarjetas(s,torneo.getCodigo()),"Fútbol oficial: acumula dos amarillas entre partidos");
        PartidoOficial tercero=g.programarPartido(torneo,ins,"Universidad D",f.plusDays(2),LocalTime.NOON,false);
        verificar(!tercero.participo(s),"Suspensión oficial excluye jugador del siguiente partido");
        g.registrarResultado(tercero,0,0);
        verificar(!equipo.suspendidoPorTarjetas(s,torneo.getCodigo()),"Suspensión oficial se cumple al disputar partido");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarResultado(tercero,1,0);
            }
        },"Resultado duplicado no suma puntos dos veces");
        verificar(ins.getPuntosAcumulados()==7,"Tabla mantiene puntos tras rechazo de resultado duplicado");
        Equipo juvenil=new Equipo("Juvenil",Categoria.SUB_15,15,futbol,en);
        List<Socio> menores=new ArrayList<>();
        for(int n=0;n<7;n++)
        {
            Socio menor=new Socio("J"+n,"Juvenil "+n,LocalDate.now().minusYears(15),"juvenil"+n,"demo123","J"+n);
            juvenil.inscribir(menor,"Defensa",n,LocalDate.now());menores.add(menor);
        }
        Competencia futura=new Competencia("FUTURA","Futura","Org",futbol,Categoria.SUB_15,FormatoCompetencia.LIGA,LocalDate.now().plusYears(1),LocalDate.now().plusYears(1).plusDays(10),LocalDate.now());
        g.registrarCompetencia(futura);

        rechazar(InscripcionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.inscribirEquipo(futura,juvenil,menores);
            }
        },"SD11: verifica edad al inicio del torneo, no solo al entrar al equipo");
        Competencia ascun=new Competencia("ASCUN","ASCUN","ASCUN",futbol,Categoria.MAYORES,FormatoCompetencia.GRUPOS_Y_ELIMINACION,f,f.plusDays(20),LocalDate.now());
        g.registrarCompetencia(ascun);
        InscripcionCompetencia ia=g.inscribirEquipo(ascun,equipo,equipo.getJugadoresActivos());
        g.registrarRival(ascun,"Rival A","A");g.registrarRival(ascun,"Rival B","B");g.registrarRival(ascun,"Rival C","B");

        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                ascun.iniciarEliminacion(1);
            }
        },"ASCUN: no avanza sin resultados de grupos");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarPartido(ascun,ia,"Rival B",f,LocalTime.of(10,0),false);
            }
        },"ASCUN: no cruza grupos en primera fase");
        PartidoOficial grupo=g.programarPartido(ascun,ia,"Rival A",f,LocalTime.of(10,0),false);
        g.registrarResultado(grupo,2,0);
        g.registrarResultadoExterno(ascun,"B1","Rival B","Rival C",1,0);
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException { g.registrarResultadoExterno(ascun,"B2","Rival B","Rival C",1,0); }
        },"Grupos: cambiar el id no permite repetir el mismo enfrentamiento");
        verificar(ascun.getClasificacionCompleta().get(1).getJugados()==1,"Resultado de grupos rechazado no aumenta partidos jugados");
        ascun.iniciarEliminacion(1);

        verificar(ascun.getFaseActual().equals("ELIMINACION_DIRECTA"),"ASCUN: clasifica por puntos de grupos y cambia de fase");
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.programarPartido(ascun,ia,"Rival A",f.plusDays(3),LocalTime.of(10,0),false);
            }
        },"ASCUN: no alinea equipo eliminado en grupos");
        PartidoOficial finalAscun=g.programarPartido(ascun,ia,"Rival B",f.plusDays(3),LocalTime.of(10,0),false);
        rechazar(ClubException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException
            {
                g.registrarResultado(finalAscun,0,0);
            }
        },"ASCUN: eliminación directa exige ganador");
        g.registrarResultado(finalAscun,0,1);
        verificar(ia.isEliminada(),"ASCUN: derrota elimina al equipo");
    }
    private static void otrosDeportes() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();
        GestorDeportivo d=c.getGestorDeportivo();
        GestorCompetencias g=c.getGestorCompetencias();
        Entrenador en=c.getGestorUsuarios().getEntrenadores().get(0);
        LocalDate fecha=LocalDate.now().plusDays(2);
        for (int numero : new int[] {1,2})
        {
            final Deporte deporte=d.getDeportes().get(numero);
            Equipo equipo=new Equipo("Equipo "+numero,Categoria.MAYORES,20,deporte,en);
            d.crearEquipo(equipo);
            for (int n=1;n<=7;n++) d.inscribirEnEquipo(socio(c,n),equipo,numero==1?"Base":"Armador",n);
            Competencia torneo=new Competencia("DEP"+numero,"Torneo","Circuito",deporte,Categoria.MAYORES,FormatoCompetencia.LIGA,fecha,fecha.plusDays(5),LocalDate.now());
            g.registrarCompetencia(torneo);g.registrarRival(torneo,"Rival","A");
            InscripcionCompetencia ins=g.inscribirEquipo(torneo,equipo,equipo.getJugadoresActivos());
            final PartidoOficial partido=g.programarPartido(torneo,ins,"Rival",fecha,LocalTime.NOON,true);
            rechazar(ClubException.class,new Accion()
            {
                @Override
                public void ejecutar() throws ClubException { g.registrarResultado(partido,1,1); }
            },"Sin empates en "+deporte.getNombre());
            if (numero==2)
            {
                rechazar(ClubException.class,new Accion()
                {
                    @Override
                    public void ejecutar() throws ClubException { g.registrarResultado(partido,25,20); }
                },"Voleibol rechaza puntos individuales como marcador de sets");
            }
            g.registrarResultado(partido,numero==1?80:3,numero==1?70:2);
            Map<String,Integer> estadistica=new HashMap<>();
            estadistica.put(numero==1?"puntos":"aces",numero==1?18:4);
            estadistica.put(numero==1?"rebotes":"bloqueos",numero==1?7:2);
            d.registrarEstadistica(en,partido,socio(c,1),estadistica);
            verificar(ins.getPuntosAcumulados()==2,"Puntos de clasificación en "+deporte.getNombre());
            verificar(d.consultarRanking(deporte).get(0).getAcumulados().get(numero==1?"puntos":"aces")== (numero==1?18:4),"Estadísticas propias de "+deporte.getNombre());
            if (numero==2) verificar(torneo.getClasificacionCompleta().get(1).getPuntos()==1,"Voleibol: derrota 2-3 otorga un punto");
        }
        // La sancion puede aparecer despues de haber programado el siguiente encuentro.
        Equipo equipo=d.getEquipos().get(0);Deporte futbol=d.getDeportes().get(0);
        Competencia torneo=new Competencia("TARDIA","Tarjetas","Circuito",futbol,Categoria.MAYORES,FormatoCompetencia.LIGA,fecha,fecha.plusDays(5),LocalDate.now());
        g.registrarCompetencia(torneo);g.registrarRival(torneo,"Uno","A");g.registrarRival(torneo,"Dos","A");
        InscripcionCompetencia ins=g.inscribirEquipo(torneo,equipo,equipo.getJugadoresActivos());
        PartidoOficial primero=g.programarPartido(torneo,ins,"Uno",fecha,LocalTime.of(14,0),false);
        final PartidoOficial segundo=g.programarPartido(torneo,ins,"Dos",fecha.plusDays(1),LocalTime.of(14,0),false);
        g.registrarResultado(primero,1,0);d.registrarEstadistica(en,primero,socio(c,1),valores("rojas",1));
        rechazar(ParticipacionException.class,new Accion()
        {
            @Override
            public void ejecutar() throws ClubException { g.registrarResultado(segundo,2,0); }
        },"Revalida sanción aunque el partido ya estuviera programado");
        verificar(segundo.getEstado()==EstadoPartido.PROGRAMADO && ins.getPuntosAcumulados()==3,"Rechazo de alineación no modifica resultado ni tabla");
        segundo.quitarParticipante(socio(c,1));g.registrarResultado(segundo,2,0);
        verificar(ins.getPuntosAcumulados()==6,"Se puede corregir alineación y disputar el encuentro");
        System.out.println("ESTADO DEPORTIVO: puntos del equipo="+ins.getPuntosAcumulados()+", partidos="+ins.getPartidosJugados());
    }
    private static void persistencia() throws ClubException
    {
        ClubDeportivo c=DatosPrueba.crear();GestorComercio g=c.getGestorComercio();
        Socio s=socio(c,1);s.recibirBebidaCaliente();
        g.sugerirProducto(c.getGestorUsuarios().getEntrenadores().get(0),new Bebida("B","Bebida propuesta",2000,false));
        c.getGestorDeportivo().getEquipos().get(0).registrarTarjetas(s,"INTERNOS",2,0,2);
        g.registrarMensualidad(s,YearMonth.now().minusMonths(1),ConceptoMensualidad.ACADEMIA,150000);
        try
        {
            File archivo=File.createTempFile("revision-club-",".json");
            PersistenciaJson persistencia=new PersistenciaJson();persistencia.salvar(c,archivo.getPath());
            ClubDeportivo copia=persistencia.cargar(archivo.getPath());
            Socio recuperado=socio(copia,1);

        verificar(recuperado.tieneBebidaCaliente(),"Persistencia conserva posesión de bebida");
        verificar(copia.getGestorDeportivo().getEquipos().get(0).suspendidoPorTarjetas(recuperado,"INTERNOS"),"Persistencia conserva sanciones pendientes y referencias de socios");
        verificar(copia.getGestorComercio().getSugerencias().size()==1,"Persistencia conserva sugerencias pendientes");
        verificar(copia.getGestorReportes().generarReporteVentas(LocalDate.now(),LocalDate.now()).getTotal()==150000,"Reportes filtran mensualidad por fecha de pago");
            // La carga no debe romper los nuevos servicios de reportes ni los roles.
            verificar(copia.iniciarSesion("entrenador","demo123") instanceof Entrenador,"Persistencia y acceso del empleado");
            verificar(copia.iniciarSesion("fisio","demo123") instanceof Fisioterapeuta,"Persistencia y acceso del fisioterapeuta");
            verificar(copia.iniciarSesion("admin","demo123") instanceof Administrador,"Persistencia y acceso del administrador");
            System.out.println("ESTADO RECUPERADO: sugerencias="+copia.getGestorComercio().getSugerencias().size()+", socios="+copia.getGestorUsuarios().getPersonas().size()+", suspensión pendiente="+copia.getGestorDeportivo().getEquipos().get(0).suspendidoPorTarjetas(recuperado,"INTERNOS"));
            if (!archivo.delete()) throw new IOException("No se pudo limpiar el archivo temporal");
        }
        catch(IOException error) { throw new AssertionError(error); }
    }
}
