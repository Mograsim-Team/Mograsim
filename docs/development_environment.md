# Mograsim Development Environment

<span style="color:grey">_Mograsim Development Documentation Version 0.4 --- 2019-10-17_</span>

A short guide to the Mograsim Maven Tycho configuration and Maven Tycho in general, 
as well as some information on Eclipse Plugin Development and OSGi.


## Maven Tycho

[Maven Tycho](https://www.eclipse.org/tycho/sitedocs/index.html) is a plugin for 
[Maven](http://maven.apache.org) that allows building Eclipse and OSGi Projects comfortably 
and automatically using Maven.

Useful links:
- [eclipse.org/tycho](https://www.eclipse.org/tycho/)
- [wiki.eclipse.org/Category:Tycho](https://wiki.eclipse.org/Category:Tycho)
- [vogella.com/tutorials/EclipseTycho](https://www.vogella.com/tutorials/EclipseTycho/article.html)
- [eclipse.org/tycho/sitedocs](https://www.eclipse.org/tycho/sitedocs/index.html)
- [Tycho pomless sources](https://git.eclipse.org/c/tycho/org.eclipse.tycho.extras.git/tree/tycho-pomless/src/main/java/org/eclipse/tycho/pomless) 
  (see *Mapping for naming and other conventions, TychoAggregatorMapping for folders)

## OSGi

OSGi is a module system for Java (completely unrelated to the Java 9 Jigsaw module 
system) that allows detailed control over modules, dependencies, versions and more. 
The file associated with OSGi here is a specific MANIFEST.MF in the META-INF directory 
of each project.

Roughly, an OSGi bundle has:
- a **symbolic name** that acts as an identifier, therefore it should be unique and 
  must not be changed, otherwise it a large portion of the configuration would break. 
  Never change that field after distribution.
- a **bundle name** and **bundle vendor**; this is only for the users and developers 
  and is not constrained in any way. You can change that, but it should be consistent 
  across all Mograsim modules. Both can be externalized to
- a **bundle version** that denotes current the version of the bundle. The `qualifier` 
  is replaced during the build process with a timestamp of the format `YYYYMMDDhhmmss`. 
  The version itself can be set using the maven tycho version plugin, which replaces 
  not only the maven versions, but all (equivalent) versions in the MANIFEST as well.
- a **bundle required execution environment** (abbr. BREE) where the bundle's *minimal* 
  JDK version is set. This is also the one eclipse uses and displays as JRE System 
  Library in the Eclipse projects.<br> This should be the same as the one the target 
  definition used for the build and the same that maven is run with ( -> check the 
  Run Configuration)

## Mograsim Structure

The tree of Mograsim projects:

- `plugins` - _This contains all bundles/plugins that provide functionality to Mograsim_
    - **net.mograsim.logic.core** <br> The core logic for pure simulation. This contains 
      the most important low-level logic circuits and gates and defines how that 
      logic gets simulated. The underlying system for simulation in Mograsim is an 
      event based approach (see `Timeline`), using VHDL-like logic defined in `Bit` 
      and `BitVector`. More complex ciruits are build out of `Wire`s connecting `Component`s.
    - **net.mograsim.logic.model** <br>The model describes how that core logic is 
      displayed and arranged, and allows (de-)serialization to JSON. The core logic 
      model gets generated based on this. Every basic net.mograsim.logic.core Component 
      has a model equivalent here that represents it in the GUI and persisted state 
      (JSON).
    - **net.mograsim.logic.model.editor** <br> This bundle contains a standalone 
      SWT-based editor to edit Mograsim JSON-models in a comfortable way. It can 
      be used to create a new `SubmodelComponent` by arranging existing ones to a 
      new component. Note that editing and simulation are entirely different processes 
      in Mograsim, the editor cannot simulate and a running simulation cannot be 
      edited. (At least in the current state)
    - **net.mograsim.logic.model.am2900** <br> This plugin contains a model for the 
      AMD Am2900 Family Microprocessors arranged into a microprogrammable model. 
      This can be used in the microprogram editor of the Mograsim plugin and in the 
      Mograsim assembler editor (by using a properly set up instruction set). We 
      took care that the plugin is independent, meaning that it only provides an 
      implementation for an extension point of the net.mograsim.machine plugin and 
      no other Mograsim depends on it.<br> This is also the way to go if you want 
      to add your own machine definitions to Mograsim and use them there.
    - **net.mograsim.machine** <br> The machine plugin defines an extension point 
      that can be extended and implemented in other (your own) plugins to provide 
      new machines to Mograsim. In addition to the extension point and the most important 
      interfaces, it contains some commonly required or useful components to build/define 
      another machine. net.mograsim.logic.model.am2900 for example is such an extension. 
      The machine plugin itself scans for all available implementations of the interface 
      given by the extension point, and other parts of the Mograsim plugin get access 
      to them that way.
    - **net.mograsim.plugin.core** <br> This is - as the name says - the core plugin 
      of mograsim. Here lie all the Eclipse Platform specific parts that make Mograsim 
      as an Eclipse plugin work. This includes the different editors, view, settings 
      and other functional extension to the Eclipse IDE.
    - **net.mograsim.plugin.core.nl_de** <br> This optional plugin provides a German 
      localization for Mograsim.
    - **net.mograsim.plugin.branding** <br> This contains elements for branding the 
      feature plugin, and additional resources and raw files (original logo and icon 
      in SVG and Adobe Illustrator and similar).
    - **net.mograsim.plugin.docs** <br> The Plugin providing the user documentation.
    - **net.mograsim.preferences** <br> A plugin for managing and accessing preferences.
- `features` - _This contains all Mograsim feature plugins_
    - **net.mograsim.feature** <br> The Mograsim feature plugin, containing all the 
      plugins above and combining them into a feature. A feature is a plugin collection 
      with additional properties that allows for easy installation by the end user. 
      For this purpose, it also contains license information, authors, updatesite 
      location (allows eclipse to automatically check if updates are available) and 
      more.
- `products` - _This contains standalone Mograsim product(s)_
    - **net.mograsim.product** <br> A product can be pretty much anything (see Eclipse 
      RCP), in our case, this is simply a pre-configured Eclipse package, comparable 
      to the packages located at [www.eclipse.org/downloads/packages](https://www.eclipse.org/downloads/packages/).
- `releng` - _Abbreviation for **Rel**ease **Eng**ineering, contains important configuration_
    - **net.mograsim.configuration** <br> This contains the configuration for the 
      Mograsim project and the build in particular. The central Maven configuration 
      is located here in a single `pom.xml`, that is the super-parent of all others, 
      including the generated ones.
    - **net.mograsim.updatesite** <br> This updatesite project collects features 
      in form of an update site that can be accessed by Eclipse to install new features 
      or update features. The features are grouped into categories which can come 
      with further descriptions. The `target/repository/` is the part of the whole 
      Mograsim project that actually gets deployed. By using Help -> Install New 
      Software -> Add -> Local, the `repository`-folder can be selected and features 
      can be installed offline, e.g. to test them.
    - **net.mograsim.target** <br> The target definition for the build. This is a 
      well-defined environment that we expect the plugins to work in/with. That includes 
      all plugin dependencies (like all plugins the Eclipse Platform is comprised 
      of) and the (minimum) JRE that is expected. Some plugins exclusively for testing 
      are included here, too.
- `tests` - _This contains (integration-like) tests in form of plugin fragments_
    - **net.mograsim.logic.core.tests**
    - **net.mograsim.logic.model.am2900.tests**
    - **net.mograsim.machine.tests**
- `SWTHelper` - _This contains the [SWTHelper](https://github.com/Haspamelodica/SWTHelper) 
  git submodule._
    - `bundles` - _This submodule folder needs to be named like that for Tycho pom-less 
      build to work._
        - **SWTInput** - Contains type specific input fields for SWT.
        - **SWTObjectWrappers** - SWT object abstractions to enable zooming and optimizations.
        - <span style="color:grey">_SWTSystemInOutApplication_</span> - _unused_
        - <span style="color:grey">_SWTTiledZoomableCanvas_</span> - _unused_
        - **SWTZoomableCanvas** - The SWT canvas we draw the simulation in.

## Build Mograsim

Use the main aggregator `pom.xml` next to this markdown file to build mograsim, take 
care to use the correct JDK to run it (see Run Configuration).

A short guide to the [Maven goals (Maven Lifecycle)](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html):
- The `clean` goal will remove all `target`-folders and other generated files like 
  flattened poms and polyglot build artifacts (all are listed in `.gitignore`).
- The `validate` goal is useful to simply check if the setup itself (Maven Tycho) 
  is ok and everything needed is available.
- The `integration-test` goal will run the test plugins, the `test` goal beforehand 
  **will not.**
- I recommend running `clean verify` or `verify`, this will do everything up to and 
  including the testing.

## Challenges

Not everything is as simple as it seems at first glance.

### Maven Core Extension Problems

Context: Tycho 1.5.0 was not released, and thus required a workaround.
This Problem may appear again, maybe with a different core extension, so this section 
explains the problem, how to diagnose it and a fairly good workaround.

The Tycho extra `tycho-pomless` is a Maven core extension allows for simpler structure 
and less redundancy. Maven core extensions must be available at [the central maven repository](http://repo.maven.apache.org/maven2/) 
(or already in the local repository), you cannot specify an alternative remote repository 
in `.mvn/extensions.xml`. If a core extension cannot be resolved, you will get currently 
(Maven 3.6.1) only a warning like

> [WARNING] The POM for org.eclipse.tycho.extras:tycho-pomless:jar:1.5.0-SNAPSHOT is missing, no dependency information available

This means that the extension was not found, and it cannot be used, which leads to 
a failure later on. To get around that, create a dummy pom that only serves the purpose 
to "request" and resolve the extension:

```xml
<project .. bla .. bla ..>
    <modelVersion>4.0.0</modelVersion>
    <groupId>net.mograsim</groupId>
    <artifactId>net.mograsim.tycho-download</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <pluginRepositories>
        <!-- currently necessary because we are using a SNAPSHOT build of tycho -->
        <pluginRepository>
            <id>tycho-snapshots</id>
            <url>https://repo.eclipse.org/content/repositories/tycho-snapshots/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>

    <build>
        <extensions>
            <extension>
                <groupId>org.eclipse.tycho.extras</groupId>
                <artifactId>tycho-pomless</artifactId>
                <version>1.5.0-SNAPSHOT</version>
            </extension>
        </extensions>
    </build>
</project>
``` 
While that this is not the most compact way, it can be run by the developer and build 
server equally easy and does not require special CLI knowledge. As developer, you 
need to run that only once (in Eclipse: right click on pom.xml -> Run As -> Maven 
clean). For continuous integration, you can insert one more line in the YAML (or 
equivalent), like in our case ` - mvn $MAVEN_CLI_OPTS clean`.

### Git Submodules

Git submodules are a challenge with a tycho build, because the projects that reside 
in it need to be build, too. But not only that, they need to use the same configuration 
for the build, which is problematic if you do not have control over them. The solution 
only exists with Tycho 1.5.0 (currently only as snapshot), where deep folder structures 
are automatically scanned and poms get gnereated; not every folder requires an aggregator 
pom. This however **requires** (at the moment) certain naming conventions (see section 
on Tycho itself).

In our case, [SWTHelper](https://github.com/Haspamelodica/SWTHelper) is a git submodule 
containing several plain Java Eclipse projects with OSGi configuration (MANIFEST.MF), 
which is the reason this works at all. Due to the naming conventions, the submodule 
folder is named `bundles`.

### Maven incompatibility

Maven 3.6.2 is currently incompatible with Tycho <= 1.5.0.

If you encounter

> [FATAL] Non-readable POM "somepath"\tests\.polyglot.pom.tycho: input contained no data @ 

or

> [FATAL] Non-readable POM "somepath"\bundles\net.mograsim.logic.core\.polyglot..META-INF_MANIFEST.MF: input contained no data @ 

make sure to use Maven 3.6.1 to fix that (this is the default Eclipse embedded Maven 
at the moment).
