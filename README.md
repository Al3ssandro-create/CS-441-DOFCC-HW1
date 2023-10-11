# Scala Hadoop MapReduce Program
Alessandro Martinolli    
mail:[amart409@uic.edu](amart409@uic.edu)   
youtube video:[https://youtu.be/xvdkpopnPFg](https://youtu.be/xvdkpopnPFg)   
## Overview

This is a Scala-based Hadoop MapReduce program designed for processing graph data. The program aims to analyze nodes from two graphs, generating results based on a specific comparison logic. It's structured in the `com.lsc` package and uses MapReduce for distributed processing.

## Features

- **Node Combination Generator**: 
  - Generates all possible node combinations between two graphs.
  
- **Hadoop MapReduce Job**: 
  - Processes node combinations, apply analysis logic and produces outputs.

## Modules

### 1. `Main`

- Acts as the main driver.
- Sets up and executes the Hadoop MapReduce job.

### 2. `MapReduce`

- Contains the Mapper and Reducer definitions.
  
### 3. `FileManager`

- Manages file operations for creating and handling shards.
  
### 4. `Parser`

- Handles node parsing logic.
- Calculates node similarities.

### 5. `Comparison`

- Computes required statistics.
- Analyzes the reducer output and YAML input to produce various metrics.

## Usage

1. Ensure your Hadoop environment is up and running.
2. Clone the repository
3. Compile and run the program using SBT: 
   ```bash
   sbt clean compile
   sbt "run <input dir> <output dir>"

4. Compile and test the program using SBT:
    ```bash
   sbt clean test
    
5. Run the main method in Comparison class.   
_Note: The JAR for this program is already included in the repository._
## Additional Information

- The program includes commented sections that indicate prior workflows and logic. These sections can be uncommented based on requirements.
- Logging is facilitated through `slf4j`, ensuring detailed logs regarding the status of graph loading, shard creation, and MapReduce job execution.
