package com.franciscodadone.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import com.franciscodadone.util.UpdaterConfig;
import com.franciscodadone.controller.AppController;
import org.json.JSONObject;
import com.franciscodadone.views.DownloadingScreen;
import com.franciscodadone.views.MainFrame;

public class MainApp extends UpdaterConfig {

    public static void main(String[] args) {

        /**
         * Checks if the dir is present.
         */
        File directory = new File(FINAL_APP_DECOMPRESSION_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        frame = new MainFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle(FRAME_TITLE);

        /**
         * Assigns to lastCommitID the commit ID from GitHub.
         */
        JSONObject json;
        try {
            json = AppController.readJsonFromUrl(JSON_URL);
            lastCommitID = (String) json.get("sha");
            System.out.println("Github last commit ID: " + lastCommitID);
        } catch (Exception e1) {
            if(!AppController.launchApp()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Necesitas internet para descargar la aplicación por primera vez.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            frame.dispose();
            ds.dispose();
        }

        /**
         * If true, an update was found.
         * Else... it launches the app.
         */
        if (!AppController.getCommitID().trim().equals(lastCommitID)) {
            System.out.println("Update found!");

            /**
             * Setting up downloading screen
             */
            ds = new DownloadingScreen();
            ds.setVisible(true);
            ds.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ds.setResizable(false);
            ds.setTitle(FRAME_TITLE);
            frame.dispose();

            /**
             * Downloading the update.
             */
            try (BufferedInputStream inputStream = new BufferedInputStream(new URL(REPO_URL).openStream());
                 FileOutputStream fileOS = new FileOutputStream(ZIP_DIRECTORY)) {
                byte[] data = new byte[1024];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
                System.out.println("Download finished");
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * Unzipping the compressed downloaded file.
             */
            try {
                AppController.unzip(ZIP_DIRECTORY, FINAL_APP_DECOMPRESSION_DIRECTORY, "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * Deleting files in the source directory that will be updated.
             * This is mainly to fix a windows issue. In linux worked fine...
             */
            File[] updateFolderFiles = new File(FINAL_APP_DECOMPRESSION_DIRECTORY + File.separator + REPOSITORY_NAME + "-" + UPDATE_BRANCH_NAME).listFiles();
            File[] srcFolderFiles = new File(FINAL_APP_DECOMPRESSION_DIRECTORY).listFiles();
            for(File fileInSrc: srcFolderFiles) {
                for(File fileInUpdate: updateFolderFiles) {
                    if(fileInSrc.getName().equals(fileInUpdate.getName())) {
                        fileInSrc.delete();
                    }
                }
            }

            /**
             * Moves the files from the extracted folder to the main folder and deletes them.
             */
            AppController.moveFiles(
                    new File(FINAL_APP_DECOMPRESSION_DIRECTORY + File.separator + REPOSITORY_NAME + "-" + UPDATE_BRANCH_NAME),
                    new File(FINAL_APP_DECOMPRESSION_DIRECTORY)
            );

            /**
             * Launches the app.
             */
            AppController.launchApp();

            /**
             * Deletes the zip file.
             */
            File updateFile = new File(ZIP_DIRECTORY);
            updateFile.delete();

            AppController.newCommitID(lastCommitID);

        } else {
            System.out.println("No updates found!");
            AppController.launchApp();
            frame.dispose();
        }
    }

    private static String lastCommitID;
    private static MainFrame frame;
    public static DownloadingScreen ds;

}
