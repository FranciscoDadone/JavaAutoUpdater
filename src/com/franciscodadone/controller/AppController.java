package com.franciscodadone.controller;

import com.franciscodadone.main.MainApp;
import com.franciscodadone.util.UpdaterConfig;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AppController extends UpdaterConfig {

    /**
     * Moves the contents of a folder to another.
     * @param sourceFile
     * @param destFile
     */
    public static void moveFiles(File sourceFile, File destFile) {
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            assert files != null;
            for (File file : files) moveFiles(file, new File(destFile, file.getName()));
            if (!sourceFile.delete()) throw new RuntimeException();
        } else {
            if (!destFile.getParentFile().exists())
                if (!destFile.getParentFile().mkdirs()) throw new RuntimeException();
            if (!sourceFile.renameTo(destFile)) throw new RuntimeException();
        }
    }

    /**
     * Reads the JSON.
     * @param rd
     * @return
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Gets the JSON response from GitHub.
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
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

    /**
     * Updates the commit ID in the saved file.
     * @param commitID
     */
    public static void newCommitID(String commitID) {
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
            MainApp.ds.dispose();    //kills the downloading screen
        }
    }

    /**
     * Reads the commit ID from the saved file.
     * @return
     */
    public static String getCommitID() {
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
            System.out.println("No '" + FILE_TO_DETECT_UPDATES + "' found.");
        }
        return "";
    }

    /**
     * Unzips a zip file
     * Used to unzip the downloaded file.
     *
     * @param source
     * @param destination
     * @param password
     */
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

    /**
     * Launches the updated app.
     * @return boolean
     */
    public static boolean launchApp() {
        try {
            /**
             * If it is windows it adds " to the path.
             */
            Process p = Runtime.getRuntime().exec(
                    (System.getProperty("os.name").toLowerCase().contains("windows")) ?
                            "java -jar" + '"' + FINAL_APP_EXECUTABLE + '"' :
                            "java -jar " + FINAL_APP_EXECUTABLE
            );

            /**
             * Checks for errors in the output.
             * If there are errors... returns false and displays a JOptionPane.
             */
            InputStream error = p.getErrorStream();
            String err = "";
            for (int i = 0; i < error.available(); i++) err += error.read();

            return !err.toLowerCase().contains("error");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
