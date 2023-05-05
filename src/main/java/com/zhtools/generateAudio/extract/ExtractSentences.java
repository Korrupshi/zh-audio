package com.zhtools.generateAudio.extract;

// import java.io.BufferedWriter;
import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.OutputStreamWriter;
// import java.io.Writer;
// import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
// import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.SegToken;


public class ExtractSentences {
    public static void main(String[] args) {
        // path to subtitle file
        // String src_path = ".\\downloads\\"+folder_name;
        // String output_path = ".\\audio\\"+file_name;
        
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

        extractSentences(fileName, subtitleTimestamp);

        // // Sort longest sentence first
        List<String> sortedSentences = new ArrayList<>(sentences);
        Collections.sort(sortedSentences, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s2.length(), s1.length());
            }
        });
        sentences = new LinkedHashSet<>(sortedSentences);

    }
    
    private static void extractSentences(String fileName,List<Tuple<String,String,String>> subtitles) {
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
                    double start_time = Math.round((Integer.parseInt(start.substring(0, 2)) * 3600
                            + Integer.parseInt(start.substring(3, 5)) * 60 + Float.parseFloat(start.substring(6))) * 1000);
                    double end_time = Math.round((Integer.parseInt(end.substring(0, 2)) * 3600
                            + Integer.parseInt(end.substring(3, 5)) * 60 + Float.parseFloat(end.substring(6))) * 1000);
                    wordSentences.computeIfAbsent(currentWord, k -> new ArrayList<>()).add(sentence);

                    // Add used sentence to set
                    System.out.println(sentence);
                    keepSentences.add(sentence);
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
        }
    }
