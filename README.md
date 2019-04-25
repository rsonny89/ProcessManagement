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

### Run Project

In order to run the current build of the project, simply run `mvn exec:java`. This will run the tool with the default options.

You can pass in CLI options by executing the command `mvn exec:java -Dexec.args="--help"`. This should print the following:

```text
Usage: <main class> [options]
  Options:
    --bottom, -b
      Lower limit of the process runtime value
      Default: 1
    --count, -c
      Number of processes to create
      Default: 10
    --help
      Prints possible options for the CLI tool
    -i, --interactive
      Runs the scheduler in interactive mode
      Default: false
    --io-bottom
      Lowest number of cycles a process will wait for IO
      Default: 1
    --io-calls
      Maximum number of IO calls a process can make
      Default: 2
    --io-top
      Maximum number of cycles a process will wait for IO
      Default: 10
    -p, --print-info
      If the scheduling info should be shown in non interactive mode
      Default: false
    --shortest
      Runs the shortest job scheduler
      Default: false
    --top, -t
      Upper limit of the process runtime value
      Default: 10
    -w, --wait
      Time in milliseconds each cycle should take
      Default: 500

```

By default, the program runs the first come first served scheduler.

When using the tool for the first time, it's recommended to use the interactive mode. This is done by typing the command `mvn exec:java -Dexec.args="-i"`. You should see something appear like this:

```text
Creating 10 processes

Cycle Action    PID Type
    1 Interrupt 1   NEW  

[INFO] =================
Order PID Remaining
    - -   -
    0 1   2
========================
```

To continue, simply press enter:

```text
Creating 10 processes

Cycle Action    PID Type
    1 Interrupt 1   NEW  

[INFO] =================
Order PID Remaining
    - -   -
    0 1   2
========================

    2 Schedule  1        

[INFO] =================
Order PID Remaining
    - 1   2
========================

```

This mode walks you through how the scheduler operates on each cycle and displays the current ready queue order based on the scheduling algorithm. Keep pressing enter to walk through each step until it's down or press `ctrl-c` to exit.

At the end of each finished cycle, it shows a calculation of the amount of CPU utilized:

```text
...
  106 Wait               
  107 Wait               
  108 Interrupt 10  IO   
  109 Schedule  10       
  110 Run       10       
  111 Run       10  END  

--- STATISTICS ---
CPU Utilization: 97
```

In this case, CPU utilization was quite high, but that changes if we set the IO wait times to be higher `mvn exec:java -Dexec.args="-w 0 --io-top 50 --io-bottom 30"`:

```text
...
  137 Wait               
  138 Wait               
  139 Interrupt 8   IO   
  140 Schedule  8        
  141 Run       8        
  142 Run       8   END  

--- STATISTICS ---
CPU Utilization: 53
```

CPU utilization drops significantly with an increase in IO calls and IO wait time. Of course, this is also a luck of the draw to get these results because the amount of wait time is randomized and the number of IO calls are randomized, but on average the CPU utilization is lower.
