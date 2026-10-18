# Diseño e implementación · Club Deportivo Séneca

## Modelo vigente y alcance

La base es **Modelo_clases_final.pdf**, compartido el 18 de octubre de 2026, con
paquetes `modelo.personas`, `modelo.deportes`, `modelo.actividades`,
`modelo.competencias`, `modelo.personal`, `modelo.inventario`, `modelo.comercio`,
`logica`, `persistencia`, `consola` y `excepciones`. Las enumeraciones se agruparon
en `modelo.tipos`. No se sustituyeron los PDF históricos de la Entrega 1.

La implementación incluye los tipos y operaciones del diseño final. Se añadieron
constructores, consultas y operaciones auxiliares para mantener encapsulamiento y
permitir crear los escenarios de demostración. `Validacion` concentra comprobaciones
compartidas; `DatosPrueba` y `PruebasSistema` constituyen los programas de prueba.

Se añadieron tres clases que el caso necesita y el PDF final no enumera:

| Extensión | Justificación |
|---|---|
| `Bebida extends Producto` | Temperatura y restricción de consumo en pádel/tenis de mesa techado |
| `SugerenciaProducto` | Empleados proponen y administrador aprueba o rechaza productos |
| `PartidoInterno extends Partido` | Dos equipos del club, misma categoría y disciplina, con mínimo de jugadores |

Estas extensiones están incluidas tanto en las fuentes UML como en el código.
Los archivos `clases-diseno.puml` y `clases-alto-nivel.puml` reflejan las clases
entregadas; los cinco diagramas de secuencia representan las llamadas implementadas.

## Responsabilidades y dependencias

| Componente | Responsabilidad |
|---|---|
| `ClubDeportivo` | Componer gestores y ofrecer el inicio de sesión |
| `GestorUsuarios` | Identidades únicas, autenticación, consulta y sanciones por fecha |
| `GestorInstalaciones` | Disponibilidad, reservas, cancelaciones y permanencia en canchas |
| `GestorDeportivo` | Inscripciones, prácticas, partidos internos, entrenamientos, estadísticas y rankings |
| `GestorCompetencias` | Nóminas oficiales, partidos, resultados y clasificación |
| `GestorComercio` | Productos, ventas, códigos, mensualidades y sugerencias |
| `GestorPersonal` | Turnos, solicitudes y validación de cobertura |
| `GestorReportes` | Consolidar ingresos por rubro, mes y periodo |
| `IPersistenciaClub` | Contrato de carga y almacenamiento |
| `PersistenciaJson` | Persistir un grafo de objetos completo conservando sus referencias |
| Consolas | Autenticar y mostrar resúmenes por rol; pruebas demuestran las operaciones completas |

Las entidades usan atributos privados, métodos de dominio y listas/mapas de sólo
lectura para las consultas. `Evento`, `Partido`, `Persona`, `Empleado`, `Producto`,
`Estadistica`, `PuntoVenta` y `MovimientoInventario` son abstractas. La herencia
permite cambiar el comportamiento deportivo o comercial sin condicionales en las
consolas. Los métodos de gestores validan reglas que involucran varias entidades.

## Reglas de dominio

### Usuarios, equipos y academias

Documento y login son únicos. Todos los roles poseen login y contraseña; el ejemplo
usa contraseñas ficticias. Se almacena un resumen SHA-256, no la contraseña literal.
La implementación es académica: no incorpora sesiones web ni gestión de claves
de un servicio real.

Las categorías respetan literalmente el PDF final: Sub-13 abarca 0–13, Sub-15
14–15, Sub-17 16–17 y Mayores 18–120. Se valida edad al inscribir, cupo, posición,
camiseta única por equipo y pertenencia a un solo equipo por deporte desde el gestor.
Las academias requieren un nivel de 1 a 10 en el rango permitido y no admiten
inscripciones activas repetidas.

Las bajas se implementan con `Socio.darDeBaja()` y `Equipo.darDeBaja()`, conservando
historial; las consultas de jugadores activos excluyen a quienes se dieron de baja.
`InscripcionEquipo.retirar()` e `InscripcionAcademia.retirar()` conservan el registro.

### Instalaciones y eventos

Los intervalos usan **[inicio, fin)**: un evento que termina a las 09:00 permite otro
que empieza exactamente a las 09:00. Se comprueban mantenimiento, capacidad,
horario, disciplina y superposición. No se permiten eventos que crucen medianoche.
Una reserva cancelada libera la instalación y permanece en el historial.

El horario inicial de datos es 06:00–22:00. Una reserva individual requiere dos
participantes y una de dobles cuatro; los demás límites se configuran en `Deporte`.
La consulta de disponibilidad muestra posibles inicios cada 30 minutos.

`ingresar` registra al titular de una reserva vigente dentro de la instalación y
`salir` retira esa permanencia. El socio con bebida caliente no entra a pádel ni a
una mesa de tenis de mesa techada. La cafetería consulta la permanencia antes de
vender una bebida caliente. `terminarBebida()` libera la restricción de consumo.

### Entrenamientos, partidos y estadísticas

El entrenador debe tener especialidad en la disciplina. La convocatoria debe ser
única, caber en la instalación y contener socios activos habilitados. Se comprueba
que el entrenador no tenga otra sesión superpuesta. Sólo el responsable registra
asistencia; la nota es opcional y, si existe, está entre 1 y 5.

Las prácticas individuales admiten niveles cuya diferencia no exceda el parámetro
del deporte. Un entrenador de esa disciplina puede ser rival sin esa comparación.
Los datos iniciales usan diferencia máxima **1**. El caso original describe niveles
por nombres mientras el modelo final usa 1–10: prevalece el modelo final recibido.

Los partidos internos validan ambos equipos y el mínimo habilitado por cada uno:
7 en fútbol, 5 en baloncesto y 6 en voleibol en los datos iniciales. Las estadísticas
requieren un partido finalizado, un jugador participante y un entrenador de la
disciplina. No se permite registrar dos estadísticas para el mismo jugador/partido.
Los valores no pueden ser negativos.

Fútbol acumula goles y asistencias; baloncesto puntos y rebotes; voleibol aces y
bloqueos. Los mapas permiten conservar los valores complementarios de cada deporte
(por ejemplo, sets, games, aces y errores no forzados). El entrenador registra los
valores de todos los participantes mediante una llamada por jugador.

### Competencias y tarjetas

Las competencias tienen fechas, cierre de inscripción, categoría, disciplina,
organizador y formato. Se valida que la nómina pertenezca al equipo, no tenga
duplicados y cumpla el tamaño configurado. Sólo los jugadores habilitados de la
nómina participan en los partidos oficiales. Un partido local ocupa una instalación;
uno visitante mantiene instalación nula.

Victoria/empate/derrota otorgan 3/1/0 puntos según la configuración inicial. La tabla
ordena por puntos; empates en puntos se ordenan por nombre para hacer el resultado
reproducible. Liga, eliminación directa y grupos más eliminación se representan
con `FormatoCompetencia`; `PartidoOficial.setFase()` permite indicar la fase de un
partido. Las eliminatorias rechazan empates y eliminan al perdedor del club.

El fixture y la selección de los cruces de grupos **los programa el gestor con los
datos suministrados**; no se implementa un generador automático de calendarios ni
una regla de clasificación entre grupos que el modelo no define. El rival externo
es un texto, tal como indica el PDF; la tabla almacena las inscripciones del club,
sin inventar nóminas completas de instituciones externas.

Dos amarillas acumuladas o una roja impiden participar en el siguiente partido
del ámbito correspondiente. Las tarjetas oficiales se separan por competencia y
las internas por equipo. Al finalizar el siguiente partido se cumple la suspensión
de quienes estaban excluidos por ella. Las sanciones administrativas por rango de
fechas son independientes y no se consumen como tarjetas.

### Rankings

Se conserva `Ranking` y `EntradaRanking` del diseño. Cada estadística se procesa
una vez. El puntaje inicial es 1000. Se definieron estas fórmulas simples, ya que el
PDF no fija sus coeficientes:

| Disciplina | Cambio de puntaje |
|---|---|
| Fútbol | 3 × goles + asistencias |
| Baloncesto | puntos + rebotes |
| Voleibol | 2 × aces + bloqueos |
| Individual | +20 al ganar, −10 al perder, mínimo total 0 |

Es un ranking simplificado por rendimiento y resultado, **no un ELO que calcule
probabilidad contra el rival**. Se conservan totales individuales y partidos
jugados; las estadísticas históricas permanecen asociadas a sus partidos.

### Comercio e inventario

Cada venta corresponde a un único punto de venta. La tienda admite artículos
deportivos; la cafetería snacks y bebidas. El inventario distingue existencias por
producto y ubicación y conserva los movimientos de reabastecimiento y traslado.
Los métodos de esos movimientos exigen una instancia de administrador.

La tienda aplica 15% a empleados o 8% a socios con un código válido compartido;
no suma ambos descuentos. El IVA es 19% sobre el subtotal con descuento. La
cafetería aplica impoconsumo de 8% y permite propina voluntaria; su sugerencia es 10%.
Estos valores son los del modelo del ejercicio, no una parametrización tributaria
para uso comercial. La tienda no admite propina.

Una venta con snacks que contengan alérgenos requiere confirmación de información
previa mediante `marcarAlergenosInformados()`. Las consultas de ingredientes y
alérgenos están disponibles antes de comprar.

Antes de cerrar se revalida el stock agregado de cada producto, el saldo de puntos,
los alérgenos, la propina y las restricciones de bebidas. Un rechazo no descuenta
existencias ni saldo de fidelización. La venta cerrada no se cobra de nuevo.

Por cada $1000 completos en la base pagada sin impuestos, propina ni redención se
gana un punto. Cada punto vale $10; sólo lo redime un socio y la redención no puede
superar la compra sin propina. Las mensualidades conservan socio, periodo,
concepto y fecha de pago y rechazan duplicados por socio/periodo/concepto.

### Personal y reportes

Un turno tiene una fecha concreta. La semana se representa con siete fechas;
no se repiten automáticamente los turnos. El cambio sólo se aprueba si mantiene
al menos dos entrenadores distintos y un fisioterapeuta distinto durante **todo**
el horario de atención, tanto en la fecha original como en la nueva. Se verifican
los instantes de apertura y cada inicio/fin de turno: esto detecta huecos de
cualquier duración, sin depender de muestreos de una hora.

Los reportes financieros filtran un intervalo inclusivo de fechas y consolidan
ventas de tienda, cafetería y mensualidades por rubro y mes. El total de ventas
incluye los impuestos y las propinas cobrados. Las consultas de sesiones,
asistencias, partidos, estadísticas y rankings exponen el historial deportivo.

## Persistencia

`IPersistenciaClub` desacopla los gestores del mecanismo de almacenamiento.
`PersistenciaJson` escribe **JSON de envoltura** con `formato: CDS-1` y un campo
`datos` que contiene una instantánea binaria Java codificada en Base64. No se
presenta como JSON de entidades editable a mano. Esta elección permite conservar
referencias compartidas y ciclos (socio/equipo, venta/producto e instalación/evento)
sin dependencias externas; el enunciado admite archivos binarios.

La carga valida la cabecera, filtra las clases permitidas y limita profundidad,
referencias y longitud de arreglos. La escritura usa archivo temporal y movimiento
atómico cuando el sistema de archivos lo soporta. La ruta inicial es
`datos/club-ejemplo.json`, separada de `src`. Toda la información del club se salva
en conjunto, incluidos inventario, historiales, sanciones pendientes y credenciales.
Se usa `serialVersionUID=1L`; cambiar la estructura de clases puede requerir
regenerar los datos. No se soporta edición concurrente entre procesos.

## Demostración y validación

`PruebasSistema` crea escenarios independientes de entrada manual y comprueba
resultados positivos y rechazos. Se ejecutaron 54 comprobaciones con Java 17 y
compilación `javac -encoding UTF-8 -Xlint:all`, sin advertencias. El ciclo de
persistencia comprueba identidad compartida, autenticación y ventas recuperadas.
Véanse `PRUEBAS.md` y `resultado-pruebas.txt`.

La interfaz completa de las historias de usuario queda para la etapa de consola
interactiva; esta entrega implementa métodos de dominio y demostraciones. Las
consolas incluidas autentican y muestran consultas básicas de cada rol.

## Diagramas de esta implementación

![Paquetes y clases](diagramas/paquetes.svg)

Las fuentes de [clases de diseño](diagramas/clases-diseno.puml) incluyen atributos,
métodos y relaciones. La fuente de [clases de alto nivel](diagramas/clases-alto-nivel.puml)
incluye todos los tipos y relaciones sin mostrar sus miembros. Se pueden abrir y
exportar con PlantUML. Los SVG de secuencias se incluyen para consulta inmediata:

![Reserva](diagramas/secuencia-01-reservar.svg)

![Venta](diagramas/secuencia-02-finalizar-venta.svg)

![Cambio de turno](diagramas/secuencia-03-aprobar-turno.svg)

![Partido oficial](diagramas/secuencia-04-partido-oficial.svg)

![Persistencia](diagramas/secuencia-05-persistencia.svg)
