package com.franciscodadone.util;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class UpdaterConfig {

    // GLOBAL APP CONSTANTS

    /**
     * Your application name
     */
    protected final static String APP_NAME = "Brikelos";

    /**
     * GitHub API link for your repo (replace this params in your case).
     * "https://api.github.com/repos/:githubUsername/:githubRepo/commits/:githubBranch"
     * (the default GitHub branch is 'master')
     */
    protected final static String JSON_URL = "https://api.github.com/repos/FranciscoDadone/Brikelos-app/commits/update-dev";

    /**
     * Your repository URL followed by '/archive/your_branch_name_here.zip' (default branch name 'master')
     */
    protected final static String REPO_URL = "https://github.com/FranciscoDadone/Brikelos-app/archive/update-dev.zip";

    /**
     * The directory where the downloaded zip file will be contained and later in the installation removed.
     */
    protected final static String ZIP_DIRECTORY = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "Brikelos" + File.separator + "Update.zip";

    /**
     * this is where the final app will be decompressed ('.' is in the same directory that this Launcher is in)
     */
    protected final static String FINAL_APP_DECOMPRESSION_DIRECTORY = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "Brikelos";

    /**
     * This is the command to run the application after decompression or after checking for updates
     */
    protected final static String FINAL_APP_EXECUTABLE = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "Brikelos" + File.separator + "App.jar";

    /**
     * This file only serves the purpose of storing the latest commit ID so the launcher knows if it has to update the app or not
     */
    protected final static String FILE_TO_DETECT_UPDATES = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "Brikelos" + File.separator + "update.txt";

    /**
     * Title in the top of the window.
     */
    protected final static String FRAME_TITLE = APP_NAME;

    /**
     * Name of the branch that handles the updates.
     */
    protected final static String UPDATE_BRANCH_NAME = "update-dev";

    /**
     * Name of your repository.
     */
    protected final static String REPOSITORY_NAME = "Brikelos-app";

}
