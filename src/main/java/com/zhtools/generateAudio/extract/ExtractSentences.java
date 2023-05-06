package com.zhtools.generateAudio.extract;

// My functions
import com.zhtools.generateAudio.utils.Utils;

import java.io.BufferedWriter;
// IO
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
// Data types
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Writing Json
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Jieba CWS
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

public class ExtractSentences {
    /*
     * 1. Extract and parse subtitles from VTT file 
     * 2. Create wordsSentence map, sentence to keep, and wordCounts
     *      - Get the words missing audio
     *      - Export word_counts.json, subtitles.txt, missing_words.txt
     * 3. Loop over subtitles to extract audio and kept sentence
     *      - export audio
     */
    public static void main(String[] args) {
        // # 1. Extract and parse subtitles from VTT file
        String folderName = "Candice X Mandarin";
        String fileName = "candice_ep46";

        // Load subtitle file (.vtt)
        String vttRaw = SubtitleReader.readVTT(folderName, fileName);
        SubtitleData subtitleData = SubtitleReader.parseSubtitle(vttRaw);

        String subtitleText = subtitleData.getSubtitles();

        // # 2. Create unique words list and sentence to keep
        String[] lines = subtitleText.split("\n");
        Set<String> sentences = new HashSet<>(Arrays.asList(lines));

        // Sort longest first
        sentences = Utils.sortSet(sentences);

        // Words with audio
        String wordsPath = ".\\audio\\words";
        Set<String> audioWords = Utils.getFileNames(wordsPath);

        // Get dictionary words
        Set<String> dictWords = DictionaryReader.fetchKeys();
        JiebaSegmenter segmenter = new JiebaSegmenter();

        Map<String, Integer> wordCounts = new HashMap<>();
        Set<String> keepSentences = new HashSet<>();
        Map<String, List<String>> wordSentences = new HashMap<>();
        Set<String> missingWords = new HashSet<>();

        for(String sentence : sentences){
            // Remove whitespace
            sentence = sentence.replace(" ", "");

            // Get a set of unique words in the sentence
            List<String> words = new ArrayList<>();
            
            for (SegToken token : segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH)) {
                words.add(token.word);
            }

            // Add words to map and append sentence
            for (String word : words) {
                // Iterate over word and test if in dictionary
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

                if (wordSentences.get(currentWord) == null || wordSentences.get(currentWord).isEmpty()) {
                    wordSentences.computeIfAbsent(currentWord, k -> new ArrayList<>()).add(sentence);

                    // Add used sentence to set
                    keepSentences.add(sentence);
                }

                if(audioWords.contains(currentWord) == false){
                    missingWords.add(currentWord);
                }
            }
        }
        
        // Create output directory if it doesn't exist
        String outputPath = String.format(".\\audio\\%s", fileName);
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Write subtitles file .txt
        SubtitleReader.writeSubtitles(subtitleText, fileName);

        // Export word counts json
        wordCounts = Utils.sortByValue(wordCounts, false);
        exportWordCounts(wordCounts,fileName);
        
        // # 3. Loop over subtitles to extract audio and kept sentence
        List<Tuple<String,String,String>> subtitles = subtitleData.getTimestamp();

        // Get input audio file
        String audioPath = String.format(".\\downloads\\%s\\%s.wav", folderName, fileName);
        
        // For each subtitle line with timestamp, find core sentences
        // for (int n = 0; n < subtitles.size(); n++) {
        //     Tuple<String, String, String> subtitle = subtitles.get(n);
        //     String sentence = subtitle.sentence;
            
        //     // Remove whitespace
        //     sentence = sentence.replace(" ", "");
            
        //     if(keepSentences.contains(sentence)){
        //         String start = subtitle.start;
        //         String end = subtitle.end;
        //         // Convert to sec
        //         int startSeconds = Integer.parseInt(start.substring(0, 2)) * 3600 +
        //                 Integer.parseInt(start.substring(3, 5)) * 60 +
        //                 (int) Float.parseFloat(start.substring(6));
        //         int endSeconds = (int) Math.ceil(Integer.parseInt(end.substring(0, 2)) * 3600 +
        //                 Integer.parseInt(end.substring(3, 5)) * 60 +
        //                 Float.parseFloat(end.substring(6)));

        //         // output file
        //         String finalPath = String.format("%s\\%s.wav", outputPath, sentence);

        //         // Trim and and export audio
        //         AudioTrimmer.trimAndExportAudio(audioPath, finalPath, startSeconds, endSeconds);
                
        //     }
        //     }

            // # 4. Get missing words
            List<String> strings = new ArrayList<>();
            String string = "";
            int maxLength = 0;
            Set<String> missingWordsSet = new HashSet<>(missingWords);
            for (int i = 0; i < missingWordsSet.size(); i++) {
                String word = (String) missingWordsSet.toArray()[i];
                int length = word.length() + 1; // +1 because we add a period to each word
                if (maxLength + length > 500) {
                    strings.add(string);

                    // reset values
                    string = "";
                    maxLength = 0;
                } else {
                    string += word + "。";
                    maxLength = string.length();
                }

                // Add the remaining string at last run
                if (i+1 == missingWordsSet.size()) {
                    strings.add(string);
                }
            }

            String outputMissing = "";
            for (int i = 0; i < strings.size(); i++) {
                outputMissing += "String " + (i+1) + "\n" + strings.get(i) + "\n";
            }

            // Save missing_words
            String missingPath = outputPath + "\\missing_words.txt";
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(missingPath), StandardCharsets.UTF_8))) {
                writer.write(outputMissing);
                System.out.println("[INFO]: " + missingWords.size() + " words need audio");
            } catch (IOException e) {
                System.err.println("[ERROR]: Failed to write missing_words.txt file.");
                e.printStackTrace();
            }


            System.out.println(String.format("[COMPLETE]: %d sentences segmented.",keepSentences.size()));
        }

    private static void exportWordCounts(Map<String, Integer> wordCounts, String fileName) {
        String outputFile = String.format(".\\audio\\%s\\word_counts.json", fileName);

        // Create a Gson object with pretty printing enabled
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the map to JSON
        String json = gson.toJson(wordCounts);

        // Write the JSON to a file
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
