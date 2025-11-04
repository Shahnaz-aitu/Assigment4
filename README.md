Assignment 4 Report — Smart City Scheduling
1. Introduction

This project integrates two key graph analysis topics — Strongly Connected Components (SCC) and Shortest Paths in Directed Acyclic Graphs (DAGs) — into a single applied case for Smart City / Smart Campus Scheduling.
The scenario represents scheduling of interdependent tasks such as maintenance, cleaning, or sensor operations. The algorithms help detect cyclic dependencies, order independent tasks efficiently, and find optimal and critical task sequences.

2. Implementation Overview
Structure

The project is organized into modular Java packages:

graph.model – Base classes for Graph and Condensation.

graph.scc – Implementation of Tarjan’s SCC algorithm.

graph.topo – Implementation of Kahn’s Topological Sort.

graph.dagsp – Shortest and longest path algorithms for DAGs.

graph.metrics – Collects performance metrics (DFS visits, relaxations, timings).

Workflow

Read input graph from a JSON dataset (/data/data1.json).

Construct a directed weighted graph.

Apply Tarjan’s algorithm to find SCCs.

Build a condensation DAG (each SCC becomes a single node).

Compute a topological order using Kahn’s algorithm.

Calculate shortest paths (from a given source node).

Compute the longest path to find the critical path.

Print metrics and results for analysis.

3. Dataset Description

For this assignment, a single dataset was used to validate the algorithms:

{
  "directed": true,
  "n": 6,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 0, "v": 2, "w": 2},
    {"u": 1, "v": 3, "w": 4},
    {"u": 2, "v": 3, "w": 1},
    {"u": 3, "v": 4, "w": 5},
    {"u": 4, "v": 5, "w": 2}
  ],
  "source": 0,
  "weight_model": "edge"
}


Nodes: 6

Edges: 6

Graph type: Directed Acyclic Graph (DAG)

Weight model: Edge weights

Source node: 0

This dataset models a chain of dependent tasks where no cycles exist — ideal for verifying correctness of SCC detection, topological ordering, and path computations.

4. Results Summary
SCC Detection

Algorithm: Tarjan’s

Result: Each vertex forms its own SCC (graph is acyclic).

SCC count: 6

Condensation graph: identical to original DAG.

Topological Sort

Algorithm: Kahn’s

Order of components: [0, 1, 2, 3, 4, 5]

Derived order of original tasks: identical.

Shortest Paths (from source = 0)
Target	Distance	Path
0	0	[0]
1	3	[0 → 1]
2	2	[0 → 2]
3	3	[0 → 2 → 3]
4	8	[0 → 2 → 3 → 4]
5	10	[0 → 2 → 3 → 4 → 5]
Longest Path (Critical Path)

Path: 0 → 1 → 3 → 4 → 5

Total length: 14

Interpretation: Represents the critical chain of tasks that determines total project completion time.

5. Metrics Summary
Metric	Description	Count
DFS Visits	During Tarjan’s SCC	6
Edge Relaxations	During DAG shortest path	6
Queue Operations	During Kahn’s sort	6
Execution Time	Full pipeline runtime	< 1 ms (small dataset)
6. Analysis

The dataset confirmed correct SCC detection (no cycles found).

Condensation produced a clean DAG suitable for path algorithms.

Topological ordering was stable and consistent.

Shortest path results verified correctness of dynamic programming logic.

The critical path correctly identified the longest execution chain.

Performance was excellent for small graphs, with potential to scale to larger datasets for performance testing (medium and large graphs).

7. Conclusions

This project successfully met all the assignment requirements:

Implemented SCC detection, condensation, topological sorting, and DAG shortest/longest path algorithms.

Achieved modular, testable Java code with clear structure.

Verified correctness on a working dataset.

Collected performance metrics and validated execution efficiency.

Key insights:

SCC compression is vital for handling cyclic dependencies.

Topological sorting provides efficient task sequencing for DAGs.

Shortest and longest path computations help in identifying optimal and critical schedules in real-world smart city operations.

Future improvements:

Add multiple datasets (small, medium, large) with various densities.

Expand the metrics collection for comparative performance analysis.

Integrate visualization for graph structures and paths.
