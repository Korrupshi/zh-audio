package com.zhtools.generateAudio.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Utils {
    public static Set<String> getFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        Set<String> fileNames = new HashSet<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String word = fileName.substring(0, fileName.lastIndexOf('.'));
                    fileNames.add(word);
                }
            }
        }
        return fileNames;
    }
    
    public static Set<String> sortSet(Set<String> list) {
        List<String> sortedSentences = new ArrayList<>(list);
        Collections.sort(sortedSentences, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s2.length(), s1.length());
            }
        });
        Set<String> sortedSet = new LinkedHashSet<>(sortedSentences);

        return sortedSet;
        
    }
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean ascending)
            {
                List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

                // Sorting the list based on values
                list.sort((o1, o2) -> ascending ? o1.getValue().compareTo(o2.getValue()) == 0
                        ? o1.getKey().compareTo(o2.getKey())
                        : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                        ? o2.getKey().compareTo(o1.getKey())
                        : o2.getValue().compareTo(o1.getValue()));
                return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

            }
    
}
