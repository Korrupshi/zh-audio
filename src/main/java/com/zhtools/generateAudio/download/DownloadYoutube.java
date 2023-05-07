package com.zhtools.generateAudio.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadYoutube {
    public static  String download(String fileName, String url) {
        /*
         * From the youtube url, downloads wav audio and subtitles in chinese.
         */
        // Set the options for youtube-dl
        String[] ydlOpts = {
            "yt-dlp",
            "--format", "bestaudio/best",
            "--no-playlist",
            "--extract-audio",
            "--audio-format", "wav",
            "--audio-quality", "128K",
            // "--quiet",
            "--write-sub",
            // "--get-filename",
            "--sub-format", "vtt",
            "--sub-lang", "zh,zh-Hans",
            "--output", ".\\downloads\\%(uploader)s\\" + fileName + ".%(ext)s",
            url
        };

        String uploader = null;
        String language = null;

        try {
            // Ensure that the downloads folder exists
            File downloadsFolder = new File(".\\downloads");
            downloadsFolder.mkdir();
            // Create ProcessBuilder object
            ProcessBuilder pb = new ProcessBuilder(ydlOpts);

            // Start the process
            Process p = pb.start();

            // Read the process output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            System.out.println("[DOWNLOAD]: Starting downloads...");
            while ((line = br.readLine()) != null) {

                //[info] jKPlNHHYKZo: Downloading subtitles: zh
                //[ExtractAudio] Destination: downloads\Candice X Mandarin\candice_ep42.wav
                // System.out.println(line);
                // String folderName = line.substring(0, line.lastIndexOf("\\"));
                // uploader = folderName.substring(folderName.lastIndexOf("\\")+1);

                System.out.println(line);
                // Check if uploader is present in the output
                if (line.contains("[ExtractAudio] Destination:")) {
                    String folderName = line.substring(0, line.lastIndexOf("\\"));
                    uploader = folderName.substring(folderName.lastIndexOf("\\")+1);
                }
                // Get language of subs
                if (line.contains("Downloading subtitles:")) {
                    language = line.split("Downloading subtitles: ")[1];
                }
            }
            System.out.println("[Complete] Audio and subtitles downloaded!");
        } catch (IOException e) {
            System.err.println("Error downloading audio and subtitles: " + e.getMessage());
        }
        System.out.println(uploader);
        System.out.println(language);
        return uploader;
    }
}

