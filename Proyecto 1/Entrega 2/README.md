# Implementación de Modelo_clases_final.pdf

Proyecto Java 17 para Eclipse. Las clases de la aplicación corresponden al PDF.
Los dos programas del paquete `pruebas` sirven para crear datos y verificarla.

## Eclipse

1. File → Import → General → Existing Projects into Workspace.
2. Seleccionar la carpeta `ClubDeportivo` y finalizar.
3. Usar un JDK 17 y ejecutar `pruebas.PruebasSistema` como Java Application.
4. Resultado esperado: **46 verificaciones correctas**.
5. Ejecutar `pruebas.DatosPrueba` para regenerar los datos de ejemplo.
6. Ejecutar `consola.ConsolaPrincipal` para iniciar sesión.

Datos de acceso de ejemplo: `admin`, `entrenador`, `entrenador2`, `fisio` o
`socio1` a `socio16`. Clave común: `demo123`.

## Terminal

Dentro de `ClubDeportivo`:

```sh
sh ejecutar.sh pruebas
sh ejecutar.sh datos
sh ejecutar.sh consola
```

Los datos se guardan en `datos`, separados de `src`. No se requieren dependencias externas.

## Archivos de entrega

- `ClubDeportivo`: proyecto Eclipse, fuentes Java, datos y pruebas ejecutables.
- `documentacion/DISENO.md`: documento de diseño de la implementación del PDF.
- `documentacion/diagramas`: fuentes editables `.puml` de clases y secuencias.

La aplicación contiene los **88 tipos del PDF**. Los dos tipos adicionales del
paquete `pruebas` son únicamente los programas de demostración exigidos por la entrega.
