# Datos de ejemplo

`club-ejemplo.json` se genera ejecutando `pruebas.DatosPrueba`, en `srctest`.
Todos los datos son ficticios. Los turnos se crean para el día siguiente a la generación.
Regenerar los datos con `sh ejecutar.sh datos` si se trabaja en otra fecha.

La clase `PersistenciaJson` conserva el nombre del modelo. Usa una envoltura JSON
CDS-1 de cuatro líneas cuyo campo `datos` contiene el grafo Java serializado en
Base64. La serialización permite recuperar las mismas relaciones entre objetos.
Se usan BufferedReader, PrintWriter, ObjectInputStream y ObjectOutputStream.
El archivo está diseñado para ser escrito y leído por esta aplicación.

La clave de ejemplo es `demo123`. Los usuarios son `admin`, `entrenador`,
`entrenador2`, `fisio` y `socio1` a `socio16`.
