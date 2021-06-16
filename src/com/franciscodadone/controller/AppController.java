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
            MainApp.ds.dispose();    //kills the downloading screen and boots the actual app
        }
    }

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
}
