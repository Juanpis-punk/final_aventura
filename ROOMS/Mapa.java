import java.util.Scanner;

/**
 * EL TABLERO DEL MUNDO - La navegacion en miniatura.
 *
 * <p>Version de juguete del movimiento que usa el juego: una sola matriz 5x5
 * que el jugador recorre con W, A, S, D. Estudienla y ejecutenla aparte para
 * entender el movimiento y los limites de una matriz antes de leer el motor
 * completo (Main.java).</p>
 *
 * <p>Como se conecta con Main.java: mover() en Main es este mismo
 * procesarComando(), pero alla la matriz de ZONAS decide si la casilla
 * destino es piso (.), algo solido (#) o un objeto que pregunta (letra).
 * Aqui hay UNA matriz; el juego usa DOS del mismo tamano por habitacion.</p>
 *
 * <p>Compilar y ejecutar: <code>javac Mapa.java</code> y
 * <code>java Mapa</code>.</p>
 */
public class Mapa {

    /** Cuantas filas tiene el tablero de juguete. */
    public static final int FILAS = 5;

    /** Cuantas columnas tiene el tablero de juguete. */
    public static final int COLUMNAS = 5;

    /** El estado del mundo: false = sin visitar, true = visitado. */
    public static boolean[][] visitado = new boolean[FILAS][COLUMNAS];

    /** La fila donde esta el jugador; empieza en el centro. */
    public static int fila = 2;

    /** La columna donde esta el jugador; empieza en el centro. */
    public static int columna = 2;

    /** El lector de teclado del demo. */
    public static Scanner lector = new Scanner(System.in);

    /** El ciclo del demo: dibujar, leer comando, mover, hasta la Q. */
    public static void main(String[] args) {
        visitado[fila][columna] = true; // la casilla inicial cuenta como visitada

        System.out.println("=== EL TABLERO DEL MUNDO ===");
        System.out.println("Muevete con W, A, S, D. Escribe Q para salir.");

        boolean jugando = true;
        while (jugando) {
            dibujarMapa();
            System.out.print("> Comando (W/A/S/D/Q): ");
            String comando = lector.nextLine().trim().toUpperCase();
            if (comando.equals("Q")) {
                jugando = false;
            } else {
                procesarComando(comando);
            }
        }

        System.out.println("Exploraste " + contarVisitadas() + " de "
                + (FILAS * COLUMNAS) + " casillas. Hasta pronto!");
    }

    /**
     * Dibuja la matriz en consola: @ el jugador, x casilla ya visitada,
     * . casilla sin explorar. La brujula de la derecha recuerda hacia
     * donde mueve cada tecla.
     */
    public static void dibujarMapa() {
        System.out.println();
        System.out.println("+-----------+");
        for (int i = 0; i < FILAS; i++) {
            System.out.print("| ");
            for (int j = 0; j < COLUMNAS; j++) {
                if (i == fila && j == columna) {
                    System.out.print("@ ");
                } else if (visitado[i][j]) {
                    System.out.print("x ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.print("|");
            if (i == 1) System.out.print("     W");
            if (i == 2) System.out.print("   A + D");
            if (i == 3) System.out.print("     S");
            System.out.println();
        }
        System.out.println("+-----------+");
        // TODO equipo: marcar casillas especiales (T = tesoro, P = peligro)
        //              usando otra matriz o simbolos extra.
    }

    /**
     * Mueve al jugador segun la tecla. Ojo con la relacion entre teclas
     * e indices de la matriz: W = fila - 1 (subir), S = fila + 1 (bajar),
     * A = columna - 1 (izquierda), D = columna + 1 (derecha). Si el
     * movimiento sale del tablero, el jugador no se mueve.
     *
     * @param comando W, A, S o D en mayuscula.
     */
    public static void procesarComando(String comando) {
        int nuevaFila = fila;
        int nuevaColumna = columna;

        if (comando.equals("W")) {
            nuevaFila = fila - 1;
        } else if (comando.equals("S")) {
            nuevaFila = fila + 1;
        } else if (comando.equals("D")) {
            nuevaColumna = columna + 1;
        } else if (comando.equals("A")) {
            nuevaColumna = columna - 1;
        } else {
            System.out.println("Comando invalido. Usa W, A, S, D o Q.");
            return;
        }

        if (dentroDelTablero(nuevaFila, nuevaColumna)) {
            fila = nuevaFila;
            columna = nuevaColumna;
            visitado[fila][columna] = true;
        } else {
            System.out.println("Un muro invisible te detiene: no puedes salir del mapa.");
        }
        // En Main.java este mismo punto es donde un objeto "pregunta":
        // la matriz de zonas decide que hay en la casilla destino.
    }

    /**
     * Valida los limites del tablero.
     *
     * @param f la fila a revisar.
     * @param c la columna a revisar.
     * @return true si la posicion existe dentro de la matriz.
     */
    public static boolean dentroDelTablero(int f, int c) {
        return f >= 0 && f < FILAS && c >= 0 && c < COLUMNAS;
    }

    /**
     * Recorre toda la matriz contando las casillas visitadas.
     *
     * @return cuantas casillas distintas piso el jugador.
     */
    public static int contarVisitadas() {
        int total = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (visitado[i][j]) {
                    total++;
                }
            }
        }
        return total;
    }
}
