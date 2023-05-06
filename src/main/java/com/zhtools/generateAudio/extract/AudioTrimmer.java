// https://stackoverflow.com/questions/7546010/obtaining-an-audioinputstream-upto-some-x-bytes-from-the-original-cutting-an-au/7547123#7547123
package com.zhtools.generateAudio.extract;

import java.io.File;
import java.io.IOException;

// import java.io.;
import javax.sound.sampled.*;

public class AudioTrimmer {

  public static void wavToM4a(String inputPath) throws IOException, InterruptedException{
    String outputPath = inputPath.replace(".wav", ".m4a");
    ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputPath, "-c:a", "aac", "-b:a", "128k", outputPath);
    Process process = pb.start();
    process.waitFor();

    // Delete wav file
    // File inputFile = new File(inputPath);
    // inputFile.delete();
}

    public static AudioInputStream loadAudio(File audioFile) throws UnsupportedAudioFileException, IOException {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
      return audioStream;
  }

  public static AudioFormat getFormatInfo(File audioFile)throws UnsupportedAudioFileException, IOException {
      AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audioFile);
      AudioFormat format = fileFormat.getFormat();
      return format;
  }
    
    public static void trimAndExportAudio(String sourceFileName, String destinationFileName, int startSeconds, int endSeconds) {
        int secondsToCopy = endSeconds - startSeconds;
        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;
        try {
          File file = new File(sourceFileName);
          AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
          AudioFormat format = fileFormat.getFormat();
          inputStream = AudioSystem.getAudioInputStream(file);

          // Calculate the number of bytes per second
          int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();

          // Skip to the start position in the input stream
          inputStream.skip(startSeconds * bytesPerSecond);

          // Calculate the number of frames to copy
          long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();

          // Create a new input stream for the segment of audio to be copied
          shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);

          // Write new file
          File destinationFile = new File(destinationFileName);
          AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
          } catch (Exception e) {
              println(e);
          } finally {
              if (inputStream != null) try { inputStream.close(); } catch (Exception e) { println(e); }
              if (shortenedStream != null) try { shortenedStream.close(); } catch (Exception e) { println(e); }
          }
      }

  public static void println(Object o) {
    System.out.println(o);
  }

  public static void print(Object o) {
    System.out.print(o);

  }
}
