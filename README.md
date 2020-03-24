# JavaAutoUpdater

JavaAutoUpdater is a Launcher that auto-updates the app for the user, it can be used in any Java application!
This is a cheap option for an auto updater in java because it uses a GitHub repository like a download server and the GitHub API to control the version of the app using the last commit ID provided by the API.

 - IMPORTANT: this app doesn't use a webserver to store the files and it isn't recomended for large scale production, only for small ones.
 
 
 # ¿How it works?
  This app is designed to use it as a launcher for automatic updates for any other java application.
  When you double click this app it will prompt you with the Updates checker and if there is any update avaible it will download and install it for your client and later launch the app, and if there isn't any updates it will launch the app automatically.
  
 - When it detects updates or first open
 
 ![](https://media.giphy.com/media/JQAxNQixOdaVghR8S7/giphy.gif)
 
 - When there isn't any updates avaible for the app
 
 ![](https://media.giphy.com/media/cPO9DlKKNLPxDs5Cvw/giphy.gif)
 
 
 
 # ¿How to use it?
 
 The first thing you want to do is clone this repo and edit in ```'com.franciscodadone.main.MainApp.java'``` the global app constants.
  - APP_NAME: Your application name
  - JSON_URL: GitHub API link for your repo, replace this params in your case. ```'https://api.github.com/repos/:githubUsername/:githubRepo/commits/:githubBranch'``` (the default GitHub branch is 'master'). Example of this: ```'https://api.github.com/repos/FranciscoDadone/Chatty/commits/update'```
  - REPO_URL: Your repository URL followed by ```'/archive/your_branch_name_here.zip'``` (default branch name 'master'). Example: ```'https://github.com/FranciscoDadone/Chatty/archive/update.zip'```
  - ZIP_DIRECTORY: The directory where the downloaded zip file will be contained and later in the installation removed.
  - FINAL_APP_DECOMPRESSION_DIRECTORY: this is where the final app will be decompressed (```'.'``` is in the same directory that this Launcher is in).
  - FINAL_APP_EXECUTABLE: This is the command to run the application after decompression or after checking for updates.
  - FILE_TO_DETECT_UPDATES: This file only serves the porpuse of storing the lastest commit ID so the launcher knows if it has to update the app or not.
  
 When you finished changing all the settings, you have to compile the project as a runnable jar and use that executable as your launcher for your application.
