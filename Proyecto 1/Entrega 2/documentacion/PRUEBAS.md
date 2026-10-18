# Pruebas y demostraciones

Ejecute `pruebas.PruebasSistema` en Eclipse o `sh ejecutar.sh pruebas` desde
`ClubDeportivo`. No requiere JUnit ni activar `-ea`: las comprobaciones lanzan
`AssertionError` directamente si el resultado es incorrecto y el proceso falla.

| Verificaciones | Comportamiento |
|---|---|
| 1–5 | Autenticación, contraseña incorrecta, usuarios duplicados, categorías y nivel |
| 6–12 | Reserva, superposición, contigüidad, cancelación, horario y modalidad |
| 13–17 | Práctica, niveles, ranking y duplicados de resultado/estadística |
| 18–21 | Asistencia, notas, convocatoria y único equipo por deporte |
| 22–27 | Descuentos, IVA, stock, fidelización y venta repetida |
| 28–30 | Alérgenos, ausencia de cambios tras rechazo, propina y puntos |
| 31–33 | Traslado, existencias insuficientes y sugerencias |
| 34–36 | Tabla, suspensión por tarjetas y cumplimiento |
| 37–39 | Cobertura de personal, rechazo y conservación del estado |
| 40–41 | Mensualidad duplicada y reporte por periodo/rubro |
| 42–44 | Bebida caliente, restricción en ambos sentidos e ingreso posterior |
| 45–48 | Persistencia: credenciales, relaciones, ventas y archivo incorrecto |
| 49–50 | Partido interno: mínimo por equipo y resultado válido |
| 51 | Cambio de turno válido |
| 52–54 | Eliminatoria: empate, eliminación y bloqueo de nuevos partidos |

Resultado verificado: **54 verificaciones correctas**. El registro completo está
en `resultado-pruebas.txt`. Los errores esperados se capturan como `ClubException`;
un error de programación o un resultado incorrecto hace fallar el programa.

Las pruebas de persistencia crean una carpeta temporal y la eliminan al terminar.
La demostración no modifica el archivo de ejemplo. Para regenerar los datos iniciales,
ejecute `pruebas.DatosPrueba`, que sí reemplaza `datos/club-ejemplo.json`.

El proyecto se verificó con el compilador y la JVM 17. Los metadatos de Eclipse y
las configuraciones `.launch` están incluidos; no se verificó una importación por
la interfaz gráfica de Eclipse en este entorno.
