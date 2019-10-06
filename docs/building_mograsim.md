# Building Mograsim

These is a very basic instruction set for building Mograsim from source. 

It assumes you know nothing about Eclipse Plug-In development, Maven, or Git.

## Prerequisites

- [Java JDK](http://jdk.java.net/) 11 or later (or [Oracle Implementation](https://www.oracle.com/technetwork/java/javase/downloads/index.html))
- [Eclipse](https://www.eclipse.org/downloads/) 2019-03 or later (TODO: do earlier versions work?)
- _(optional)_ [Git](https://git-scm.com/downloads)

## Install PDE (Plugin Development Environment)

1. In Eclipse, click on "Help" -> "Install New Software..."
2. Next to "Work With:", select "The Eclipse Project Updates"
3. Tick "Eclipse PDE Plug-in Developer Resources" (expand "Eclipse Plugin Development 
   Tools" or type "PDE" in the search bar to see it)
4. Click on "Next >" two times, read and accept the license, and click on "Finish"
5. Wait for the installation to complete (may take a while). When prompted, restart 
   Eclipse.

## Clone the Git repository and import the projects

Can be done via Git or via EGit (Eclipse Git).

### a) via EGit

1. In Eclipse, click on "Window" -> "Show View" -> "Other..."
2. Select "Git Repositories" (expand "Git" to see it); click on "Open"
3. In the "Git Repositories" view, click on "Clone a Git repository"
4. Enter the repository URL ("Host", "Repository path", and "Protocol" should fill 
   automatically); click on "Next >"
5. Select only "master"; click on "Next >"
6. Enter the directory you want the Mograsim projects to be saved in
7. Tick "Clone submodules" and "Import all existing Eclipse projects after clone 
   finishes"; click on "Finish"

### b) via Git (requires Git to be installed)

1. In a command prompt (Terminal on Linux; git-bash on Windows), execute `git clone 
   --recurse-submodules --single-branch -b master <repository URL> <target directory>`
2. _(optional)_ Import the repository in Eclipse:
    1. In Eclipse, open the "Git Repositories" view as described in the first two 
       steps in **a)**
    2. In the "Git Repositories" view, click on "Add an existing local Git repository"
    3. Enter the directory of the repository and tick the repository you see in the 
       list below; click on "Add". (If there is another repository ending with "SWTHelper", 
       ignore it)
3. Import the projects into the workspace
    1. Click on "File" -> "Import..."
    2. Select "Existing Projects into Workspace" (expand "General" to see it); click 
       on "Next >"
    3. Tick "Select root directory:" and "Search for nested projects"
    4. Next to "Select root directory:", enter the directory of the repository; click 
       on "Finish"

Note: After importing the projects, probably an automatic workspace build will start. 
Wait for it to finish before continuing.

## Do the Maven Tycho workaround:

In the project explorer or package explorer, right-click on "net.mograsim.tycho-download", 
click on "Run As" -> "Maven clean" and wait for it to finish.

## Build the Update Site

1. In the project explorer or package explorer, right-click on "net.mograsim", click 
   on "Run As" -> "Maven build..."
2. Next to "Goals", enter "clean verify"; click on "Run" and wait for it to finish 
   (this will take a while)
3. Select all projects, right-click, and click on "Refresh"

The update site now should be built and is located in net.mograsim.plugin.updatesite/target/repository. 
In there you should see, among other files, a folder named "features", a folder named 
"plugins" containing some jar files, and two jar files called "artifacts.jar" and 
"content.jar".

More information about the build, structure and environment can be found in the [development environment description](development_environment.md).