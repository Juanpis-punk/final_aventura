import java.io.File;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

/**
 * SONIDO - La caja de musica del juego (clase del profesor).
 *
 * <p>Todo suena con MIDI, que viene incluido en el JDK. Dos maneras:</p>
 * <ol>
 *   <li><b>EFECTOS programados</b>: una melodia es un ARREGLO de numeros.
 *       Nota 60 = Do central; 62 = Re; 64 = Mi; 65 = Fa; 67 = Sol; 69 = La;
 *       71 = Si; 72 = Do agudo. Sumar 12 sube una octava. Crear un efecto
 *       nuevo = copiar un metodo y cambiar los numeros.</li>
 *   <li><b>MUSICA de fondo</b>: un archivo .mid en la carpeta musica/.
 *       Pueden DESCARGAR un .mid de internet o GENERARLO con una IA y
 *       ponerlo ahi: el secuenciador lo toca en bucle por su cuenta, sin
 *       detener el juego.</li>
 * </ol>
 *
 * <p>Si el computador no tiene audio, todo falla EN SILENCIO y el juego sigue
 * normal: por eso cada metodo valida y usa try-catch.</p>
 */
public class Sonido {

    /** El sintetizador MIDI (los efectos suenan por aqui). */
    public static Synthesizer sintetizador = null;

    /** El canal 0 del sintetizador, listo para tocar notas. */
    public static MidiChannel canal = null;

    /** El secuenciador que reproduce los archivos .mid por su cuenta. */
    public static Sequencer secuenciador = null;

    /** Configuracion del jugador: musica de fondo activa (tecla M). */
    public static boolean musicaActiva = true;

    /** Configuracion del jugador: efectos activos (tecla E). */
    public static boolean efectosActivos = true;

    /** La ultima musica pedida, para reanudarla al reactivar la tecla M. */
    public static String rutaMusica = "";

    /**
     * Abre el sintetizador una sola vez, al iniciar el juego.
     * Si el equipo no tiene audio, el canal queda en null y el juego
     * continua sin sonido.
     */
    public static void iniciar() {
        try {
            sintetizador = MidiSystem.getSynthesizer();
            sintetizador.open();
            canal = sintetizador.getChannels()[0];
        } catch (Exception e) {
            canal = null; // sin audio: el juego sigue, mudo
        }
    }

    /* ============ EFECTOS: melodias como arreglos ============ */

    /** Arpegio ascendente: abrir el cofre, logros. */
    public static void exito()    { tocar(new int[] {60, 64, 67, 72}, 110); }

    /** "Pling" corto: guardar algo en el inventario. */
    public static void recoger()  { tocar(new int[] {72, 76}, 90); }

    /** Dos notas graves: clave incorrecta, candado cerrado. */
    public static void error()    { tocar(new int[] {45, 40}, 160); }

    /** Nota seca y grave: chocar con un muro. */
    public static void golpe()    { tocar(new int[] {36}, 120); }

    /** La fanfarria del final del juego. */
    public static void victoria() { tocar(new int[] {60, 64, 67, 72, 71, 72}, 150); }
    // TODO equipo: agreguen aqui sus propios efectos.

    /**
     * Toca las notas del arreglo, una tras otra, si hay audio y los
     * efectos estan activados.
     *
     * @param notas      las notas MIDI (60 = Do central; +12 por octava).
     * @param duracionMs cuanto dura cada nota, en milisegundos.
     */
    public static void tocar(int[] notas, int duracionMs) {
        if (canal == null || !efectosActivos) {
            return;
        }
        try {
            for (int i = 0; i < notas.length; i++) {
                canal.noteOn(notas[i], 85);
                Thread.sleep(duracionMs);
                canal.noteOff(notas[i]);
            }
        } catch (InterruptedException e) {
            // un efecto interrumpido no es un problema
        }
    }

    /* ============ MUSICA DE FONDO: archivos .mid ============ */

    /**
     * Reproduce un archivo .mid en bucle sin detener el juego (el
     * secuenciador toca por su cuenta). Si el archivo no existe o esta
     * danado, simplemente no hay musica.
     *
     * @param ruta el archivo MIDI, por ejemplo "musica/ambiente.mid".
     */
    public static void musicaFondo(String ruta) {
        rutaMusica = ruta; // se recuerda por si el jugador apaga y prende
        File archivo = new File(ruta);
        if (!archivo.exists() || !musicaActiva) {
            return;
        }
        try {
            Sequence cancion = MidiSystem.getSequence(archivo);
            secuenciador = MidiSystem.getSequencer();
            secuenciador.open();
            secuenciador.setSequence(cancion);
            secuenciador.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            secuenciador.start();
        } catch (Exception e) {
            secuenciador = null;
        }
    }

    /** Detiene la musica de fondo (se puede reanudar con alternarMusica). */
    public static void detenerMusica() {
        if (secuenciador != null) {
            secuenciador.stop();
        }
    }

    /* ============ CONFIGURACION (teclas M y E) ============ */

    /**
     * Prende o apaga la musica de fondo (tecla M). Si estaba sonando se
     * pausa; al prenderla de nuevo continua donde iba (o arranca desde
     * cero si nunca habia sonado).
     */
    public static void alternarMusica() {
        musicaActiva = !musicaActiva;
        if (!musicaActiva) {
            detenerMusica();
        } else if (secuenciador != null) {
            secuenciador.start(); // reanuda donde iba
        } else {
            musicaFondo(rutaMusica); // nunca habia arrancado
        }
    }

    /** Prende o apaga los efectos (tecla E); tocar() revisa la bandera. */
    public static void alternarEfectos() {
        efectosActivos = !efectosActivos;
    }

    /**
     * Cierra el sintetizador y el secuenciador al salir del juego.
     * Sin esto el programa no termina: los dispositivos MIDI abiertos
     * mantienen vivo el proceso.
     */
    public static void apagar() {
        detenerMusica();
        if (secuenciador != null) {
            secuenciador.close();
        }
        if (sintetizador != null) {
            sintetizador.close();
        }
    }
}
