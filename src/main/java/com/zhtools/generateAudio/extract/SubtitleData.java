package com.zhtools.generateAudio.extract;
import java.util.List;

public class SubtitleData {
    private List<Tuple<String,String,String>> timestamp;
    private String subtitles;
    
    public SubtitleData(List<Tuple<String,String,String>> timestamp, String subtitles) {
        this.timestamp = timestamp;
        this.subtitles = subtitles;
    }
    
    public List<Tuple<String,String,String>> getTimestamp() {
        return timestamp;
    }
    
    public String getSubtitles() {
        return subtitles;
    }
}

