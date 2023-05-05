package com.zhtools.generateAudio.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
// import java.io.BufferedReader;
// import java.io.FileInputStream;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.HashSet;
// import java.util.LinkedHashSet;
import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
import java.util.Set;


public class ExtractSentences {
    public static void main(String[] args) {
        // path to subtitle file
        // String src_path = ".\\downloads\\"+folder_name;
        // String output_path = ".\\audio\\"+file_name;
        // Load dictionary
        Set<String> dictKeys = DictionaryReader.fetchKeys();

        // Input: Name of folder with subtitles file
        String folderName = "Candice X Mandarin";
        String fileName = "candice_ep46";

        // Load subtitle file (.vtt)
        String vttRaw = SubtitleReader.readVTT(folderName, fileName);
        SubtitleData subtitleData = SubtitleReader.parseSubtitle(vttRaw);

        String subtitleText = subtitleData.getSubtitles();

        // Write subtitles file .txt
        SubtitleReader.writeSubtitles(subtitleText, fileName);
        String[] lines = subtitleText.split("\n");
        Set<String> sentences = new HashSet<>(Arrays.asList(lines));
        List<List<String>> subtitleTimestamp = subtitleData.getTimestamp();

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
    
}
