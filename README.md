🐝 Artificial Bee Colony (ABC) Algorithm Visualizer

An interactive JavaFX-based visualization of the Artificial Bee Colony (ABC) optimization algorithm, designed for educational and experimental purposes.
This project demonstrates how swarm intelligence techniques explore complex search spaces using the Rastrigin function as a benchmark.

📌 Project Overview

The Artificial Bee Colony (ABC) algorithm is a population-based metaheuristic inspired by the foraging behavior of honey bees.
This project provides:

A complete implementation of the ABC algorithm

A real-time visualization of bee behaviors and solution convergence

An educational tool to understand exploration vs. exploitation in swarm intelligence

The system visually represents:

Food sources (candidate solutions)

Employed, onlooker, and scout bee behaviors

Fitness landscape of the Rastrigin function

Convergence dynamics over iterations

🎯 Objectives

Implement the ABC algorithm for 2D optimization problems

Optimize the Rastrigin function, a highly multimodal benchmark

Visualize algorithm phases and swarm dynamics in real time

Enable parameter experimentation (food count, limit, neighborhood radius)

Provide performance analytics (best fitness, average fitness, abandoned sources)

🧠 Algorithm Description
Artificial Bee Colony Phases

Employed Bee Phase
Each employed bee explores the neighborhood of its associated food source and applies greedy selection.

Onlooker Bee Phase
Onlooker bees probabilistically select food sources based on fitness and perform local exploration.

Scout Bee Phase
Food sources that fail to improve after a given limit are abandoned and replaced with random solutions.

Fitness Function

The algorithm optimizes the Rastrigin function:

𝑓
(
𝑥
,
𝑦
)
=
20
+
(
𝑥
2
−
10
cos
⁡
(
2
𝜋
𝑥
)
)
+
(
𝑦
2
−
10
cos
⁡
(
2
𝜋
𝑦
)
)
f(x,y)=20+(x
2
−10cos(2πx))+(y
2
−10cos(2πy))

Since ABC is a maximization algorithm, the function is transformed as:

fitness = max(0, 100 - min(100, f(x, y)))


Higher fitness values indicate better solutions.

🖥️ Visualization Features

Color-coded food sources

🟢 Green: high fitness

🔴 Red: low fitness

Circle size proportional to fitness

Neighborhood radius visualization

Trial counter display for near-abandoned sources

Live statistics panel

Best fitness

Average fitness

Abandoned food sources

Phase indicator (Employed / Onlooker / Scout)

📁 Project Structure
src/main/java/
 └── com/example/abc_algorithm/
     ├── algo/
     │   ├── ABCAlgorithm.java
     │   ├── FoodSource.java
     │   ├── Bee.java
     │   └── Utils.java
     ├── HelloController.java
     ├── HelloApplication.java
     └── Launcher.java

src/main/resources/
 └── hello-view.fxml

⚙️ Requirements

Java Development Kit (JDK) 8 or higher

JavaFX SDK

IDE such as IntelliJ IDEA or Eclipse (recommended)

▶️ How to Run

Clone the repository:

git clone https://github.com/your-username/abc-algorithm-visualizer.git


Open the project in your IDE

Make sure JavaFX is correctly configured

Run Launcher.java

Click Start ABC Visualization to begin the simulation

🔧 Adjustable Parameters
Parameter	Description
Food Source Count	Number of candidate solutions
Trial Limit	Max unsuccessful trials before abandonment
Neighborhood Radius	Local search range
Speed	Controls animation speed
📊 Educational Value

This project is ideal for:

Learning swarm intelligence algorithms

Understanding metaheuristic optimization

Visualizing exploration vs. exploitation

Academic demonstrations and coursework

👩‍💻 Authors

Hiba Ouhmad

Khadija Lekbiri
Computer Science – UM6P
December 2025

📄 License

This project is intended for educational and academic use.
You may reuse or modify it with proper attribution.
