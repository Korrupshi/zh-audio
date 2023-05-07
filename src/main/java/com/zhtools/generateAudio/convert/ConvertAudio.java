package com.zhtools.generateAudio.convert;

import java.io.File;
import java.io.IOException;

public class ConvertAudio {

    public static void convertToM4a(String folderName, String fileName) {
        System.out.println("[CONVERTING]: Converting audio, please wait...");
        // String fileName = "candice_ep46";
        String audioPath = ".\\audio\\" + fileName;

        File folder = new File(audioPath);
        File[] audioFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        for (int i = 0; i < audioFiles.length; i++) {
            System.out.println((i+1) + "/" + audioFiles.length);
            File file = audioFiles[i];
            String inputPath = file.getPath();
            try {
                wavToM4a(inputPath);
            } catch (IOException e) {
                System.out.println("[ERROR]");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("[ERROR]");
                e.printStackTrace();
            }
        }

        // Delete downloaded wav and sub files
        File downloadsFolder = new File(String.format(".\\downloads\\%s",folderName));
        deleteFolder(downloadsFolder);

        System.out.println("[COMPLETE]: converting complete");

    }

    public static void wavToM4a(String inputPath) throws IOException, InterruptedException{
            String outputPath = inputPath.replace(".wav", ".m4a");

            File outputFile = new File(outputPath);
            // Convert to m4a if the file doesnt already exist
            if (outputFile.exists() == false) {
                ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputPath, "-c:a", "aac", "-b:a", "128k", outputPath);
                Process process = pb.start();
                process.waitFor();

                // // Delete wav file
                File inputFile = new File(inputPath);
                inputFile.delete();
            } 
    }

    public static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            folder.delete();
        } else {
            System.out.println("Cannot delete, Folder does not exist.");
        }
    }


    }

