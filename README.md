# CPU Scheduling Comparator

## Overview

This project is a desktop application developed in Java to simulate and compare two CPU scheduling algorithms:

* Round Robin (RR)
* Shortest Remaining Time First (SRTF)

The application provides a graphical user interface (GUI) that allows users to:

* Add and manage processes dynamically
* Execute scheduling algorithms
* Display process metrics
* Visualize execution using Gantt Charts
* Compare algorithm performance

The project was built as part of an Operating Systems course.

---

# Features

## Process Management

* Add new processes dynamically
* Remove existing processes
* Reset all inputs to default values
* Validate process input values

## Scheduling Algorithms

### Round Robin (RR)

* Uses configurable time quantum
* Supports process preemption
* Calculates:

  * Completion Time
  * Turnaround Time
  * Waiting Time
  * Response Time

### Shortest Remaining Time First (SRTF)

* Preemptive scheduling algorithm
* Always selects the process with the shortest remaining burst time
* Calculates all scheduling metrics automatically

## Visualization

* Interactive tables for scheduling results
* Gantt chart visualization for process execution order
* Algorithm comparison panel

## Comparison Metrics

The system compares:

* Average Turnaround Time
* Average Waiting Time
* Average Response Time

---

# Technologies Used

* Programming Language: Java
* GUI Framework: Java Swing
* Graphics & Layouts: Java AWT
* IDE: IntelliJ IDEA / Eclipse / NetBeans (any Java IDE)

---

# Project Structure

```text
OS_PROJECT/
│
├── os/
│   ├── Main.java
│   ├── SchedulerGUI.java
│   ├── RRAlgorithm.java
│   ├── SRTFAlgorithm.java
│   ├── ProcessData.java
│   ├── SchedulingResult.java
│   └── GanttSlot.java
│
└── README.md
```

---

# File Descriptions

## Main.java

Program entry point.

## SchedulerGUI.java

Responsible for:

* Building the graphical user interface
* Handling user interaction
* Displaying tables and Gantt charts
* Comparing algorithms

## RRAlgorithm.java

Implements the Round Robin scheduling algorithm.

## SRTFAlgorithm.java

Implements the Shortest Remaining Time First scheduling algorithm.

## ProcessData.java

Stores process information such as:

* Process ID
* Arrival Time
* Burst Time
* Completion Time
* Waiting Time
* Turnaround Time
* Response Time

## SchedulingResult.java

Stores scheduling results and statistics.

## GanttSlot.java

Represents a single slot in the Gantt chart.

---

# How to Run

## Requirements

* Java JDK 8 or later
* Any Java IDE or terminal

## Run Using IDE

1. Open the project in your IDE.
2. Navigate to `Main.java`.
3. Run the file.

## Run Using Terminal

Compile the project:

```bash
javac os/*.java
```

Run the application:

```bash
java os.Main
```

---

# How to Use

1. Enter process information:

   * Process ID
   * Arrival Time
   * Burst Time

2. Set the Round Robin time quantum.

3. Choose one of the following:

   * Run RR
   * Run SRTF
   * Compare Both

4. View:

   * Scheduling tables
   * Gantt charts
   * Performance comparison

---

# Scheduling Metrics

## Turnaround Time (TAT)

Time taken from process arrival until completion.

## Waiting Time (WT)

Total time a process spends waiting in the ready queue.

## Response Time (RT)

Time from arrival until first CPU allocation.

---

# GUI Preview

The GUI contains:

* Input Section
* Results Tables
* Gantt Chart Panels
* Comparison Section

The interface uses a modern dark-theme design implemented using Java Swing.

---

# Sample Input

| Process | Arrival Time | Burst Time |
| ------- | ------------ | ---------- |
| P1      | 0            | 5          |
| P2      | 1            | 3          |
| P3      | 2            | 8          |

Time Quantum = 2


---

# Learning Objectives

This project helps demonstrate:

* CPU Scheduling Concepts
* Preemptive Scheduling
* Process Management
* GUI Development in Java
* Data Visualization
* Algorithm Performance Analysis

---

# License

This project is for educational purposes only.
