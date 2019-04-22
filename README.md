# Process Management

Process management simulation project for COSC 519.

## Goal

We'll be building a CLI tool that simulates process management. The user will be allowed to choose a few options on how the simulation will run:

* How many processes to generate.
* The range of time it will take the processes to run
* The maximum amount of IO calls a process can potentially make.
* The scheduling algorithm to use.

## Quick Start

This project uses Maven as it's central build tool. You can read quick overview of what Maven tries to accomplish [here](https://maven.apache.org/background/philosophy-of-maven.html) and a quick getting started guide on the tool [here](https://maven.apache.org/guides/getting-started/index.html). This project was built using the artifact ID `maven-archetype-quickstart`. This particular archetype is meant for bare bone applications that aim to help with the overall structure and build cycle of a simple project.

Maven will mainly be used to collect any external dependencies, build the final JAR file, and run any test suites we may need.

For example, you'll see the first external dependency JCommander located in the `pom.xml` file:

```xml
<dependency>
  <groupId>com.beust</groupId>
  <artifactId>jcommander</artifactId>
  <version>1.72</version>
</dependency>
```

This tells maven to look for `com.beust.commander` from the [Maven Central Repository](https://search.maven.org/) when building the JAR artifact. The Maven Central Repository is just a centralized store for any external dependencies needed in a Java project.

To get started with the project, you only need to run `mvn install`. This will run any tests, collect all dependencies, and output a JAR file in the `target/` directory. Ignore the rest of the folders in `target/` for now.
