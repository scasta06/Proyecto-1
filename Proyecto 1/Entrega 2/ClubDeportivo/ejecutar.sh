#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p bin
find src -name '*.java' -print > fuentes.txt
javac -encoding UTF-8 -Xlint:all -d bin @fuentes.txt
rm fuentes.txt
case "${1:-pruebas}" in
  pruebas) java -cp bin pruebas.PruebasSistema ;;
  datos) java -cp bin pruebas.DatosPrueba datos/club-ejemplo.json ;;
  consola) java -cp bin consola.ConsolaPrincipal "${2:-datos/club-ejemplo.json}" ;;
  *) echo "Uso: sh ejecutar.sh [pruebas|datos|consola]"; exit 1 ;;
esac
