package com.franciscodadone.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

import com.franciscodadone.util.UpdaterConfig;
import com.franciscodadone.util.Utils;
import org.json.JSONObject;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;

import com.franciscodadone.views.DownloadingScreen;
import com.franciscodadone.views.MainFrame;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

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

        // Get the last commit ID

        JSONObject json;
        try {
            json = readJsonFromUrl(JSON_URL);
            lastCommitID = (String) json.get("sha");
            System.out.println("Github last commit ID: " + lastCommitID);
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (!getCommitID().trim().equals(lastCommitID)) {
            //DOWNLOADING FILES...
            System.out.println("Update found!");
            ds = new DownloadingScreen();
            ds.setVisible(true);
            ds.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ds.setResizable(false);
            ds.setTitle(FRAME_TITLE);
            frame.dispose();
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

            try {
                unzip(ZIP_DIRECTORY, FINAL_APP_DECOMPRESSION_DIRECTORY, "");
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
            Utils.moveFiles(
                    new File(FINAL_APP_DECOMPRESSION_DIRECTORY + File.separator + REPOSITORY_NAME + "-" + UPDATE_BRANCH_NAME),
                    new File(FINAL_APP_DECOMPRESSION_DIRECTORY)
            );

            /**
             * Launches the app.
             */
            launchApp();

            /**
             * Deletes the zip file.
             */
            File updateFile = new File(ZIP_DIRECTORY);
            updateFile.delete();

            newCommitID(lastCommitID);

        } else {
            System.out.println("No updates found!");
            launchApp();
            frame.dispose();
        }
    }

    // READING JSON FROM GITHUB API ------------- //
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static void newCommitID(String commitID) {
        try {
            FileWriter fileWriter = new FileWriter(FILE_TO_DETECT_UPDATES);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(commitID);
            System.out.println("CommitID updated '" + commitID + "'");
            bufferedWriter.close();
        } catch (Exception ex) {
            System.out.println(
                    "Error updating the commit ID '" + FILE_TO_DETECT_UPDATES + "'"
            );
        } finally {
            ds.dispose();    //kills the downloading screen and boots the actual app
        }
    }

    private static String getCommitID() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_TO_DETECT_UPDATES));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            br.close();
            return everything;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void unzip(String source, String destination, String password) {

        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void launchApp() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Runtime.getRuntime().exec("java -jar " + '"' + FINAL_APP_EXECUTABLE + '"');
            } else {
                Runtime.getRuntime().exec("java -jar " + FINAL_APP_EXECUTABLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String lastCommitID;
    private static MainFrame frame;
    private static DownloadingScreen ds;

}
