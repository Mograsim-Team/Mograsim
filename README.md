# About

Mograsim is a modular, graphical simulator for teaching microprogramming, ISAs and 
circuit logic in a way that allows for a smooth transition between those levels. 

The aim is to give students a better understanding how these layers blend in a machine, 
and what the purpose of microprogramming is. In more detail, Mograsim allows:

* Programming and running simple assembler on the machine
* Defining an ISA (Instruction Set) to use in the assembler
* Microprogram the CPU to implement the specified ISA
* View the Circuit Logic operating and executing the microprogram in detail

Mograsim focuses especially on the AMD Am2900 Family microprocessors in a specific 
16-bit arrangement used by the Technical University of Munich as an example.  

# Building Mograsim

These are a very basic instructions for building Mograsim from source. 

It assumes you know nothing about Eclipse Plug-In development, Maven, or Git.

## Prerequisites

1. [Java 11 JDK](http://jdk.java.net/java-se-ri/11) [(or Oracle Implementation)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. [Eclipse 2019-03 or later](https://www.eclipse.org/downloads/)
3. (optional) [Git](https://git-scm.com/downloads)

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

### a) via EGit (Eclipse integration)

1. In Eclipse, click on "Window" -> "Show View" -> "Other..."
2. Select "Git Repositories" (expand "Git" to see it)
3. In the "Git Repositories" view, click on "Clone a Git repository"
4. Enter the repository URL ("Host", "Repository path", and "Protocol" should fill 
   automatically); click on "Next >"
5. Select only "development"; click on "Next >"
6. Enter the directory you want the Mograsim projects to be saved in
7. Tick "Clone submodules" and "Import all existing Eclipse projects after clone 
   finishes"; click on "Finish"

### b) via Git (requires Git to be installed)

1. In a command prompt (Terminal on Linux; git-bash on Windows), execute `git clone 
   --recurse-submodules --single-branch -b development <repository URL> <target directory>`
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

More information about the build, structure and environment can be found in the [MAVEN-TYCHO-README.MD](MAVEN-TYCHO-README.MD).
