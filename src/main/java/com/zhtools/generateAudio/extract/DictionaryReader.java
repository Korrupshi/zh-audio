package com.zhtools.generateAudio.extract;

import java.io.File;
import java.io.FileReader;
import java.util.Set;

import org.json.JSONObject;

public class DictionaryReader {
    public static Set<String> fetchKeys() {
        System.setProperty("file.encoding", "UTF-8");
        try {
            // specify the file path
            File file = new File(".\\data\\hashDict.json");
            
            // read the contents of the file into a string
            FileReader reader = new FileReader(file);
            char[] contents = new char[(int) file.length()];
            reader.read(contents);
            String jsonStr = new String(contents);
            
            // parse the JSON string into a JSONObject
            JSONObject dictionary = new JSONObject(jsonStr);
            
            // Create a set of keys
            Set<String> dict_keys = dictionary.keySet();

            reader.close();

            return dict_keys;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

