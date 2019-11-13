# Getting started with Mograsim

## Prerequisites

- [Java](http://jdk.java.net/) 11 or later (or [Oracle Implementation](https://www.oracle.com/technetwork/java/javase/downloads/index.html))
- [Eclipse](https://www.eclipse.org/downloads/) 2019-03 or later (Open question: do earlier versions work?)

## Install Mograsim

1. Start Eclipse.
2. Go to "Help" -> "Install New Software...".
3. Add the Mograsim update site:
	1. Click on "Add...".
	2. Next to "Location:", enter the update site address. Currently, this is http://vmschulz34.in.tum.de/updatesite/. This address could, however, change in the future.
	3. Click on "Add".
1. Tick "Mograsim".
2. Click on "Next >" two times, read and accept the license, and click on "Finish".
3. Confirm that you want to install unsigned content.
4. Wait for the installation to complete (may take a while). When prompted, restart Eclipse.

## Enable the Launch action set

1. Go to "Window" -> "Perspective" -> "Open Perspective"
   and select "Mograsim" or click "Other..." and then select Mograsim.<br>
   This can also be done by the perspective switcher on the right.

## Create a new Mograsim project

1. Create a new Mograsim project. (Go to "File" -> "New" -> "Project...", select "Mograsim" -> "Mograsim 
   Project".)
2. Give it a project name and a Mograsim machine and finish.

Alternatively, you can create a general Project and add the Mograsim nature to the new project and set it up:

1. Open the properties dialog of the new project. (Right-click on it, select Properties.)
2. Go to the "Project Natures" page, click on "Add...". If a confirmation dialog pops up, confirm.
3. Select "Mograsim Project Nature"; click on "OK".
4. Click on "Apply and Close" and re-open the properties dialog.
5. Go to the new "Mograsim" page, select "Am2900Simple", click on "Apply and Close".

## Write a MPM file (containing the microprogram)

1. Create a new file with the extension ".mpm". (Right-click on the project -> "New" -> "File"; enter the filename; click on "Finish".)
2. The Mograsim instruction editor should open. If not, right-click on the MPM file -> "Open With" -> "Other..."; select "Instruction Editor"; click on "OK".
3. Write a microprogram. (Note: The MPROM is hardcoded to be Opcode * 0x10.)

Every cell differing from the default value is highlighted with a cursive font and green background.

## _(optional)_ Write a MEM file (containing the initial contents of the memory)

1. Create a new file with the extension ".mem" (as described above).
2. The Mograsim memory editor should open. If not, open it as described above.
3. Write the memory contents.

Each table row contains one (16 bit wide) memory cell.  
The two text fields labeled "Address" and "Number of cells" only refer to the cells displayed simultaneously in the editor. The editor internally retains all 65536 addressable cells.

## Open the views "Simulation", "Debug", "Memory" and "Registers"

1. Go to "Window" -> "Show View" -> "Other...", select a view; click on "Open".  
The Simulation view is in the category "Mograsim" and the other views in "Debug".

It is recommended to move the Simulation view to the Editor pane.

## Create a machine launch configuration and run it

1. Click on the little triangle next to the "Launch" symbol in the toolbar; click on "Run configurations...".
2. Right-click on "Mograsim machine" -> "New Configuration".
3. Enter the Mograsim project containing both the MPM and MEM file, as well as these files.
4. If you don't have a MEM file, leave the according field blank. This causes the memory to be initialized with 0.
5. Click "Run". The Simulation view now should contain a rectangle containing either the text "Am2900" in a very small font or a huge mess of smaller rectangles connected by colored lines.

The machine doesn't start running yet since it starts paused.

## Set up and get used to the views

### Simulation view (see also the [simulation model explanation](model.md))

Move the view around by dragging with the left mouse button held.  
Zoom in and out by either scrolling up or down or by dragging down and up with the middle mouse button held.

Using the slider or by directly entering a number in the text field, the machine can be slowed down or sped up.

Also, a "step by step execution" mode can be enabled.
Step by step execution means that the machine is automatically paused on each rising edge of the clock.  

At the bottom of the Simulation view, a single instruction table row is displayed. This row contains the instruction currently being executed.
The MPM can be modified by this line. This is not recommended, however, because changes done here are not reflected in the MPM file, and will be undone if the MPM file is hot-replaced (see below), even if the changed row didn't change in the hot replace. Also, as for a hot replace, these changes doesn't affect the currently active microinstruction.

### Memory view

1. Select the preferred numerical representation
2. Right-click on the table -> "Format...".
3. Select 16 units per row and one unit per column. (8 units per row if 16 don't fit on the screen.)

The table now displays the contents of the currently running machine. At this moment, this should be equal to the contents of the initial memory specified when creating the launch configuration. This table updates automatically if the machine writes a memory cell. In this case, for a short time, this cell is highlighted using a red font and with a small delta symbol in the cell's corner.

### Register view

1. Expand the register group you would like to view.  
It should contain the registers R0-R15 as well as the Q register.  
All of them should be 0, displayed as a bitstring

It is possible to change the register contents via this view by clicking on the old value and entering a new bitstring. It is not recommended to use `Z` in these bitstrings.

### Debug view

Lists all currently running machines. The Simulation, Memory, and Register view follow the selection done here.
A Mograsim machine launch contains one debug target, which contains one thread labeled "pseudo thread".  
The pseudo thread contains one stack frame labeled "pseudo stack frame" as long as the machine is paused. Unfortunately, it is required by Eclipse that threads don't have stack frames as long as they are running.  
For the Memory view to work, the pseudo thread or pseudo stack frame of a machine launch needs to be selected; for the Register view, the stack frame needs to be selected.

### Toolbar

The toolbar also contains some relevant buttons: "Resume", "Suspend", and "Terminate".  
Suspend not only causes the clock to stop, but freezes the other components and wires.  Because of this, some components can seem to be in an incorrect state, for example the two inputs of a NAND gate could be 1, while the output also is 1.

## Start the machine

Set the simulation speed to the lowest setting.
In the toolbar, click the "Resume" button.
Slowly increase the simulation speed until the wires in the Simulation view start flickering. The machine is now running and starts executing the microprogram.

## Notes

1. If the MPM file used by a machine launch is opened in the Instruction editor, the row corresponding to the microinstruction currently being executed is highlighted there using a bold font and yellow background.
2. If the MPM file changes, after confirmation, the changes are hot-replaced into the machine.
	Changes in the MPM file don't affect the currently active microinstruction, however.
3. Some of the behaviour described here can be changed in the Eclipse Preferences (in the pages "Mograsim" and "General" -> "Appearance" -> "Colors and Fonts"). For these cases the default behaviour is described.

## Troubleshooting

- If nothing Mograsim-related seems to be installed after installing Mograsim, make sure Eclipse uses Java 11 or later.
1. Go to "Help" -> "About Eclipse IDE", click on "Installation Details", go to the tab "Configuration".
2. Search the line starting with "java.version=". (This line is in the "System properties" block, which usually occupies the first 200 lines.)
3. If this line specifies a version less than 11, configure your Eclipse installation to use a Java 11 JRE (or later). See https://wiki.eclipse.org/FAQ_How_do_I_run_Eclipse%3F#Find_the_JVM.
