# JavaAutoUpdater

JavaAutoUpdater is an application Launcher that auto-updates the app for the user, it can be used in any Java application!
This is a cheap option for an auto updater in java because it uses a GitHub repository like a download server and the GitHub API to control the version of the app using the last commit ID provided by the API.

 - IMPORTANT: this app don't use a webserver to store the files and it's not recomended for large scale production, only for small ones.
 
 # ¿How to use it?
 
 The first thing you want to do is clone this repo and edit in 'com.franciscodadone.main.MainApp.java' the global app constants.
  - APP_NAME: Your application name
  - JSON_URL: GitHub API link for yout repo, replace this params in your case. ```'https://api.github.com/repos/:githubUsername/:githubRepo/commits/:githubBranch'``` (the default GitHub branch is 'master'). Example: ```'https://api.github.com/repos/FranciscoDadone/Chatty/commits/update'```
  - REPO_URL: Your repository URL followed by '/archive/your_branch_name_here.zip' (default branch name 'master'). Example: ```'https://github.com/FranciscoDadone/Chatty/archive/update.zip'```
  - ZIP_DIRECTORY: The directory where the downloaded zip file will be contained and later in the installation removed.
  - FINAL_APP_DECOMPRESSION_DIRECTORY: this is where the final app will be decompressed ('.' is in the same directory that this Launcher is in).
  - FINAL_APP_EXECUTABLE: This is the command to run the application after decompression or after checking for updates.
  - FILE_TO_DETECT_UPDATES: This file only serves the porpuse of storing the lastest commit ID so the launcher knows if it has to update the app or not.
