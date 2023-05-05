package com.zhtools.generateAudio.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.HashSet;
// import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
// import java.io.IOException;

public class SubtitleReader {

    public static void extractSubtitles() {
        
    }

    public static String readVTT(String folderName, String fileName) {
        // path to folder containing vtt file
        String srcPath = String.format(".\\downloads\\%s\\%s", folderName, fileName);
    
        String vttData = null;
    
        // Try to read file with ".zh.vtt" extension
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(String.format("%s.zh.vtt",srcPath)), StandardCharsets.UTF_8))) {
            vttData = readVTTFile(reader);
        } catch (IOException e1) {
            // If file with ".zh.vtt" extension not found, try to read file with ".zh-Hans.vtt" extension
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(String.format("%s.zh-Hans.vtt",srcPath)), StandardCharsets.UTF_8))) {
                vttData = readVTTFile(reader);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    
        return vttData;
    }
    
    private static String readVTTFile(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
    public static SubtitleData parseSubtitle(String vttData) {
        // Pattern for timestamp and subtitle text
        String pattern = "(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+-->\\s+(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s*\n([^\\n]*)";
    
        // Extract regex and matches
        Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE);
        Matcher matcher = regex.matcher(vttData);
    
        // Subtitle data variables
        List<Tuple<String,String,String>> subtitleTimestamp = new ArrayList<>();
        StringBuilder subtitleTextBuilder = new StringBuilder();
    
        // Extract subtitle data from matches
        while (matcher.find()) {
            // Extract the subtitle data from the match
            List<String> subtitleData = List.of(matcher.group(1), matcher.group(2), matcher.group(3).trim());
            Tuple<String, String, String> tuple = new Tuple<>(matcher.group(1), matcher.group(2), matcher.group(3).trim());
    
            // Add the subtitle data to the list
            // subtitleTimestamp.add(subtitleData);
            subtitleTimestamp.add(tuple);
    
            // Append the subtitle text to the builder
            subtitleTextBuilder.append(subtitleData.get(2)).append("\n");
        }
        String subtitleText = subtitleTextBuilder.toString();
    
        return new SubtitleData(subtitleTimestamp, subtitleText);
    }

        public static void writeSubtitles(String text, String fileName) {
            String outputDir = String.format(".\\audio\\%s", fileName);
            File outputFolder = new File(outputDir);
            outputFolder.mkdirs(); // create directories if they do not exist

            File outputFile = new File(outputDir, "subtitles.txt");
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
