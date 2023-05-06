package com.zhtools.generateAudio.extract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
// import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;


public class ExtractSentences {
    public static void main(String[] args) {
        // Input: Name of folder with subtitles file
        String folderName = "Candice X Mandarin";
        String fileName = "candice_ep46";

        // Load subtitle file (.vtt)
        String vttRaw = SubtitleReader.readVTT(folderName, fileName);
        SubtitleData subtitleData = SubtitleReader.parseSubtitle(vttRaw);

        String subtitleText = subtitleData.getSubtitles();

        // Write subtitles file .txt
        // SubtitleReader.writeSubtitles(subtitleText, fileName);

        String[] lines = subtitleText.split("\n");
        Set<String> sentences = new HashSet<>(Arrays.asList(lines));
        List<Tuple<String,String,String>> subtitleTimestamp = subtitleData.getTimestamp();

        extractSentences(fileName,folderName, subtitleTimestamp);

        // // Sort longest sentence first
        // List<String> sortedSentences = new ArrayList<>(sentences);
        // Collections.sort(sortedSentences, new Comparator<String>() {
        //     @Override
        //     public int compare(String s1, String s2) {
        //         return Integer.compare(s2.length(), s1.length());
        //     }
        // });
        // sentences = new LinkedHashSet<>(sortedSentences);

    }

    private static void writeJson(Map<String, Integer> wordCounts) {

        // Create a Gson object with pretty printing enabled
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the map to JSON
        String json = gson.toJson(wordCounts);

        // Write the JSON to a file
        try {
            FileWriter writer = new FileWriter("word_counts.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void extractSentences(String fileName,String folderName,List<Tuple<String,String,String>> subtitles) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        Set<String> dictWords = DictionaryReader.fetchKeys();
        
        // Create a map with empty lists as default values
        Map<String, List<String>> wordSentences = new HashMap<>();
        Map<String, Integer> wordCounts = new HashMap<>();
        Set<String> keepSentences = new HashSet<>();
        Set<String> missingWords = new HashSet<>();

        // Create output directory if it doesn't exist
        String outputPath = String.format(".\\audio\\%s", fileName);
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String text = "";

        // Get input audio file
        String audioPath = String.format(".\\downloads\\%s\\%s.wav",folderName,fileName);
        // File audioFile = new File(audioPath);
        // AudioInputStream audioStream = null;
        // AudioFormat format = null;

        // try {
        //     // Load audio stream
        //     audioStream = AudioSystem.getAudioInputStream(audioFile);

        //     // Get audio format
        //     AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audioFile);
        //     format = fileFormat.getFormat();

           

        // } catch (UnsupportedAudioFileException | IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // Output destination
        String dstPath = String.format(".\\audio\\%s",fileName);

        // For each subtitle line with timestamp, find core sentences
        for (int n = 0; n < subtitles.size(); n++) {
            Tuple<String,String,String> subtitle = subtitles.get(n);
            String start = subtitle.start;
            String end = subtitle.end;
            String sentence = subtitle.sentence;

            // Remove whitespace
            sentence = sentence.replace(" ", "");
            text += sentence + "\n";

            // Get a set of unique words in the sentence
            List<String> words = new ArrayList<>();
            for (SegToken token : segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH)) {
                words.add(token.word);
            }

            // Add words to map and append sentence
            for (String word : words) {
                // Iterate over word and test if in map
                String currentWord = word;
                while (currentWord.length() > 0) {
                    if (dictWords.contains(currentWord)) {
                        break;
                    }
                    currentWord = currentWord.substring(1);
                }

                if (currentWord.length() == 0) {
                    continue;
                }

                // Add to word counts
                wordCounts.merge(currentWord, 1, Integer::sum);

                // Check if this word is missing audio
                // if (!audioWords.contains(currentWord)) {
                //     missingWords.add(currentWord);
                // }

                // Add max 1 sentence per word
                if (wordSentences.get(currentWord) == null || wordSentences.get(currentWord).isEmpty()) {
                    // Convert timestamp to ms
                    // double start_time = Math.round((Integer.parseInt(start.substring(0, 2)) * 3600
                    //         + Integer.parseInt(start.substring(3, 5)) * 60 + Float.parseFloat(start.substring(6))) * 1000);
                    // double end_time = Math.round((Integer.parseInt(end.substring(0, 2)) * 3600
                    //         + Integer.parseInt(end.substring(3, 5)) * 60 + Float.parseFloat(end.substring(6))) * 1000);
                    // wordSentences.computeIfAbsent(currentWord, k -> new ArrayList<>()).add(sentence);

                    // Convert to sec
                    int startSeconds = Integer.parseInt(start.substring(0, 2)) * 3600 +
                    Integer.parseInt(start.substring(3, 5)) * 60 +
                                    (int) Float.parseFloat(start.substring(6));
                    int endSeconds = (int) Math.ceil(Integer.parseInt(end.substring(0, 2)) * 3600 +
                                    Integer.parseInt(end.substring(3, 5)) * 60 +
                                    Float.parseFloat(end.substring(6)));

                    // int startMs = startSeconds * 1000;
                    // int endMs = endSeconds * 1000;

                    wordSentences.computeIfAbsent(currentWord, k -> new ArrayList<>()).add(sentence);


                    // Add used sentence to set
                    keepSentences.add(sentence);

                    // output file
                    String finalPath =  String.format("%s\\%s.wav",dstPath,sentence);

                     // Trim and and export audio
                    AudioTrimmer.trimAudio(audioPath, finalPath, startSeconds, endSeconds);
                    // AudioTrimmer.trimAudio(audioStream,format, finalPath, startSeconds, endSeconds);
                }
            }
            
            // Write the text to a file
            // String outputFilePath = outputDir.getAbsolutePath() + File.separator + "subtitles.txt";
            // try (Writer writer = new BufferedWriter(
                //         new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8))) {
                    //     writer.write(text);
                    // } catch (IOException e) {
                        //     e.printStackTrace();
                        // }
                        
                        
                    }
                    System.out.println(keepSentences.size());

                    // Export word counts json
                    writeJson(wordCounts);
        }
    }
