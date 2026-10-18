# Proyecto 1 - Entrega 2

Implementación de **Modelo_clases_final.pdf**, adaptada a las diapositivas del curso.
La aplicación conserva los **88 tipos del modelo**. Los dos programas de `srctest`
son únicamente los programas de datos y demostración exigidos por la entrega.

## Abrir y ejecutar en Eclipse

1. File → Import → General → Existing Projects into Workspace.
2. Seleccionar la carpeta `ClubDeportivo` y finalizar.
3. Configurar un JDK 17 en Java → Installed JREs.
4. En `srctest/pruebas`, ejecutar `PruebasSistema` como Java Application.
5. Resultado esperado: **46 verificaciones correctas**.
6. Ejecutar `DatosPrueba` para regenerar `datos/club-ejemplo.json`.
7. En `src/consola`, ejecutar `ConsolaPrincipal` para iniciar sesión.

Se incluyen las configuraciones `Demostracion.launch`, `DatosPrueba.launch` y
`Consola.launch`. Su directorio de trabajo es la carpeta del proyecto Eclipse.
No se requiere Maven, Gradle ni bibliotecas externas.

## Organización

- `ClubDeportivo/src`: las clases de la aplicación según el PDF.
- `ClubDeportivo/srctest`: programas de prueba separados, como recomienda la clase.
- `ClubDeportivo/datos`: archivo persistente y datos de ejemplo.
- `documentacion/DISENO.md`: decisiones de implementación y correspondencia con las slides.
- `documentacion/diagramas`: fuentes editables de clases y secuencias, y vistas SVG.

En Eclipse se configuran `src` y `srctest` como carpetas de fuentes. Los archivos
compilados se generan en `bin`, que no se sube al repositorio.

## Datos de acceso de ejemplo

Todos los datos son ficticios. La clave común es `demo123`.

| Rol | Login |
|---|---|
| Administrador | `admin` |
| Entrenador | `entrenador` o `entrenador2` |
| Fisioterapeuta | `fisio` |
| Socio | `socio1` hasta `socio16` |

La consola muestra un menú de consulta por rol. Las operaciones del modelo se
comprueban ejecutando `PruebasSistema`, sin ingresar manualmente cada escenario.

## Terminal

Desde la carpeta `ClubDeportivo`:

```sh
sh ejecutar.sh pruebas
sh ejecutar.sh datos
sh ejecutar.sh consola
```

Las pruebas usan ciclos y clases anónimas. Las excepciones del club son verificadas:
los métodos que pueden producirlas declaran `throws ClubException`, y los programas
que pueden manejarlas utilizan `try/catch`.

Se comprobó la compilación sin advertencias con Java 17 y también con `--release 8`.
No se utilizó la interfaz gráfica de Eclipse para verificar la importación.

## Entrega

Esta carpeta reúne proyecto Eclipse, instrucciones, datos de prueba, documento de
diseño y fuentes de diagramas. El enlace al repositorio se entrega en Bloque Neón,
en la actividad Proyecto 1 - Entrega 2. Si la sección exige una organización,
verifique que el repositorio pertenezca a la indicada por el profesor.
