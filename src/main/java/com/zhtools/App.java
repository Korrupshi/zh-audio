package com.zhtools;

import com.zhtools.generateAudio.convert.ConvertAudio;
import com.zhtools.generateAudio.download.DownloadYoutube;
import com.zhtools.generateAudio.extract.ExtractSentences;

/**
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        // Step 1: Download
        String fileName = "candice_ep42";
        String url = "https://www.youtube.com/watch?v=jKPlNHHYKZo";
        
        // Download and get the folder where saved
        String folderName = DownloadYoutube.download(fileName,url);

        // Step 2: Extract
        ExtractSentences.extractSentenceAudio(folderName,fileName);

        // // Step 3: Convert
        ConvertAudio.convertToM4a(folderName,fileName);
    }
}
