# Anexo · Uso de inteligencia artificial

Fecha: 18 de octubre de 2026. Herramienta: ChatGPT / Codex.

## Uso realizado

Se utilizó IA para leer el modelo final, generar código Java de entidades y gestores,
crear la persistencia y los programas de demostración, depurar la compilación,
preparar datos ficticios, documentar decisiones y producir fuentes UML consistentes
con el código. Esto incluye generación de lógica funcional, no sólo getters,
setters y constructores. Este registro no constituye aprobación del profesor sobre
el alcance de uso permitido en la asignatura.

El proyecto debe revisarse, comprenderse y sustentarse por sus autores. Las reglas
de uso de IA de la asignatura siguen siendo aplicables; este anexo describe el uso
real sin atribuirle una autoría manual que no tuvo.

## Solicitudes recibidas en esta conversación

1. «necesito implementar esto en eclipse en lenguaje java y subirlo a este
   repositorio https://github.com/scasta06/Proyecto-1», acompañado de los puntos
   de implementación, consistencia del diseño y estructura de Entrega 2.
2. «el pdf que esta en el repositorio no es el bueno el que te mande es el bueno
   corregido ten enesto en cuenta».
3. Se adjuntaron versiones de imagen y PDF del modelo; las primeras no pudieron
   leerse por problemas técnicos de acceso.
4. «que hago entonces», respecto al acceso al modelo.
5. Se compartió `Modelo_clases_final.pdf`, cuyo texto sí pudo recuperarse y se
   utilizó como referencia vigente.

## Decisiones tomadas por la herramienta

- Java SE 17 sin dependencias externas e importación como proyecto Eclipse.
- Colecciones estándar `List`, `Map` y `Set`, coherentes con el modelo Java recibido.
- Persistencia de grafo binario dentro de una envoltura JSON versionada.
- Fórmulas simples de ranking y valores configurables explicitados en DISENO.md.
- Extensiones Bebida, SugerenciaProducto y PartidoInterno por requisitos del caso.
- Demostraciones ejecutables sin entrada manual y diagramas PlantUML editables.

## Validación automatizada

Compilación con `javac -Xlint:all` y 54 comprobaciones ejecutables. Esta validación
comprueba los escenarios documentados; no reemplaza la revisión del grupo ni la
validación académica del diseño y de los criterios de la sección.
