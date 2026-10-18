# Proyecto 1 · Entrega 2 · Club Deportivo Séneca

Implementación Java 17 del modelo corregido **Modelo_clases_final.pdf**, recibido el
18 de octubre de 2026. El PDF anterior de Entrega 1 es histórico; no se usó como
modelo vigente. La Entrega 1 se conserva intacta.

## Abrir en Eclipse

1. Instale o seleccione un **JDK 17** en Eclipse → Preferences → Java → Installed JREs.
2. Abra **File → Import → General → Existing Projects into Workspace**.
3. En *Select root directory* seleccione la carpeta `ClubDeportivo` de esta entrega.
4. Marque `ClubDeportivoSeneca` y termine la importación.
5. Abra `src/pruebas/PruebasSistema.java` y seleccione **Run As → Java Application**.
6. La consola debe terminar con **RESULTADO: 54 verificaciones correctas.**

También se incluyen `Demostracion.launch`, `DatosPrueba.launch` y `Consola.launch`.
Se ejecutan con **Run As → ...** desde Eclipse. Las rutas de trabajo apuntan a la
carpeta del proyecto importado, donde está `datos`.

No se necesitan Maven, Gradle, JUnit ni bibliotecas externas.

## Datos y consola

El archivo `ClubDeportivo/datos/club-ejemplo.json` trae 16 socios, un administrador,
dos entrenadores, un fisioterapeuta, seis deportes, seis instalaciones, un equipo,
una academia, productos y existencias. Todos los datos son ficticios.

Para regenerarlo, ejecute `pruebas.DatosPrueba`. Las fechas de turnos se calculan
respecto al día de generación. Para iniciar sesión, ejecute `consola.ConsolaPrincipal`.

| Rol | Login | Clave de ejemplo |
|---|---|---|
| Administrador | `admin` | `demo123` |
| Entrenador | `entrenador` o `entrenador2` | `demo123` |
| Fisioterapeuta | `fisio` | `demo123` |
| Socio | `socio1` hasta `socio16` | `demo123` |

La consola permite iniciar sesión y consultar un resumen del rol. **La demostración
completa de la lógica se ejecuta con PruebasSistema**, que no requiere introducir
datos. La interfaz interactiva de todas las historias no forma parte de esta entrega.

## Ejecutar desde Terminal en macOS/Linux

Desde la carpeta `ClubDeportivo`:

```sh
sh ejecutar.sh pruebas
sh ejecutar.sh datos
sh ejecutar.sh consola
```

El primer comando compila todo y ejecuta las demostraciones. El segundo restablece
el archivo de ejemplo; el tercero inicia sesión sobre ese archivo.
Para usar una copia de trabajo: `sh ejecutar.sh consola datos/club-trabajo.json`
después de copiar allí `datos/club-ejemplo.json`.

## Archivos de entrega

| Ruta | Contenido |
|---|---|
| `ClubDeportivo/src` | Modelo, gestores, excepciones, persistencia, consola y pruebas |
| `ClubDeportivo/datos` | Archivo inicial y explicación del formato |
| `documentacion/DISENO.md` | Decisiones, restricciones, reglas y trazabilidad |
| `documentacion/INVENTARIO_CLASES.md` | Inventario de tipos por paquete |
| `documentacion/PRUEBAS.md` | Cobertura y resultado de ejecución |
| `documentacion/ANEXO_IA.md` | Registro del uso de IA y solicitudes recibidas |
| `documentacion/diagramas/clases-diseno.puml` | Fuente de clases, atributos, métodos y relaciones |
| `documentacion/diagramas/clases-alto-nivel.puml` | Fuente de clases y relaciones sin detalle de miembros |
| `documentacion/diagramas/secuencia-*.puml` | Cinco diagramas de secuencia coherentes con el código |
| `documentacion/diagramas/paquetes.svg` | Vista vectorial legible del inventario de paquetes y clases |

Los archivos `.puml` son fuentes editables de PlantUML. El proyecto Java no depende
de PlantUML para compilar ni ejecutarse.

## Entrega en Bloque Neón

Entregue el enlace al repositorio en **Proyecto 1 - Entrega 2**. Si la sección exige
una organización, verifique que el repositorio pertenezca a ella: el repositorio
indicado actualmente está bajo la cuenta personal `scasta06`.
