package com.zhtools.generateAudio.extract;
import java.util.List;

public class SubtitleData {
    private List<List<String>> timestamp;
    private String subtitles;
    
    public SubtitleData(List<List<String>> timestamp, String subtitles) {
        this.timestamp = timestamp;
        this.subtitles = subtitles;
    }
    
    public List<List<String>> getTimestamp() {
        return timestamp;
    }
    
    public String getSubtitles() {
        return subtitles;
    }
}

