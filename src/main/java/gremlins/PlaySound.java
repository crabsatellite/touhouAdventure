package gremlins;

import processing.core.PApplet;

import javax.sound.sampled.*;
import java.io.File;

/**
 * =============== Play sound method. Volume 0 ~ 2 =====================//
 */
public class PlaySound extends PApplet{
    public static void playBGM() {
        new Thread(() -> {
            while (true) {
                playSound(2, "src/main/resources/sounds/BGM.wav");
            }
        }).start();
    }

    public static void playFireballShootSound() {
        new Thread(() -> playSound(2, "src/main/resources/sounds/fireball_shoot.wav")).start();
    }

    public static void playPowerUpSound() {
        new Thread(() -> playSound(2, "src/main/resources/sounds/powerup.wav")).start();
    }

    public static void playWizardDeadSound() {
        new Thread(() -> playSound(2, "src/main/resources/sounds/wizard_dead.wav")).start();
    }
    public static void playStageClearSound() {
        new Thread(() -> playSound(2, "src/main/resources/sounds/stage_clear.wav")).start();
    }

    public static void playSound(double volume, String pathname) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File( pathname ));
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
            int nByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (true) {
                nByte = ais.read(buffer, 0, SIZE);
                if (nByte == -1) {
                    break;
                }
                sdl.write(buffer, 0, nByte);
            }
            sdl.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
