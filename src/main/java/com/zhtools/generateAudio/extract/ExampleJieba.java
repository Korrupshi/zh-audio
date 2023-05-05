package com.zhtools.generateAudio.extract;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ExampleJieba {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        JiebaSegmenter segmenter = new JiebaSegmenter();
        String sentence = "我爱自然语言处理";
        String lines = "";
        for (SegToken token : segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH)) {
            lines+= token.word+"\n";
        }

        // Write txt file
        try {
            // Creates a FileOutputStream
            FileOutputStream file = new FileOutputStream("output.txt");
      
            // Creates an OutputStreamWriter
            OutputStreamWriter output = new OutputStreamWriter(file);
      
            // Writes string to the file
            output.write(lines);
      
            // Closes the writer
            output.close();
          }
      
          catch (Exception e) {
            e.getStackTrace();
          }
    }
}


