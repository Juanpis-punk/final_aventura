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
     * El ciclo de vida del juego: configuracion inicial (los placeholders),
     * carga de la habitacion 1 y el ciclo principal de comandos.
     */
    public static void main(String[] args) {
        Sonido.iniciar();

        // ---- Configuracion inicial: aqui viven 3 de los placeholders ----
        configurarArte();
        tipoJuego = seleccionarTipoJuego();
        configurarJugador();

        if (!cargarMundo(1)) {
            return; // sin datos no hay juego
        }
        Sonido.musicaFondo("musica/ambiente.mid");

        boolean escapo = false;
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
                }
            }
        }

        if (!escapo) {
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
     * @return el modo elegido por el jugador (por ahora siempre 1).
     */
    public static int seleccionarTipoJuego() {
        // TODO equipo: menu de modos de juego + lectura validada.
        System.out.println(" Modo de juego: HISTORIA (TODO equipo: el menu de modos)");
        return 1;
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
        // TODO equipo: pedir nombre, elegir dificultad y aplicarla.
        System.out.println(" Jugador: " + nombreJugador + " | Dificultad: " + dificultad
                + " (TODO equipo: la terminal inicial)");
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
        // TODO equipo: habitacionActual++, cargarMundo(...), transicion y return false.
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        System.out.println(centrar("Escapaste de la habitacion " + habitacionActual + "...", ANCHO_ESCENA));
        System.out.println(centrar("pero la puerta da a un pasillo EN CONSTRUCCION.", ANCHO_ESCENA));
        System.out.println();
        System.out.println(centrar("Equipo: las habitaciones " + (habitacionActual + 1)
                + " y " + TOTAL_HABITACIONES + " son SU mision.", ANCHO_ESCENA));
        System.out.println();
        System.out.println(bordeEscena());
        System.out.print(centrar("[ ENTER para salir ]", ANCHO_ESCENA));
        lector.nextLine();
        return true; // provisional: el juego termina en la habitacion 1
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

        // El numero del cofre: decena 1-9 y unidad par distinta de cero,
        // asi la MITAD es exacta y el numero AL REVES no empieza por cero.
        int decena = 1 + (int) (Math.random() * 9);
        int[] unidadesPares = {2, 4, 6, 8};
        int unidad = unidadesPares[(int) (Math.random() * unidadesPares.length)];
        numeroCofre = decena * 10 + unidad;

        // La operacion de esta habitacion y su clave.
        operacionElegida = (int) (Math.random() * operaciones.length);
        clave = calcularClave(operacionElegida, numeroCofre);
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
        String[] contenido = new String[4 + lineasInventario];
        contenido[0] = "Habitacion " + habitacionActual + " de " + TOTAL_HABITACIONES;
        contenido[1] = "Objetos mirados: " + mirados.length() + " de " + objetos.length;
        contenido[2] = "";
        contenido[3] = "Inventario:";
        if (totalInventario == 0) {
            contenido[4] = "  (vacio)";
        } else {
            for (int i = 0; i < totalInventario; i++) {
                contenido[4 + i] = "  - " + inventario[i];
            }
        }
        // TODO equipo: mostrar aqui el nombre del jugador y la dificultad.

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
        // TODO equipo: agreguen los else-if de SUS objetos especiales
        //              (en las habitaciones 2 y 3 necesitaran varios).

        // La alfombra entrega la llave al inventario la primera vez.
        if (simbolo == 'A') {
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
        if (simbolo == 'C') {
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
        if (simbolo == 'N') {
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
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        recordarMirado('P');
        System.out.print(centrar("Clave (solo ENTER para alejarte): ", ANCHO_ESCENA));
        String intento = lector.nextLine().trim();
        if (intento.equals(clave)) {
            Sonido.exito();
            if (habitacionActual >= TOTAL_HABITACIONES) {
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
        System.out.println(centrar("Escapaste de las " + TOTAL_HABITACIONES
                + " habitaciones. Eres libre, " + nombreJugador + ".", ANCHO_ESCENA));
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
     * La pista que toca segun el estado del juego (tecla H).
     * TODO equipo: pistas propias para las habitaciones 2 y 3, y limite
     * de pistas segun la dificultad.
     */
    public static String darPista() {
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
