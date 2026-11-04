# Escape: La Habitación 🔐

Código base del **proyecto final** del curso de Lógica de Programación.
Es un juego de "salir de la habitación" en consola… pero el juego completo son
**TRES habitaciones**. La habitación 1 ya funciona de punta a punta y es igual
para todos los grupos. Las habitaciones 2 y 3 **las construye el equipo
programando**: el motor ya llama a los métodos *placeholder* que ustedes deben
completar — sin programarlos, **es imposible llegar al final del juego**.

## Contenido

1. [Cómo ejecutarlo](#cómo-ejecutarlo)
2. [Archivos](#archivos)
3. [Las dos matrices](#las-dos-matrices-el-corazón-del-diseño)
4. [Cómo se juega](#cómo-se-juega-3-pantallas-todo-por-teclado) (pantallas, puzzle y teclas)
5. [El sonido (MIDI)](#el-sonido-midi-)
6. [Formato de objetosN.csv](#formato-de-objetosncsv)
7. [**Así se trabaja: PLACEHOLDERs y TODOs**](#así-se-trabaja-placeholders-y-todos) — lo obligatorio
8. [El mapa del código](#el-mapa-del-código-️) — qué tocar para cambiar cada cosa
9. [Cómo extender el juego](#cómo-extender-el-juego-️) — recetas e ideas
10. [La documentación (JavaDoc)](#la-documentación-javadoc-)
11. [Bonus: habitaciones de otros equipos](#bonus-habitaciones-de-otros-equipos-)
12. [Lo que deben entregar](#lo-que-deben-entregar-alineado-con-los-criterios-de-evaluación)

## Cómo ejecutarlo

Necesitan el JDK (el mismo de todo el semestre). Desde esta carpeta:

```bash
javac *.java
java Main
```

## Archivos

| Archivo            | Qué es                                                              |
| ------------------ | ------------------------------------------------------------------- |
| `Main.java`        | El motor del juego, con los **PLACEHOLDER** y `TODO equipo:`.       |
| `habitacion1.txt`  | La matriz **VISUAL** de la habitación 1 (el dibujo en ASCII).       |
| `zonas1.txt`       | La matriz de **MOVIMIENTO** de la habitación 1.                     |
| `objetos1.csv`     | Los objetos de la habitación 1 (nombre, arte y detalle).            |
| `arte/*.txt`       | El ASCII art: título, preview (`mini_x.txt`) y escena de cada uno.  |
| `Sonido.java`      | La caja de música: efectos MIDI y música de fondo.                  |
| `musica/*.mid`     | La banda sonora: archivos MIDI que suenan en bucle.                 |
| `docs/`            | La **documentación JavaDoc** del proyecto (abran `index.html`).     |
| `Mapa.java`        | Demo en miniatura del movimiento en una matriz (ejecutable aparte). |

Las habitaciones 2 y 3 siguen el mismo esquema: `habitacion2.txt`, `zonas2.txt`,
`objetos2.csv`… **esos archivos los crean ustedes**, junto con el código que
los carga.

## Las dos matrices (el corazón del diseño)

Cada habitación se describe en dos matrices del mismo tamaño.
*Lo que se ve* y *lo que es* son cosas distintas:

`habitacion1.txt` — aquí se **dibuja** el cuarto (esto es lo que el jugador ve):

```
#####################################
#                                   #
#    ____                           #
#   |====|                          #
#   |____|                          #
#                                   #
#                        ______     #
#      @                /______\    #
#                       |  ??  |    #
#                       |______|    #
#   __________                      #
#  |~~~~~~~~~~|                     #
##############|    |#################
```

`zonas1.txt` — aquí se **pinta** qué es cada casilla: `.` piso, `#` sólido, una
letra = la zona de un objeto (esto es lo que el motor consulta para moverse):

```
#####################################
#...................................#
#....NNNN...........................#
#...NNNNNN..........................#
#...NNNNNN..........................#
#...................................#
#........................CCCCCC.....#
#.......................CCCCCCCC....#
#.......................CCCCCCCC....#
#.......................CCCCCCCC....#
#..AAAAAAAAAAAA.....................#
#..AAAAAAAAAAAA.....................#
##############PPPPPP#################
```

- La `@` en la matriz visual marca dónde arranca el jugador (el motor la
  encuentra y la borra del dibujo; su casilla en zonas debe ser `.`).
- 💡 **Truco para que siempre calcen**: copien la matriz visual, renómbrenla y
  reemplacen cada dibujo por su letra y el interior por puntos.

## Cómo se juega (3 pantallas, todo por teclado)

**1. La habitación.** Se mueve con `W A S D` (`W` arriba, `A` izquierda, `S`
abajo, `D` derecha). El panel muestra el progreso y el **inventario**:

```
 +============ ESCAPE: LA HABITACION ============+

 #####################################   +--------------------------+
 #                                   #   | Habitacion 1 de 3        |
 #    ____                           #   | Objetos mirados: 0 de 4  |
 #   |====|                          #   |                          |
 #   |____|                          #   | Inventario:              |
 #                                   #   |   (vacio)                |
 #                        ______     #   +--------------------------+
 #      @                /______\    #
 #                       |  ??  |    #    Moverse: W A S D
 #                       |______|    #    Pista: H | Rendirse: Q
 #   __________                      #    Musica: M | Efectos: E
 #  |~~~~~~~~~~|                     #
 ##############|    |#################

 >
```

**2. La pregunta.** Los objetos son sólidos: al chocar con uno, su preview
reemplaza el panel (`arte/mini_x.txt`) con el menú `1. Mirar de cerca /
2. Seguir explorando`.

**3. La escena.** El objeto a pantalla completa (arte grande + detalle).
Se cierra con ENTER y se vuelve al mapa.

🧩 **El puzzle de la habitación 1** (spoiler): la **alfombra** esconde la llave →
la llave abre el **candado del cofre** → el cofre revela un número (sorteado por
partida: dos dígitos, par) → la **nota** dice qué operación aplicarle (sorteada
entre 5: doble, mitad, +10, −10, al revés) → la **puerta** espera el resultado.
`calcularClave()` hace la cuenta. Como número y operación cambian en cada
partida, memorizar la clave no sirve. **Los puzzles de las habitaciones 2 y 3
los diseñan y programan ustedes.**

### Las teclas especiales

- **`H` — pista 💡**: `darPista()` revisa el estado del juego con un if-else y
  responde la pista que toca. Agreguen las de sus habitaciones.
- **`M` y `E` — sonido 🔊**: música y efectos, dos banderas `boolean`.
- **`X` — la tecla secreta 🥚**: conectada a `easterEgg()`, que no hace nada…
  todavía. Programarla es bonus (+10%).

## El sonido (MIDI) 🔊

`Sonido.java` suena con MIDI (viene en el JDK). Si no hay audio, todo falla en
silencio y el juego sigue.

- **Efectos = arreglos de números**: `60` = Do, `62` = Re, `64` = Mi, `67` =
  Sol, `72` = Do agudo (+12 por octava). Un efecto nuevo = copiar un método y
  cambiar los números. El motor ya usa `exito()`, `error()`, `recoger()`,
  `golpe()` y `victoria()`.
- **Música de fondo = archivos `.mid`**: `Sonido.musicaFondo("musica/x.mid")`
  reproduce en bucle sin detener el juego. Pueden **descargar un `.mid` de
  internet, generarlo con una IA** o componerlo (MuseScore). `ambiente.mid`
  pesa 141 bytes. Ideas: una música por habitación.

## Formato de `objetosN.csv`

```
simbolo,nombre,archivo,detalle
```

- `simbolo`: la letra usada en la matriz de zonas (mayúscula, única).
- `nombre`: cómo se presenta al jugador.
- `archivo`: el nombre base del arte → `arte/mini_<archivo>.txt` y
  `arte/<archivo>.txt`.
- `detalle`: el texto de la escena (ahí van las pistas).

⚠️ Sin comas dentro de los textos (`split(",")`) y sin tildes ni eñes.

## Así se trabaja: PLACEHOLDERs y TODOs

Dos tipos de marcas los guían por el código (búsquenlas en VS Code con
`Ctrl+Shift+F`):

- Un **PLACEHOLDER** es un método que el juego **ya llama**, pero cuyo cuerpo
  está incompleto a propósito: es el espacio donde ustedes programan. Cada uno
  explica su contrato en su comentario JavaDoc.
- Un **`// TODO equipo:`** es una tarea puntual dentro de un método que ya
  funciona (mejoras, casos nuevos, detalles).

### Los 4 placeholders obligatorios (en `Main.java`)

1. **`seleccionarTipoJuego()`** — el menú inicial del tipo de juego: HISTORIA
   (las 3 habitaciones), HABITACIÓN ÚNICA, CONTRARRELOJ… con entrada validada.
   Hoy siempre devuelve HISTORIA.
2. **`configurarJugador()`** — la terminal inicial del jugador: su nombre (y
   usarlo en los mensajes), la **dificultad 1-3** y su efecto en el juego (ej.
   FÁCIL = pistas ilimitadas, NORMAL = pistas limitadas, DIFÍCIL = máximo 3
   intentos de clave en la puerta). Hoy usa valores por defecto.
3. **`configurarArte()`** — la identidad visual: su propio título ASCII y la
   decoración de cada habitación o dificultad. Hoy imprime el título genérico.
4. **`siguienteHabitacion()`** — **la navegación completa del juego**: al abrir
   la puerta de las habitaciones 1 y 2 debe cargar la siguiente
   (`habitacionActual++` + `cargarMundo(...)` con los archivos que ustedes
   crean), mostrar una transición y devolver `false` para seguir jugando. Hoy
   el pasillo está "en obra" y el juego termina ahí: **por eso hay que
   programarlo para ganar**. El inventario se conserva entre habitaciones —
   úsenlo para que un objeto de la 1 sirva en la 3.

Al final del recorrido: la puerta de la habitación 3 muestra la victoria.

### Las reglas de oro

- El motor compila y la habitación 1 se juega completa **desde el día uno**:
  nunca rompan eso. Programen por pasos pequeños y prueben cada cambio.
- **Cada equipo construye SU versión**: habitaciones, puzzles, claves, arte,
  textos y melodías propias — su juego debe reflejar las decisiones e ideas de
  su equipo. El trabajo se rige por el
  [Código de Ética del curso](https://www.xacarana.com/cursos/codigo_etica.html):
  entreguen solo lo que puedan explicar.

## El mapa del código 🗺️

La guía de "qué tocar" para cada cambio:

| Quiero…                                   | Toco…                                                        |
| ----------------------------------------- | ------------------------------------------------------------ |
| Terminar el juego (habitaciones 2 y 3)    | `siguienteHabitacion()` + crear `habitacion2/3.txt`, `zonas2/3.txt`, `objetos2/3.csv` |
| El menú inicial y los modos de juego      | `seleccionarTipoJuego()`                                      |
| Nombre del jugador y dificultad           | `configurarJugador()` (+ mostrarla en `panelEstado()`)        |
| El título y el arte del juego             | `configurarArte()` + `arte/titulo.txt`                        |
| Rediseñar un cuarto (muros, tamaño)       | `habitacionN.txt` + `zonasN.txt`                              |
| Un objeto nuevo que se mira y lee         | los 2 `.txt` + fila en `objetosN.csv` + 2 artes en `arte/`    |
| Un objeto con sorpresa (da/exige algo)    | `preguntarObjeto()`                                           |
| Cambiar el puzzle o las operaciones       | `operaciones[]`, `calcularClave()` y el sorteo en `cargarMundo()` |
| Nuevas pistas para la tecla `H`           | `darPista()`                                                  |
| Una tecla/comando nuevo                   | el `while` de `main()`                                        |
| Mostrar algo en el panel (vida, nombre)   | `panelEstado()`                                               |
| Nuevos efectos o música                   | `Sonido.java` + `musica/`                                     |
| El easter egg de la tecla `X`             | `easterEgg()`                                                 |
| Conectar habitaciones de otros equipos    | `conectarHabitacionExterna()` (bonus)                         |

### Los métodos de `Main.java`, por bloques

- **Ciclo del juego**: `main()` — configuración inicial (placeholders), carga y
  el while de comandos. **Aquí se agregan teclas nuevas.**
- **Placeholders**: `seleccionarTipoJuego()`, `configurarJugador()`,
  `configurarArte()`, `siguienteHabitacion()`, `conectarHabitacionExterna()`.
- **Carga del mundo**: `cargarMundo(n)` (matrices + objetos + sorteo del
  puzzle), `cargarMatriz()`, `cargarObjetos()`, `leerLineas()`,
  `calcularAnchoMapa()`, `buscarInicio()`.
- **Pantalla principal**: `dibujarPantalla()`, `filaVisual()`, `panelEstado()`,
  `panelObjeto()`.
- **Movimiento**: `mover()` (W/A/S/D; la zona decide), `casillaZona()`.
- **Objetos**: `preguntarObjeto()` — **el lugar de los comportamientos
  especiales** (alfombra, cofre y nota son los moldes), `buscarObjeto()`.
- **Escenas**: `mostrarEscena(titulo, archivo, texto)`, `abrirEscena()`,
  `esperarEnter()`, `imprimirArte()`.
- **El final**: `abrirPuerta()` (la clave; decide si sigue el viaje o gana),
  `mostrarVictoria()`.
- **Puzzle, pistas y secreto**: `calcularClave()`, `darPista()`, `easterEgg()`.
- **Estado**: `guardarObjeto()`, `tieneObjeto()`, `recordarMirado()`.
- **Utilidades**: `partirTexto()`, `centrar()`, `enmarcar()`, `repetir()`,
  `bordeEscena()`, `leerOpcion()`.

## Cómo extender el juego 🛠️

### Receta 1: un objeto nuevo, en 4 pasos

1. Dibújenlo en la matriz visual de su habitación.
2. Pinten su zona con una letra nueva en la matriz de zonas.
3. Agreguen su fila en el CSV de la habitación.
4. Creen `arte/mini_x.txt` y `arte/x.txt`.

### Receta 2: comportamiento especial

En `preguntarObjeto()` está el punto exacto. La alfombra (entrega), el cofre
(exige y cambia de escena) y la nota (texto dinámico) son los moldes:

```java
if (simbolo == 'L') {           // la lampara
    abrirEscena(objeto);
    hayLuz = true;              // una variable static boolean nueva
    mensaje = "La lampara ilumina la habitacion...";
    return false;
}
```

### Receta 3: el inventario en sus puzzles

`guardarObjeto()` y `tieneObjeto()` ya funcionan: un objeto que entrega algo,
otro que lo exige (como el candado del cofre), cadenas de dependencias entre
habitaciones (la ganzúa de la habitación 1 abre la rejilla de la 3…).

### Ideas de puzzles

Pistas repartidas en varios objetos, contar elementos escondidos en el arte,
acertijos en el `detalle`, objetos que mienten, secuencias numéricas, claves
que dependen de la dificultad.

### Retos mayores (suben la nota)

- **Contador de movimientos** (oxígeno) ligado a la dificultad.
- **Colores ANSI**: pistas en amarillo, peligro en rojo, victoria en verde.
- **Banda sonora propia** por habitación.
- **🥚 Easter egg (+10%)**: programen la tecla secreta `X` en `easterEgg()`.
- **Más pistas**: nuevos `if` en `darPista()` por habitación.

## La documentación (JavaDoc) 📚

**JavaDoc** es el estándar de Java para documentar código: comentarios
especiales `/** ... */` sobre clases y métodos, de los que la herramienta
`javadoc` genera un **sitio web navegable** con toda la referencia del
proyecto. Así se documentan las bibliotecas profesionales (la documentación
oficial de Java está hecha así).

- La documentación de este proyecto ya está generada en **`docs/index.html`**
  (ábranla en el navegador: ahí están los contratos de todos los métodos,
  incluidos los placeholders).
- Cuando agreguen o cambien métodos, documenten con `/** ... */` y regeneren:

```bash
javadoc -d docs -charset UTF-8 *.java
```

- **Entregable**: la documentación regenerada con sus métodos nuevos hace
  parte de la nota.

## Bonus: habitaciones de otros equipos 🌐

Como todas las habitaciones del salón usan el mismo formato
(`habitacionN.txt` + `zonasN.txt` + `objetosN.csv` + su arte), se puede armar
un **juego gigante** conectando habitaciones de varios equipos. Esto **solo
queda indicado**: el placeholder `conectarHabitacionExterna()` en `Main.java`
trae las instrucciones (intercambiar la carpeta de una habitación, copiarla
con un número nuevo, cargarla desde `siguienteHabitacion()` y subir
`TOTAL_HABITACIONES`). Implementarlo con otro equipo vale **+10%**.

## Lo que deben entregar (alineado con los criterios de evaluación)

1. **El juego completo**: las habitaciones 2 y 3 propias (matrices, objetos,
   puzzles y artes) y la **navegación completa** hasta la victoria.
2. **Los 4 placeholders implementados**: tipo de juego, terminal inicial del
   jugador con **dificultad aplicada**, arte configurado y el viaje entre
   habitaciones.
3. **Entradas validadas**: el juego nunca se rompe por un dato mal ingresado.
4. **La documentación JavaDoc regenerada** con sus métodos.
5. **Sustentación con presentación**: diapositivas con la arquitectura de su
   juego, qué programaron en cada placeholder, demo en vivo y participación de
   todos los integrantes. (No hay preguntas de teoría: la nota sale de lo que
   construyeron y de cómo lo explican.)

## Primeros pasos y reglas de la entrega

El flujo para arrancar:

1. **Descarguen** `escape_la_habitacion.zip` desde el sitio del curso y
   descomprímanlo.
2. **Creen el repositorio** `final_aventura` en GitHub y suban este código tal
   cual como **primer commit** (sin archivos `.class`).
3. Agreguen a todos los integrantes como colaboradores, jueguen la habitación 1
   y lean los placeholders antes de escribir código.

Las reglas:

- **Equipos: mínimo 2 y máximo 3 personas.** Pueden repetir compañeros de
  ejercicios pasados.
- Todos los integrantes deben tener commits propios y equitativos.
- El trabajo en equipo, el uso de la IA y la autoría se rigen por el
  [Código de Ética del curso](https://www.xacarana.com/cursos/codigo_etica.html)
  (ver también "Las reglas de oro").
- Un método hace una sola cosa y la hace bien.

¡Suerte! Y recuerden: la habitación siempre tiene salida… si la programan. 🎮
