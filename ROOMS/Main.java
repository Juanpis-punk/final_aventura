import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * ESCAPE: LA HABITACION - Motor del juego (codigo base del profesor).
 *
 * <p>El juego completo son TRES habitaciones. Despiertas encerrado y debes
 * escapar de cada una resolviendo su puzzle. La habitacion 1 ya funciona de
 * punta a punta; las habitaciones 2 y 3 <b>las construye el equipo
 * programando</b>: el motor ya llama a los metodos PLACEHOLDER que ustedes
 * deben completar para poder llegar al final del juego.</p>
 *
 * <p>Fuentes de verdad de cada habitacion N:</p>
 * <ul>
 *   <li><code>habitacionN.txt</code> - la matriz VISUAL (el dibujo).</li>
 *   <li><code>zonasN.txt</code> - la matriz de MOVIMIENTO (que ES cada casilla).</li>
 *   <li><code>objetosN.csv</code> - nombre, arte y detalle de cada objeto.</li>
 *   <li><code>arte/*.txt</code> y <code>musica/*.mid</code> - compartidos.</li>
 * </ul>
 *
 * <p><b>PLACEHOLDERS vs TODOs:</b> un PLACEHOLDER es un metodo que el juego ya
 * llama pero cuyo cuerpo esta incompleto a proposito: es el espacio donde el
 * equipo programa (busquen el banner <code>PLACEHOLDER</code>). Un
 * <code>// TODO equipo:</code> es una tarea puntual dentro de un metodo que ya
 * funciona. En VS Code se encuentran todos con Ctrl+Shift+F buscando
 * <code>TODO equipo</code> o <code>PLACEHOLDER</code>.</p>
 *
 * <p><b>Importante:</b> cada equipo construye SU propia version: claves,
 * habitaciones, arte, textos y melodias distintas. El trabajo se rige por el
 * Codigo de Etica del curso (en el sitio): entreguen solo lo que puedan
 * explicar.</p>
 *
 * <p>Compilar y ejecutar: <code>javac *.java</code> y <code>java Main</code>.
 * Documentacion: <code>javadoc -d docs -charset UTF-8 *.java</code>.</p>
 */
public class Main {

    /** El lector de teclado unico de todo el juego. */
    public static Scanner lector = new Scanner(System.in);

    /** La matriz VISUAL de la habitacion actual: lo que se VE (habitacionN.txt). */
    public static char[][] visual;

    /**
     * La matriz de MOVIMIENTO (zonasN.txt), del mismo tamano que la visual:
     * que ES cada casilla ('.' piso, '#' solido, letra = objeto).
     */
    public static char[][] zonas;

    /** La tabla de objetosN.csv: [0] simbolo | [1] nombre | [2] archivo | [3] detalle. */
    public static String[][] objetos;

    /** En que habitacion esta el jugador: el progreso del juego. */
    public static int habitacionActual = 1;

    /** Cuantas habitaciones tiene el juego: la victoria esta tras la ultima. */
    public static final int TOTAL_HABITACIONES = 3;

    /** El nombre del jugador; lo pide configurarJugador() (PLACEHOLDER 2). */
    public static String nombreJugador = "aventurero";

    /** La dificultad elegida: 1 = facil, 2 = normal, 3 = dificil (PLACEHOLDER 2). */
    public static int dificultad = 1;

    /** El modo elegido en seleccionarTipoJuego() (PLACEHOLDER 1); 1 = historia. */
    public static int tipoJuego = 1;

    /** La fila de la matriz donde esta parado el jugador. */
    public static int fila;

    /** La columna de la matriz donde esta parado el jugador. */
    public static int columna;

    /** El mensaje que se muestra bajo el mapa en cada turno. */
    public static String mensaje = "Despiertas encerrado y no recuerdas nada. Explora.";

    /** Los simbolos de los objetos ya mirados de cerca (sin repetir). */
    public static String mirados = "";

    /** El inventario del jugador: un arreglo de tamano fijo. */
    public static String[] inventario = new String[10];

    /** Cuantas casillas del inventario estan ocupadas. */
    public static int totalInventario = 0;

    /** El ancho del mapa en caracteres; se calcula de la matriz visual. */
    public static int anchoMapa;

    /** El ancho interior del panel derecho, en caracteres. */
    public static final int ANCHO_PANEL = 26;

    /** El ancho de las escenas y pantallas grandes, en caracteres. */
    public static final int ANCHO_ESCENA = 64;

    /**
     * El numero grabado en el cofre. Se sortea al cargar cada habitacion
     * (dos digitos, par y sin terminar en cero) para que la clave cambie
     * en cada partida.
     */
    public static int numeroCofre;

    /** Las 5 operaciones posibles del puzzle; la nota revela la sorteada. */
    public static String[] operaciones = {
        "el DOBLE del numero",
        "la MITAD del numero",
        "el numero MAS 10",
        "el numero MENOS 10",
        "el numero escrito AL REVES"
    };

    /** El indice (0 a 4) de la operacion sorteada para esta habitacion. */
    public static int operacionElegida;

    /** La clave de la puerta: la operacion aplicada al numero del cofre. */
    public static String clave;

    /**
     * El numero de la pelota escondida bajo la almohada de la cama
     * (habitacion 2). Un digito del 1 al 9, sorteado en cada partida.
     */
    public static int pelotaCama;

    /**
     * El numero de la pelota escondida dentro de la chimenea (habitacion 2).
     * Un digito del 1 al 9, sorteado en cada partida. Va PRIMERO en la clave.
     */
    public static int pelotaChimenea;

    /**
     * El numero que pide la puerta final (habitacion 3) en dificultad 2 y 3.
     * Se sortea del 1 al 5 en cada partida.
     */
    public static int numeroPuerta3;

    /** Cuantos movimientos da el modo CONTRARRELOJ en cada habitacion. */
    public static final int MOVIMIENTOS_POR_HABITACION = 100;

    /**
     * Los movimientos que le quedan al jugador en el modo CONTRARRELOJ.
     * Se recargan en cargarMundo() al entrar a cada habitacion.
     */
    public static int movimientosRestantes;

    /**
     * El ciclo de vida del juego: configuracion inicial (los placeholders),
     * carga de la habitacion 1 y el ciclo principal de comandos.
     */
    public static void main(String[] args) {
        Sonido.iniciar();

        // ---- Configuracion inicial: aqui viven 3 de los placeholders ----
        configurarArte();
        tipoJuego = seleccionarTipoJuego();
        configurarJugador();

        // En HABITACION UNICA seleccionarTipoJuego() ya cambio habitacionActual;
        // en los otros modos sigue en 1.
        if (!cargarMundo(habitacionActual)) {
            return; // sin datos no hay juego
        }
        if (habitacionActual > 1) {
            mensaje = mensajeEntrada();
        }
        Sonido.musicaFondo("musica/ambiente.mid");

        boolean escapo = false;
        boolean sinMovimientos = false; // solo pasa en CONTRARRELOJ
        boolean jugando = true;
        while (jugando) {
            dibujarPantalla(panelEstado());
            System.out.print(" > ");
            String comando = lector.nextLine().trim().toUpperCase();
            if (comando.equals("Q")) {
                jugando = false;
            } else if (comando.equals("H")) {
                mensaje = darPista();
            } else if (comando.equals("M")) {
                Sonido.alternarMusica();
                mensaje = "Musica de fondo: " + (Sonido.musicaActiva ? "ACTIVADA" : "APAGADA");
            } else if (comando.equals("E")) {
                Sonido.alternarEfectos();
                mensaje = "Efectos de sonido: " + (Sonido.efectosActivos ? "ACTIVADOS" : "APAGADOS");
            } else if (comando.equals("X")) {
                easterEgg(); // la tecla secreta (no aparece en el panel)
            } else {
                escapo = mover(comando);
                if (escapo) {
                    jugando = false;
                } else if (tipoJuego == 3 && movimientosRestantes <= 0) {
                    sinMovimientos = true;
                    jugando = false;
                }
            }
        }

        if (sinMovimientos) {
            Sonido.error();
            System.out.println();
            System.out.println(" Se acabaron tus movimientos, " + nombreJugador
                    + ". La habitacion gana esta vez...");
        } else if (!escapo) {
            System.out.println();
            System.out.println(" Te rindes. La habitacion gana esta vez...");
        }
        Sonido.apagar(); // sin esto el programa no termina
    }

    /* ====================================================================
     *                    LOS PLACEHOLDERS DEL EQUIPO
     *
     * El motor YA llama a estos metodos. Completarlos es obligatorio para
     * terminar el juego. Cada uno explica su contrato en el javadoc.
     * ==================================================================== */

    /**
     * PLACEHOLDER 1 - EL TIPO DE JUEGO.
     *
     * <p>Debe mostrar un menu inicial y leer la opcion del jugador (validada,
     * como leerOpcion()). Modos sugeridos: 1 = HISTORIA (las 3 habitaciones),
     * 2 = HABITACION UNICA (elegir cual y escapar solo de esa),
     * 3 = CONTRARRELOJ (historia con movimientos limitados). El modo elegido
     * se guarda en tipoJuego y el resto del motor debe respetarlo.</p>
     *
     * @return el modo elegido: 1 = historia, 2 = habitacion unica, 3 = contrarreloj.
     */
    public static int seleccionarTipoJuego() {
        // Primero se explica cada modo y luego se lee validado.
        System.out.println(" Elige el modo de juego:");
        System.out.println("   1. HISTORIA         - las 3 habitaciones, una tras otra.");
        System.out.println("   2. HABITACION UNICA - eliges una habitacion y escapas solo de ella.");
        System.out.println("   3. CONTRARRELOJ     - la historia, pero solo tienes "
                + MOVIMIENTOS_POR_HABITACION + " movimientos");
        System.out.println("                         en cada habitacion.");
        int modo = leerTresOpciones();

        // En HABITACION UNICA tambien se elige el cuarto: main() carga
        // habitacionActual, asi que basta con cambiarlo aqui.
        if (modo == 2) {
            System.out.println();
            System.out.println(" Que habitacion quieres jugar?");
            habitacionActual = leerTresOpciones();
        }
        System.out.println();
        System.out.println(" Modo de juego: " + nombreModo(modo) + ".");
        return modo;
    }

    /**
     * El nombre en palabras de un modo de juego, para el panel y los mensajes.
     *
     * @param modo el tipo de juego (1, 2 o 3).
     * @return HISTORIA, HABITACION UNICA o CONTRARRELOJ.
     */
    public static String nombreModo(int modo) {
        if (modo == 1) return "HISTORIA";
        if (modo == 2) return "HABITACION UNICA";
        return "CONTRARRELOJ";
    }

    /**
     * PLACEHOLDER 2 - LA TERMINAL INICIAL DEL JUGADOR.
     *
     * <p>Debe configurar la partida conversando con el jugador: preguntar su
     * nombre (guardarlo en nombreJugador y usarlo en los mensajes del juego),
     * elegir la dificultad 1-3 (guardarla en dificultad) y aplicarla: por
     * ejemplo en FACIL hay pistas ilimitadas, en NORMAL cuestan y en DIFICIL
     * la puerta admite maximo 3 intentos de clave. Tambien pueden ofrecer
     * aqui el estado inicial del sonido (M/E).</p>
     */
    public static void configurarJugador() {
        // El nombre: si el jugador solo presiona ENTER se conserva "aventurero",
        // asi nombreJugador nunca queda vacio en los mensajes.
        System.out.print(" Como te llamas? (ENTER = aventurero): ");
        String nombre = lector.nextLine().trim();
        if (!nombre.isEmpty()) {
            nombreJugador = nombre;
        }

        // La dificultad: primero se explica y luego se lee validada.
        mostrarDificultades();
        dificultad = leerTresOpciones();
        System.out.println();
        System.out.println(" Bienvenido, " + nombreJugador + ". Dificultad: "
                + nombreDificultad(dificultad) + ".");
        System.out.println();
    }

    /**
     * Explica en pantalla que cambia cada dificultad, para que el jugador
     * elija sabiendo lo que le espera. La habitacion 1 es igual en todas.
     */
    public static void mostrarDificultades() {
        System.out.println();
        System.out.println(" Elige la dificultad (afecta las habitaciones 2 y 3):");
        System.out.println("   1. FACIL   - la puerta 2 trae una pista clara y la puerta 3");
        System.out.println("                se abre solo con la llave.");
        System.out.println("   2. NORMAL  - la pista de la puerta 2 esta corrupta y la");
        System.out.println("                puerta 3 tambien pide un numero secreto.");
        System.out.println("   3. DIFICIL - igual que NORMAL, pero con los controles");
        System.out.println("                invertidos en las habitaciones 2 y 3.");
    }

    /**
     * Lee una opcion de 1 a 3 validando la entrada (mismo estilo que
     * leerOpcion()): repite la pregunta hasta recibir 1, 2 o 3, asi nunca
     * rompe el programa. La usan el modo, la habitacion y la dificultad.
     *
     * @return la opcion elegida: 1, 2 o 3.
     */
    public static int leerTresOpciones() {
        int opcion = -1; // -1 significa "todavia no hay una opcion valida"
        do {
            System.out.print(" > Elige (1, 2 o 3): ");
            String entrada = lector.nextLine().trim();
            // Se compara como texto ANTES de convertir: asi parseInt nunca
            // recibe letras ni vacios y no puede lanzar una excepcion.
            if (entrada.equals("1") || entrada.equals("2") || entrada.equals("3")) {
                opcion = Integer.parseInt(entrada);
            } else {
                System.out.println(" Entrada invalida. Intenta de nuevo.");
            }
        } while (opcion == -1);
        return opcion;
    }

    /**
     * El nombre en palabras de una dificultad, para el panel y los mensajes.
     *
     * @param nivel la dificultad (1, 2 o 3).
     * @return FACIL, NORMAL o DIFICIL.
     */
    public static String nombreDificultad(int nivel) {
        if (nivel == 1) return "FACIL";
        if (nivel == 2) return "NORMAL";
        return "DIFICIL";
    }

    /**
     * PLACEHOLDER 3 - EL ASCII ART DEL JUEGO.
     *
     * <p>Debe configurar la identidad visual: un titulo propio del equipo
     * (reemplacen arte/titulo.txt o elijan el archivo segun el tipo de juego
     * o la dificultad), y la decoracion de cada habitacion (por ejemplo un
     * arte distinto al entrar a la 2 y a la 3). Hoy solo imprime el titulo
     * generico del profesor.</p>
     */
    public static void configurarArte() {
        // TODO equipo: titulo propio y arte por habitacion/dificultad.
        imprimirArte("arte/titulo.txt");
        System.out.println();
    }

    /**
     * PLACEHOLDER 4 - LA NAVEGACION COMPLETA DEL JUEGO.
     *
     * <p>Se llama cuando el jugador abre la puerta de una habitacion que NO
     * es la ultima. Debe llevarlo a la siguiente: sumar 1 a habitacionActual,
     * llamar cargarMundo(habitacionActual) con los archivos que ustedes crean
     * (habitacion2.txt, zonas2.txt, objetos2.csv... y los de la 3), mostrar
     * una transicion (arte + mensaje) y retornar false para que el ciclo siga
     * jugando. El inventario se conserva entre habitaciones: usenlo para que
     * un objeto de la habitacion 1 sirva en la 3.</p>
     *
     * <p>Hoy el pasillo esta "en obra": el juego termina aqui. Por eso, sin
     * programar este metodo, ES IMPOSIBLE llegar al final del juego.</p>
     *
     * @return true si el juego debe terminar; false para seguir jugando en la
     *         nueva habitacion.
     */
    public static boolean siguienteHabitacion() {
        // Se guarda de donde viene el jugador ANTES de sumar, para la transicion.
        int anterior = habitacionActual;
        habitacionActual++;

        // cargarMundo() reemplaza las matrices, los objetos y sortea el puzzle
        // nuevo. El inventario NO se toca: lo que el jugador carga viaja con el.
        if (!cargarMundo(habitacionActual)) {
            System.out.println(" No se pudo cargar la habitacion " + habitacionActual
                    + ". Revisen sus archivos.");
            return true; // sin datos no se puede seguir jugando
        }

        mostrarTransicion(anterior, habitacionActual);
        mensaje = mensajeEntrada();
        return false; // false = el ciclo de main sigue en la habitacion nueva
    }

    /**
     * La pantalla de transicion entre habitaciones, con el mismo estilo de
     * las escenas (bordes y texto centrado) y una pausa con ENTER.
     *
     * @param desde la habitacion de la que escapo el jugador.
     * @param hacia la habitacion a la que entra.
     */
    public static void mostrarTransicion(int desde, int hacia) {
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        System.out.println(centrar("Escapaste de la habitacion " + desde
                + "... entras a la habitacion " + hacia, ANCHO_ESCENA));
        System.out.println();
        System.out.println(centrar("La puerta se cierra detras de ti, " + nombreJugador + ".", ANCHO_ESCENA));
        System.out.println();
        System.out.println(bordeEscena());
        System.out.print(centrar("[ ENTER para continuar ]", ANCHO_ESCENA));
        lector.nextLine();
    }

    /**
     * El primer mensaje bajo el mapa al entrar a una habitacion nueva.
     * En DIFICIL avisa que los controles quedaron invertidos.
     *
     * @return el texto para la variable mensaje.
     */
    public static String mensajeEntrada() {
        if (dificultad == 3) {
            return "Estas mareado y los controles se han invertido!";
        }
        return "Entras a la habitacion " + habitacionActual + ", " + nombreJugador + ". Explora.";
    }

    /**
     * PLACEHOLDER BONUS - HABITACIONES DE OTROS EQUIPOS (+10%).
     *
     * <p>Como todas las habitaciones del salon usan el mismo formato
     * (habitacionN.txt + zonasN.txt + objetosN.csv + su arte), es posible
     * armar un juego GIGANTE conectando habitaciones de varios equipos.
     * Solo queda indicado: si lo intentan, el plan es:</p>
     * <ol>
     *   <li>Acordar con otro equipo el intercambio de la carpeta de una
     *       habitacion (sus 3 archivos + los arte/ que use).</li>
     *   <li>Copiar los archivos con un numero nuevo (ej. habitacion4.txt) y
     *       sus artes a arte/ sin pisar los propios.</li>
     *   <li>Llamar este metodo desde siguienteHabitacion() cuando el jugador
     *       cruce la ultima puerta propia, y subir TOTAL_HABITACIONES.</li>
     *   <li>Verificar que los simbolos y claves del otro equipo no choquen
     *       con los suyos (cada CSV trae los suyos, asi que basta cargarlo).</li>
     * </ol>
     *
     * @param numero el numero con el que se copiaron los archivos del otro equipo.
     * @return true si la habitacion externa quedo cargada y lista para jugar.
     */
    public static boolean conectarHabitacionExterna(int numero) {
        // TODO equipo (bonus): cargar y jugar la habitacion de otro equipo.
        return false;
    }

    /* ==================== CARGA DEL MUNDO ==================== */

    /**
     * Carga todos los datos de una habitacion y sortea su puzzle.
     * Se usa para la habitacion 1 y debe reutilizarse para la 2 y la 3.
     *
     * @param numero el numero de la habitacion (sus archivos llevan el sufijo).
     * @return true si todos los archivos cargaron bien.
     */
    public static boolean cargarMundo(int numero) {
        visual = cargarMatriz("habitacion" + numero + ".txt");
        zonas = cargarMatriz("zonas" + numero + ".txt");
        objetos = cargarObjetos("objetos" + numero + ".csv");
        if (visual == null || zonas == null || objetos == null) {
            return false;
        }
        calcularAnchoMapa();
        buscarInicio();
        mirados = ""; // cada habitacion cuenta sus propios objetos mirados
        movimientosRestantes = MOVIMIENTOS_POR_HABITACION; // solo cuenta en CONTRARRELOJ

        // El numero del cofre: decena 1-9 y unidad par distinta de cero,
        // asi la MITAD es exacta y el numero AL REVES no empieza por cero.
        int decena = 1 + (int) (Math.random() * 9);
        int[] unidadesPares = {2, 4, 6, 8};
        int unidad = unidadesPares[(int) (Math.random() * unidadesPares.length)];
        numeroCofre = decena * 10 + unidad;

        // La operacion de esta habitacion y su clave.
        operacionElegida = (int) (Math.random() * operaciones.length);
        clave = calcularClave(operacionElegida, numeroCofre);

        // Los puzzles propios van DESPUES de la linea anterior: asi su clave
        // reemplaza la del cofre y no queda sobrescrita.
        if (numero == 2) {
            // Dos digitos del 1 al 9 (nunca 0, para que la clave tenga 3 cifras).
            pelotaCama = 1 + (int) (Math.random() * 9);
            pelotaChimenea = 1 + (int) (Math.random() * 9);
            // El grabado cuenta el orden: primero el fuego (chimenea), luego
            // dormir (cama) y al final "suspire DOS veces" (el 2).
            clave = "" + pelotaChimenea + pelotaCama + "2";
        }
        if (numero == 3) {
            // Un numero del 1 al 5; solo se pide en NORMAL y DIFICIL.
            numeroPuerta3 = 1 + (int) (Math.random() * 5);
            clave = "" + numeroPuerta3;
        }
        return true;
    }

    /** El ancho del mapa es el de la fila mas larga de la matriz visual. */
    public static void calcularAnchoMapa() {
        anchoMapa = 0;
        for (int i = 0; i < visual.length; i++) {
            if (visual[i].length > anchoMapa) {
                anchoMapa = visual[i].length;
            }
        }
    }

    /**
     * Encuentra la @ en la matriz visual: ahi arranca el jugador.
     * Se borra del dibujo porque el @ se superpone al dibujar.
     */
    public static void buscarInicio() {
        fila = 1;
        columna = 1; // posicion de emergencia si olvidan la @
        for (int i = 0; i < visual.length; i++) {
            for (int j = 0; j < visual[i].length; j++) {
                if (visual[i][j] == '@') {
                    fila = i;
                    columna = j;
                    visual[i][j] = ' ';
                }
            }
        }
    }

    /* ==================== PANTALLA 1: LA HABITACION ==================== */

    /**
     * La pantalla principal: la habitacion (matriz visual con el @ del
     * jugador superpuesto) a la izquierda y el panel a la derecha.
     *
     * @param panel las lineas del panel derecho (estado o preview).
     */
    public static void dibujarPantalla(String[] panel) {
        System.out.println();
        System.out.println(" +============ ESCAPE: LA HABITACION ============+");
        System.out.println();
        int total = Math.max(visual.length, panel.length);
        for (int i = 0; i < total; i++) {
            String izquierda = (i < visual.length) ? filaVisual(i) : repetir(' ', anchoMapa);
            String derecha = (i < panel.length) ? panel[i] : "";
            System.out.println(" " + izquierda + "   " + derecha);
        }
        System.out.println();
        System.out.println(" " + mensaje);
    }

    /** Una fila de la matriz visual como texto, con el jugador (@) encima. */
    public static String filaVisual(int i) {
        String linea = "";
        for (int j = 0; j < anchoMapa; j++) {
            if (i == fila && j == columna) {
                linea = linea + '@';
            } else if (j < visual[i].length) {
                linea = linea + visual[i][j];
            } else {
                linea = linea + ' '; // relleno si la linea del txt quedo corta
            }
        }
        return linea;
    }

    /** El panel cuando no hay objeto cerca: progreso, inventario y comandos. */
    public static String[] panelEstado() {
        int lineasInventario = Math.max(1, totalInventario);
        // 7 lineas fijas (progreso, mirados, jugador, dificultad, modo,
        // vacio, titulo del inventario) + las lineas que ocupe el inventario.
        String[] contenido = new String[7 + lineasInventario];
        contenido[0] = "Habitacion " + habitacionActual + " de " + TOTAL_HABITACIONES;
        contenido[1] = "Objetos mirados: " + mirados.length() + " de " + objetos.length;
        contenido[2] = "Jugador: " + nombreJugador; // enmarcar() recorta si es largo
        contenido[3] = "Dificultad: " + nombreDificultad(dificultad);
        // En CONTRARRELOJ lo importante es cuanto queda; en los otros, el modo.
        if (tipoJuego == 3) {
            contenido[4] = "Movimientos: " + movimientosRestantes;
        } else {
            contenido[4] = "Modo: " + nombreModo(tipoJuego);
        }
        contenido[5] = "";
        contenido[6] = "Inventario:";
        if (totalInventario == 0) {
            contenido[7] = "  (vacio)";
        } else {
            for (int i = 0; i < totalInventario; i++) {
                contenido[7 + i] = "  - " + inventario[i];
            }
        }

        String[] caja = enmarcar(contenido);
        String[] panel = new String[caja.length + 4];
        for (int i = 0; i < caja.length; i++) {
            panel[i] = caja[i];
        }
        panel[caja.length] = "";
        panel[caja.length + 1] = " Moverse: W A S D";
        panel[caja.length + 2] = " Pista: H | Rendirse: Q";
        panel[caja.length + 3] = " Musica: M | Efectos: E";
        return panel;
    }

    /**
     * Intenta mover al jugador. La matriz de ZONAS decide que pasa:
     * '.' piso (avanza), letra A-Z objeto (pregunta), otro solido (bloquea).
     *
     * @param comando W, A, S o D en mayuscula.
     * @return true solo si el jugador escapo y el juego termina.
     */
    public static boolean mover(String comando) {
        // En DIFICIL (solo habitaciones 2 y 3) la tecla se traduce ANTES de
        // procesarla: asi todo lo de abajo, incluido el mensaje "Caminas
        // hacia...", usa la direccion REAL en que se mueve el jugador.
        if (dificultad == 3 && habitacionActual >= 2) {
            comando = invertirControles(comando);
        }
        int nuevaFila = fila;
        int nuevaColumna = columna;
        if (comando.equals("W")) {
            nuevaFila--;        // arriba = una fila menos
        } else if (comando.equals("S")) {
            nuevaFila++;        // abajo = una fila mas
        } else if (comando.equals("D")) {
            nuevaColumna++;     // derecha = una columna mas
        } else if (comando.equals("A")) {
            nuevaColumna--;     // izquierda = una columna menos
        } else {
            mensaje = "No entiendo '" + comando + "'. Usa W A S D (H = pista, Q = rendirse).";
            return false;
        }

        // En CONTRARRELOJ cada tecla de movimiento gasta uno, incluso si
        // choca: asi el jugador debe pensar antes de caminar.
        if (tipoJuego == 3) {
            movimientosRestantes--;
        }

        char casilla = casillaZona(nuevaFila, nuevaColumna);
        if (casilla == '.') {
            fila = nuevaFila;
            columna = nuevaColumna;
            mensaje = "Caminas hacia " + nombreDireccion(comando) + ".";
        } else if (casilla >= 'A' && casilla <= 'Z') {
            return preguntarObjeto(casilla); // chocaste con un objeto
        } else {
            Sonido.golpe();
            mensaje = "Algo solido te bloquea el paso.";
        }
        return false;
    }

    /**
     * Invierte una tecla de movimiento (el jugador esta mareado):
     * W y S se intercambian, A y D tambien. Cualquier otra tecla queda
     * igual, para que mover() la rechace con su mensaje normal.
     *
     * @param comando la tecla que escribio el jugador, en mayuscula.
     * @return la tecla que realmente se va a procesar.
     */
    public static String invertirControles(String comando) {
        if (comando.equals("W")) return "S";
        if (comando.equals("S")) return "W";
        if (comando.equals("A")) return "D";
        if (comando.equals("D")) return "A";
        return comando;
    }

    /** La zona de una casilla; fuera de la matriz todo es solido. */
    public static char casillaZona(int f, int c) {
        if (f < 0 || f >= zonas.length) {
            return '#';
        }
        if (c < 0 || c >= zonas[f].length) {
            return '#';
        }
        return zonas[f][c];
    }

    /** El nombre en palabras de una tecla de movimiento. */
    public static String nombreDireccion(String comando) {
        if (comando.equals("W")) return "arriba";
        if (comando.equals("S")) return "abajo";
        if (comando.equals("D")) return "la derecha";
        return "la izquierda";
    }

    /* ==================== PANTALLA 2: LA PREGUNTA ==================== */

    /**
     * El jugador choco con un objeto: se redibuja la pantalla con el preview
     * en el panel y se pregunta que hacer. Aqui viven los comportamientos
     * especiales (la alfombra, el cofre y la nota son los moldes a imitar).
     *
     * @param simbolo la letra del objeto en la matriz de zonas.
     * @return true si el objeto era la puerta final y el jugador escapo.
     */
    public static boolean preguntarObjeto(char simbolo) {
        String[] objeto = buscarObjeto(simbolo);
        if (objeto == null) {
            mensaje = "Hay un objeto sin registrar ('" + simbolo + "'). Revisen el CSV.";
            return false;
        }

        dibujarPantalla(panelObjeto(objeto));
        int opcion = leerOpcion();
        if (opcion == 2) {
            mensaje = "Decides seguir explorando.";
            return false;
        }

        // COMPORTAMIENTOS ESPECIALES por objeto van aqui.
        // Cada condicion revisa TAMBIEN la habitacion: las letras se pueden
        // repetir entre habitaciones y asi un comportamiento nunca se activa
        // en el cuarto equivocado.

        // ---- Habitacion 2: cama, mesa y chimenea ----
        if (habitacionActual == 2 && simbolo == 'B') {
            mirarCama(objeto);
            return false;
        }
        if (habitacionActual == 2 && simbolo == 'M') {
            mirarMesa(objeto);
            return false;
        }
        if (habitacionActual == 2 && simbolo == 'H') {
            mirarChimenea(objeto);
            return false;
        }

        // ---- Habitacion 3: planta, armario y puerta final ----
        // (el reloj 'R' no necesita codigo: usa la escena normal de abajo)
        if (habitacionActual == 3 && simbolo == 'L') {
            mirarPlanta(objeto);
            return false;
        }
        if (habitacionActual == 3 && simbolo == 'W') {
            mirarArmario(objeto);
            return false;
        }
        if (habitacionActual == 3 && simbolo == 'P') {
            return abrirPuertaFinal(objeto);
        }

        // La alfombra entrega la llave al inventario la primera vez.
        if (habitacionActual == 1 && simbolo == 'A') {
            abrirEscena(objeto);
            recordarMirado(simbolo);
            if (!tieneObjeto("llave oxidada")) {
                guardarObjeto("llave oxidada");
                Sonido.recoger();
                mensaje = "Guardaste la LLAVE OXIDADA en tu inventario.";
            } else {
                mensaje = "Bajo la alfombra ya solo queda polvo.";
            }
            return false;
        }

        // El cofre exige la llave: sin ella no revela su numero.
        if (habitacionActual == 1 && simbolo == 'C') {
            if (!tieneObjeto("llave oxidada")) {
                abrirEscena(objeto); // el cofre cerrado (arte y detalle del CSV)
                Sonido.error();
                mensaje = "El candado del cofre no cede. Algo debe abrirlo...";
            } else {
                Sonido.exito();
                mostrarEscena("el cofre abierto", "cofre_abierto",
                        "Usas la llave oxidada y el candado cae. CLIC. Grabado en la tapa esta el numero "
                        + numeroCofre + ".");
                esperarEnter();
                recordarMirado(simbolo);
                mensaje = "El cofre revelo su numero: " + numeroCofre + ".";
            }
            return false;
        }

        // La nota revela la operacion sorteada en esta habitacion.
        if (habitacionActual == 1 && simbolo == 'N') {
            mostrarEscena(objeto[1], objeto[2],
                    objeto[3] + " Hoy la operacion es: " + operaciones[operacionElegida] + ".");
            esperarEnter();
            recordarMirado(simbolo);
            mensaje = "Miraste " + objeto[1] + ".";
            return false;
        }

        if (simbolo == 'P') {
            return abrirPuerta(objeto);
        }
        abrirEscena(objeto);
        recordarMirado(simbolo);
        mensaje = "Miraste " + objeto[1] + ". Sigues frente al objeto.";
        return false;
    }

    /* ---------- Habitacion 2: los objetos especiales ---------- */

    /**
     * LA CAMA (molde: la alfombra). Bajo la almohada hay una pelota con un
     * numero: la primera vez se guarda en el inventario; despues la escena
     * solo le recuerda el numero al jugador.
     *
     * @param objeto la fila del CSV de la cama.
     */
    public static void mirarCama(String[] objeto) {
        // Texto dinamico (como la nota): el detalle del CSV + el numero sorteado.
        mostrarEscena(objeto[1], objeto[2],
                objeto[3] + " La pelota tiene el numero " + pelotaCama + ".");
        esperarEnter();
        recordarMirado('B');
        if (!tieneObjeto("pelota de la cama")) {
            guardarObjeto("pelota de la cama");
            Sonido.recoger();
            mensaje = "Guardaste la PELOTA DE LA CAMA. Tiene el numero " + pelotaCama + ".";
        } else {
            // Ya la tiene: no se guarda dos veces, solo se recuerda el numero.
            mensaje = "La pelota de la cama tiene el numero " + pelotaCama + ".";
        }
    }

    /**
     * LA MESA (molde: la alfombra). Entrega el balde con agua la primera vez;
     * ese balde es lo que permite acercarse a la chimenea.
     *
     * @param objeto la fila del CSV de la mesa.
     */
    public static void mirarMesa(String[] objeto) {
        abrirEscena(objeto);
        recordarMirado('M');
        if (!tieneObjeto("balde con agua")) {
            guardarObjeto("balde con agua");
            Sonido.recoger();
            mensaje = "Guardaste el BALDE CON AGUA en tu inventario.";
        } else {
            mensaje = "Sobre la mesa ya no queda nada.";
        }
    }

    /**
     * LA CHIMENEA (molde: el cofre). Exige el balde con agua: sin el, el
     * fuego no deja acercarse; con el, se apaga el fuego y aparece la
     * segunda pelota con su numero.
     *
     * @param objeto la fila del CSV de la chimenea.
     */
    public static void mirarChimenea(String[] objeto) {
        // Primero el caso "ya resuelto", para no repetir el premio.
        if (tieneObjeto("pelota de la chimenea")) {
            mostrarEscena(objeto[1], objeto[2], "El fuego ya esta apagado. Entre las cenizas"
                    + " encontraste la pelota con el numero " + pelotaChimenea + ".");
            esperarEnter();
            mensaje = "La pelota de la chimenea tiene el numero " + pelotaChimenea + ".";
            return;
        }
        if (!tieneObjeto("balde con agua")) {
            abrirEscena(objeto); // la chimenea encendida (arte y detalle del CSV)
            Sonido.error();
            mensaje = "El fuego no te deja acercarte. Necesitas algo para apagarlo...";
            return;
        }
        // Tiene el balde: se apaga el fuego y se entrega la pelota.
        Sonido.exito();
        mostrarEscena("la chimenea apagada", objeto[2], "Lanzas el agua del balde y el fuego"
                + " se apaga con un siseo. Entre las cenizas encuentras una pelota con el numero "
                + pelotaChimenea + ".");
        esperarEnter();
        recordarMirado('H');
        guardarObjeto("pelota de la chimenea");
        mensaje = "Guardaste la PELOTA DE LA CHIMENEA. Tiene el numero " + pelotaChimenea + ".";
    }

    /* ---------- Habitacion 3: los objetos especiales ---------- */

    /**
     * LA PLANTA (molde: la alfombra). Entre sus hojas esta la llave pequena;
     * se guarda la primera vez. El reloj apunta hacia ella.
     *
     * @param objeto la fila del CSV de la planta.
     */
    public static void mirarPlanta(String[] objeto) {
        abrirEscena(objeto);
        recordarMirado('L');
        if (!tieneObjeto("llave pequena")) {
            guardarObjeto("llave pequena");
            Sonido.recoger();
            mensaje = "Guardaste la LLAVE PEQUENA en tu inventario.";
        } else {
            mensaje = "Entre las hojas ya no hay nada.";
        }
    }

    /**
     * EL ARMARIO (molde: el cofre). Exige la llave pequena; al abrirlo
     * entrega la llave grande que necesita la puerta final.
     *
     * @param objeto la fila del CSV del armario.
     */
    public static void mirarArmario(String[] objeto) {
        // Primero el caso "ya resuelto", para no entregar la llave dos veces.
        if (tieneObjeto("llave grande")) {
            mostrarEscena(objeto[1], objeto[2], "El armario esta vacio.");
            esperarEnter();
            mensaje = "El armario esta vacio.";
            return;
        }
        if (!tieneObjeto("llave pequena")) {
            abrirEscena(objeto); // el armario cerrado (arte y detalle del CSV)
            Sonido.error();
            mensaje = "El armario esta cerrado con llave. Una llave pequena lo abriria...";
            return;
        }
        // Tiene la llave pequena: se abre y se entrega la llave grande.
        Sonido.exito();
        mostrarEscena("el armario abierto", objeto[2], "Usas la llave pequena y las puertas"
                + " del armario se abren con un crujido. Dentro cuelga una LLAVE GRANDE.");
        esperarEnter();
        recordarMirado('W');
        guardarObjeto("llave grande");
        mensaje = "Guardaste la LLAVE GRANDE en tu inventario.";
    }

    /**
     * LA PUERTA FINAL (habitacion 3). Sin la llave grande ni siquiera pide
     * clave. Con la llave: en FACIL se abre de una vez (victoria); en NORMAL y
     * DIFICIL ademas pide el numero secreto con la logica de abrirPuerta().
     *
     * @param objeto la fila del CSV de la puerta final.
     * @return true si el jugador escapo y el juego termina.
     */
    public static boolean abrirPuertaFinal(String[] objeto) {
        if (!tieneObjeto("llave grande")) {
            abrirEscena(objeto);
            recordarMirado('P');
            Sonido.error();
            mensaje = "La cerradura es enorme. Necesitas una LLAVE GRANDE para abrirla.";
            return false;
        }
        if (dificultad == 1) {
            recordarMirado('P');
            Sonido.exito();
            mostrarVictoria();
            return true;
        }
        // NORMAL y DIFICIL: la llave no basta, la clave es numeroPuerta3 (1 a 5).
        return abrirPuerta(objeto);
    }

    /**
     * El texto que se muestra bajo el dibujo de la puerta (molde: la nota).
     * En la habitacion 2 agrega el grabado con la pista, que depende de la
     * dificultad; en la 3 avisa que, ademas de la llave, se pide un numero;
     * en la habitacion 1 devuelve el detalle del CSV tal cual.
     *
     * @param objeto la fila del CSV de la puerta.
     * @return el texto de la escena de la puerta.
     */
    public static String textoPuerta(String[] objeto) {
        if (habitacionActual == 3) {
            // Solo se llega aqui con la llave grande en NORMAL o DIFICIL:
            // se dice el rango, pero no el numero (no es una pista).
            return objeto[3] + " La llave grande gira... pero un teclado pide un numero del 1 al 5.";
        }
        if (habitacionActual != 2) {
            return objeto[3]; // la puerta 1 queda exactamente igual
        }
        if (dificultad == 1) {
            return objeto[3] + " Grabado en la puerta: \"Antes de ir a dormir encendi el fuego,"
                    + " al final suspire DOS veces\"";
        }
        // NORMAL y DIFICIL: el orden de los numeros queda oculto; solo el final se lee.
        return objeto[3] + " Grabado en la puerta: \"#$=/!#&%$ mensaje corrupto $#&%$&..."
                + " al final suspire DOS veces\"";
    }

    /** El panel con el preview (mini arte) del objeto y la pregunta. */
    public static String[] panelObjeto(String[] objeto) {
        String[] mini = leerLineas("arte/mini_" + objeto[2] + ".txt");
        if (mini == null) {
            mini = new String[] { "( sin dibujo )" };
        }

        String[] contenido = new String[mini.length + 2];
        for (int i = 0; i < mini.length; i++) {
            contenido[i] = mini[i];
        }
        contenido[mini.length] = "";
        contenido[mini.length + 1] = centrar(objeto[1].toUpperCase(), ANCHO_PANEL - 2);

        String[] caja = enmarcar(contenido);
        String[] panel = new String[caja.length + 4];
        for (int i = 0; i < caja.length; i++) {
            panel[i] = caja[i];
        }
        panel[caja.length] = "";
        panel[caja.length + 1] = " Que haces?";
        panel[caja.length + 2] = "   1. Mirar de cerca";
        panel[caja.length + 3] = "   2. Seguir explorando";
        return panel;
    }

    /** Busca la fila del CSV cuyo simbolo coincide, o null si no existe. */
    public static String[] buscarObjeto(char simbolo) {
        for (int i = 0; i < objetos.length; i++) {
            if (objetos[i][0].charAt(0) == simbolo) {
                return objetos[i];
            }
        }
        return null;
    }

    /* ==================== PANTALLA 3: LA ESCENA ==================== */

    /** Abre la escena de un objeto del CSV y espera el ENTER. */
    public static void abrirEscena(String[] objeto) {
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        esperarEnter();
    }

    /** Pausa la escena hasta que el jugador presione ENTER. */
    public static void esperarEnter() {
        System.out.println(centrar("[ ENTER para volver a la habitacion ]", ANCHO_ESCENA));
        System.out.println(bordeEscena());
        lector.nextLine();
    }

    /**
     * Imprime una escena generica: titulo, arte grande y texto.
     * Los comportamientos especiales la reutilizan con sus propios textos.
     *
     * @param titulo  el nombre que encabeza la escena.
     * @param archivo el nombre base del arte (arte/archivo.txt).
     * @param texto   el detalle, partido automaticamente en lineas.
     */
    public static void mostrarEscena(String titulo, String archivo, String texto) {
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        System.out.println(centrar("~ " + titulo.toUpperCase() + " ~", ANCHO_ESCENA));
        System.out.println();
        imprimirArte("arte/" + archivo + ".txt");
        System.out.println();
        String[] lineas = partirTexto(texto, 48);
        for (int i = 0; i < lineas.length; i++) {
            System.out.println(centrar(lineas[i], ANCHO_ESCENA));
        }
        System.out.println();
    }

    /**
     * LA PUERTA pide la clave del puzzle. Si es la ultima habitacion el
     * jugador gana; si no, el viaje continua en siguienteHabitacion().
     *
     * @param objeto la fila del CSV de la puerta.
     * @return true si el juego termina.
     */
    public static boolean abrirPuerta(String[] objeto) {
        // textoPuerta() agrega el grabado en la habitacion 2; en la 1 es objeto[3].
        mostrarEscena(objeto[1], objeto[2], textoPuerta(objeto));
        recordarMirado('P');
        System.out.print(centrar("Clave (solo ENTER para alejarte): ", ANCHO_ESCENA));
        String intento = lector.nextLine().trim();
        if (intento.equals(clave)) {
            Sonido.exito();
            // En HABITACION UNICA cualquier puerta es la ultima.
            if (habitacionActual >= TOTAL_HABITACIONES || tipoJuego == 2) {
                mostrarVictoria();
                return true;
            }
            return siguienteHabitacion(); // PLACEHOLDER 4: el viaje continua
        }
        if (intento.isEmpty()) {
            mensaje = "Te alejas de la puerta sin intentar nada.";
        } else {
            Sonido.error();
            mensaje = "CLAVE INCORRECTA. El teclado parpadea en silencio.";
            // TODO equipo: en dificultad DIFICIL limitar los intentos.
        }
        return false;
    }

    /** La pantalla final del juego, con fanfarria y pausa para disfrutarla. */
    public static void mostrarVictoria() {
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        imprimirArte("arte/victoria.txt");
        System.out.println();
        if (tipoJuego == 2) {
            System.out.println(centrar("Escapaste de la habitacion " + habitacionActual
                    + ". Eres libre, " + nombreJugador + ".", ANCHO_ESCENA));
        } else {
            System.out.println(centrar("Escapaste de las " + TOTAL_HABITACIONES
                    + " habitaciones. Eres libre, " + nombreJugador + ".", ANCHO_ESCENA));
        }
        if (tipoJuego == 3) {
            System.out.println(centrar("Te sobraron " + movimientosRestantes
                    + " movimientos en la ultima habitacion.", ANCHO_ESCENA));
        }
        System.out.println();
        System.out.println(bordeEscena());
        Sonido.detenerMusica();
        Sonido.victoria();
        System.out.println(centrar("[ ENTER para salir ]", ANCHO_ESCENA));
        lector.nextLine();
        // TODO equipo: resumen final (movimientos gastados, objetos mirados).
    }

    /* ==================== PUZZLE, PISTAS Y SECRETO ==================== */

    /**
     * Aplica la operacion sorteada al numero del cofre.
     *
     * @param operacion indice en el arreglo operaciones (0 a 4).
     * @param numero    el numero grabado en el cofre.
     * @return la clave resultante, como texto.
     */
    public static String calcularClave(int operacion, int numero) {
        if (operacion == 0) {
            return "" + (numero * 2);
        }
        if (operacion == 1) {
            return "" + (numero / 2);
        }
        if (operacion == 2) {
            return "" + (numero + 10);
        }
        if (operacion == 3) {
            return "" + (numero - 10);
        }
        // operacion 4: el numero escrito al reves (como cadena)
        String texto = "" + numero;
        String alReves = "";
        for (int i = texto.length() - 1; i >= 0; i--) {
            alReves = alReves + texto.charAt(i);
        }
        return alReves;
    }

    /**
     * La pista que toca segun el estado del juego (tecla H). Cada habitacion
     * tiene sus propias pistas: aqui solo se decide cual metodo responde.
     *
     * @return el texto de la pista para la variable mensaje.
     */
    public static String darPista() {
        if (habitacionActual == 2) {
            return pistaHabitacion2();
        }
        if (habitacionActual == 3) {
            return pistaHabitacion3();
        }
        return pistaHabitacion1();
    }

    /**
     * Las pistas escalonadas de la habitacion 2: se revisa el inventario en
     * el orden del puzzle y se responde el primer paso que falta.
     *
     * @return el texto de la pista.
     */
    public static String pistaHabitacion2() {
        if (mirados.length() == 0) {
            return "PISTA: parece un dormitorio. Mira cada mueble de cerca.";
        }
        if (!tieneObjeto("pelota de la cama")) {
            return "PISTA: alguien durmio aqui hace poco. Revisa bien la cama.";
        }
        if (!tieneObjeto("balde con agua")) {
            return "PISTA: la chimenea quema demasiado. Busca algo con que apagarla.";
        }
        if (!tieneObjeto("pelota de la chimenea")) {
            return "PISTA: ya tienes agua. Usala en lo que arde.";
        }
        if (mirados.indexOf('P') < 0) {
            return "PISTA: tienes dos numeros. Lee lo que esta grabado en la puerta.";
        }
        // La ultima pista nunca dice el orden de las pelotas: eso lo descubre el jugador.
        if (dificultad == 1) {
            return "PISTA: el grabado cuenta una historia. Sigue sus pasos en orden.";
        }
        return "PISTA: el grabado esta corrupto, pero el final se lee claro. Piensa en el orden.";
    }

    /**
     * Las pistas escalonadas de la habitacion 3: reloj, planta, armario y
     * puerta, en ese orden.
     *
     * @return el texto de la pista.
     */
    public static String pistaHabitacion3() {
        if (mirados.indexOf('R') < 0 && !tieneObjeto("llave pequena")) {
            return "PISTA: el tiempo importa aqui. Mira el reloj de cerca.";
        }
        if (!tieneObjeto("llave pequena")) {
            return "PISTA: a las 6 las agujas apuntan hacia ABAJO. Que hay debajo del reloj?";
        }
        if (!tieneObjeto("llave grande")) {
            return "PISTA: una llave pequena abre algo grande donde se guardan cosas.";
        }
        if (dificultad == 1) {
            return "PISTA: con la llave grande ya puedes abrir la puerta final.";
        }
        return "PISTA: la puerta pide un numero del 1 al 5. Fallar no tiene castigo.";
    }

    /**
     * Las pistas originales de la habitacion 1 (sin cambios): ahora solo se
     * usan cuando el jugador esta en la habitacion 1.
     *
     * @return el texto de la pista.
     */
    public static String pistaHabitacion1() {
        if (mirados.length() == 0) {
            return "PISTA: los objetos guardan secretos. Camina hacia uno y miralo de cerca.";
        }
        if (!tieneObjeto("llave oxidada")) {
            return "PISTA: en esta habitacion hay algo escondido DEBAJO de algo.";
        }
        if (mirados.indexOf('C') < 0) {
            return "PISTA: la llave que cargas abre algo... algo que guarda un numero.";
        }
        if (mirados.indexOf('N') < 0) {
            return "PISTA: ya tienes el numero. La nota dice que OPERACION aplicarle.";
        }
        return "PISTA: aplica la operacion de la nota al numero del cofre. El resultado abre la puerta.";
    }

    /**
     * EL EASTER EGG (tecla secreta X): el gancho ya esta conectado.
     * TODO equipo: programen aqui su secreto. Ideas: el modo desarrollador
     * que imprime la matriz de zonas, un objeto oculto, un mensaje del
     * creador del cuarto.
     */
    public static void easterEgg() {
        mensaje = "Presionaste algo extrano... pero nada ocurre. (TODO equipo)";
    }

    /* ==================== ESTADO DEL JUGADOR ==================== */

    /** Guarda algo en el inventario si hay espacio y no esta repetido. */
    public static void guardarObjeto(String nombre) {
        if (totalInventario < inventario.length && !tieneObjeto(nombre)) {
            inventario[totalInventario] = nombre;
            totalInventario++;
        }
    }

    /**
     * Recorre el inventario buscando un objeto por su nombre.
     *
     * @param nombre el nombre exacto guardado.
     * @return true si el jugador lo tiene.
     */
    public static boolean tieneObjeto(String nombre) {
        for (int i = 0; i < totalInventario; i++) {
            if (inventario[i].equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    /** Recuerda los objetos ya mirados (sin repetir) en una cadena. */
    public static void recordarMirado(char simbolo) {
        if (mirados.indexOf(simbolo) < 0) {
            mirados = mirados + simbolo;
        }
    }

    /* ==================== UTILIDADES DE TEXTO ==================== */

    /** El borde superior e inferior de las escenas. */
    public static String bordeEscena() {
        return "+" + repetir('=', ANCHO_ESCENA - 2) + "+";
    }

    /** Imprime un archivo de arte centrado como bloque (sin deformarlo). */
    public static void imprimirArte(String ruta) {
        String[] arte = leerLineas(ruta);
        if (arte == null) {
            System.out.println(centrar("( falta el dibujo: " + ruta + " )", ANCHO_ESCENA));
            return;
        }
        int masAncha = 0;
        for (int i = 0; i < arte.length; i++) {
            if (arte[i].length() > masAncha) {
                masAncha = arte[i].length();
            }
        }
        String margen = repetir(' ', (ANCHO_ESCENA - masAncha) / 2);
        for (int i = 0; i < arte.length; i++) {
            System.out.println(margen + arte[i]);
        }
    }

    /** Mete lineas de texto en una caja de ancho fijo. */
    public static String[] enmarcar(String[] lineas) {
        String borde = "+" + repetir('-', ANCHO_PANEL) + "+";
        String[] caja = new String[lineas.length + 2];
        caja[0] = borde;
        for (int i = 0; i < lineas.length; i++) {
            String linea = " " + lineas[i];
            while (linea.length() < ANCHO_PANEL) {
                linea = linea + " ";
            }
            if (linea.length() > ANCHO_PANEL) {
                linea = linea.substring(0, ANCHO_PANEL);
            }
            caja[i + 1] = "|" + linea + "|";
        }
        caja[lineas.length + 1] = borde;
        return caja;
    }

    /** Agrega espacios a la izquierda para centrar un texto en 'ancho'. */
    public static String centrar(String texto, int ancho) {
        int sobrante = ancho - texto.length();
        if (sobrante <= 0) {
            return texto;
        }
        return repetir(' ', sobrante / 2) + texto;
    }

    /** Construye una cadena repitiendo un caracter 'veces' veces. */
    public static String repetir(char c, int veces) {
        String resultado = "";
        for (int i = 0; i < veces; i++) {
            resultado = resultado + c;
        }
        return resultado;
    }

    /**
     * Parte un texto largo en lineas de maximo 'ancho' caracteres,
     * cortando por espacios para no romper las palabras.
     */
    public static String[] partirTexto(String texto, int ancho) {
        String[] palabras = texto.split(" ");
        String[] temporal = new String[palabras.length];
        int total = 0;
        String linea = "";
        for (int i = 0; i < palabras.length; i++) {
            if (linea.isEmpty()) {
                linea = palabras[i];
            } else if (linea.length() + 1 + palabras[i].length() <= ancho) {
                linea = linea + " " + palabras[i];
            } else {
                temporal[total] = linea;
                total++;
                linea = palabras[i];
            }
        }
        temporal[total] = linea;
        total++;

        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) {
            lineas[i] = temporal[i];
        }
        return lineas;
    }

    /* ==================== ENTRADA Y ARCHIVOS ==================== */

    /** Lee 1 o 2 validando la entrada. Nunca rompe el programa. */
    public static int leerOpcion() {
        int opcion = -1;
        do {
            System.out.print(" > Elige (1 o 2): ");
            String entrada = lector.nextLine().trim();
            if (entrada.equals("1") || entrada.equals("2")) {
                opcion = Integer.parseInt(entrada);
            } else {
                System.out.println(" Entrada invalida. Intenta de nuevo.");
            }
        } while (opcion == -1);
        return opcion;
    }

    /** Lee un archivo de texto completo y retorna sus lineas, o null. */
    public static String[] leerLineas(String ruta) {
        String[] temporal = new String[200];
        int total = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = br.readLine()) != null && total < 200) {
                temporal[total] = linea;
                total++;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return null;
        }
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) {
            lineas[i] = temporal[i];
        }
        return lineas;
    }

    /** Convierte un archivo de texto en una matriz de caracteres. */
    public static char[][] cargarMatriz(String ruta) {
        String[] lineas = leerLineas(ruta);
        if (lineas == null) {
            return null;
        }
        char[][] matriz = new char[lineas.length][];
        for (int i = 0; i < lineas.length; i++) {
            matriz[i] = lineas[i].toCharArray();
        }
        return matriz;
    }

    /** Lee un CSV de objetos (saltando el encabezado) a una matriz de String. */
    public static String[][] cargarObjetos(String ruta) {
        String[] lineas = leerLineas(ruta);
        if (lineas == null || lineas.length < 2) {
            System.out.println(ruta + " esta vacio o no existe.");
            return null;
        }
        String[][] tabla = new String[lineas.length - 1][];
        for (int i = 1; i < lineas.length; i++) {
            tabla[i - 1] = lineas[i].split(",");
        }
        return tabla;
    }
}
