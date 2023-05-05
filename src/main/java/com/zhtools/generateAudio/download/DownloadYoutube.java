package com.zhtools.generateAudio.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadYoutube {
    public static void main(String[] args) {
        // Inputs
        String fileName = "candice_ep46";
        String url = "https://www.youtube.com/watch?v=xsXRi5XgB34";

        // Set the options for youtube-dl
        String[] ydlOpts = {
            "yt-dlp",
            "--format", "bestaudio/best",
            "--no-playlist",
            "--extract-audio",
            "--audio-format", "wav",
            "--audio-quality", "128K",
            "--quiet",
            "--write-sub",
            "--sub-format", "vtt",
            "--sub-lang", "zh,zh-Hans",
            "--output", "./downloads/%(uploader)s/" + fileName + ".%(ext)s",
            url
        };

        try {
            // Create ProcessBuilder object
            ProcessBuilder pb = new ProcessBuilder(ydlOpts);

            // Start the process
            Process p = pb.start();

            // Read the process output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            System.out.println("[INFO]: Starting downloads...");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            System.out.println("[Complete] Audio and subtitles downloaded!");
        } catch (IOException e) {
            System.err.println("Error downloading audio and subtitles: " + e.getMessage());
        }
    }
}

