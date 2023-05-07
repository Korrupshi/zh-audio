package com.zhtools.generateAudio;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.zhtools.generateAudio.convert.ConvertAudio;
import com.zhtools.generateAudio.download.DownloadYoutube;
import com.zhtools.generateAudio.extract.ExtractSentences;

/**
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        Options options = new Options();
        options.addOption("f", "filename", true, "File name");
        options.addOption("u", "url", true, "YouTube URL");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String fileName = cmd.getOptionValue("filename");
        String url = cmd.getOptionValue("url");

        if (fileName == null || url == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("App", options);
            System.exit(1);
        }
        
        // Inputs
        // String fileName = "candice_ep42";
        // String url = "https://www.youtube.com/watch?v=jKPlNHHYKZo";
        
        // Step 1: Download
        // Download and get the folder where saved
        String folderName = DownloadYoutube.download(fileName,url);

        // Step 2: Extract
        ExtractSentences.extractSentenceAudio(folderName,fileName);

        // // Step 3: Convert
        ConvertAudio.convertToM4a(folderName,fileName);
    }
}
