# SmartCityX — Integrated Smart-City Analytics Platform

> **Comprehensive Data Structures & Algorithms (DSA-3) Project**  
> **Course Outcomes Mapped:** CO1 (Strings), CO2 (Suffix Structures), CO3 (Advanced DP), CO4 (Network Flow), CO5 (NP-Completeness & Approximation), CO6 (Parallel & Randomized Algorithms).  
> **Architecture:** React city dashboard backed by a structured Java data service and the 100% Zero-`java.util.*` Collection Engine (Custom Dynamic Arrays, HashMaps, Heaps, Stacks, Queues, Disjoint Set Union).

---

## 🏙️ 1. Project High-Level Overview

**SmartCityX** is an enterprise-grade, integrated smart-city analytics platform modeling municipal decision-making, emergency response, and utility distribution for modern metropolitan environments:

1. **Transportation & Traffic Flow:** SCADA signal overrides, flyover congestion analysis, emergency transit corridors.
2. **Water & Energy Utilities:** Electrical grid load balancing, water pressure regulation, aqueduct flow networks, and bottleneck cut identification.
3. **Public-Service Requests & Emergency Dispatch:** 24/7 hotline incident intake, multi-keyword automated triage, and optimal emergency vehicle routing.
4. **City Infrastructure Documents & Operational Bylaws:** High-speed document indexing, common clause clustering, and zoning regulation search.
5. **Continuous IoT & Sensor Telemetry:** Unbounded sensor streaming, air quality index (AQI) monitoring, and parallel consumption prefix aggregation.

---

## 📚 2. Complete Syllabus & Module Mapping (CO1 – CO6)

| Module | Syllabus Domain | Implemented Algorithms | Municipal Smart-City Application |
| :--- | :--- | :--- | :--- |
| **M1 (CO1)** | **String Algorithms** | • KMP Matcher<br>• Z-Algorithm<br>• Rabin-Karp<br>• Aho-Corasick<br>• **Bittu's Algorithm (Invention)** | Searching citizen reports, identifying repeated error codes, rolling hash asset queries, multi-keyword emergency triage. |
| **M2 (CO2)** | **Suffix Structures** | • Suffix Array (Prefix Doubling)<br>• SA-IS Linear Suffix Array<br>• Kasai's LCP Array<br>• Suffix Automaton (SAM) | Large-scale indexing of municipal zoning bylaws, finding duplicated service tickets, $O(M)$ substring verification. |
| **M3 (CO3)** | **Advanced Dynamic Programming** | • Levenshtein / Damerau DP<br>• Bitmask DP (TSP)<br>• Matrix-Chain Multiplication<br>• 0/1 Subset Knapsack DP | Typo-tolerant citizen search with 2D ASCII matrix backtrace, optimal emergency patrol tour, optimizing analytics tensor matrix chains, project selection. |
| **M4 (CO4)** | **Network Flow** | • Ford-Fulkerson<br>• Edmonds-Karp ($O(VE^2)$)<br>• Dinic's Algorithm ($O(V^2E)$)<br>• Max-Flow Min-Cut<br>• Bipartite Matching | Municipal electrical transmission, water pipeline capacity, isolating critical infrastructure bottlenecks, crew assignment. |
| **M5 (CO5)** | **NP-Completeness & Approximation** | • DPLL 3-SAT Solver<br>• 3-SAT $\to$ CLIQUE<br>• CLIQUE $\to$ IS $\to$ VC<br>• Vertex-Cover 2-Approximation | Verifying municipal zoning constraints, computational conflict reduction pipeline, optimal surveillance camera placement. |
| **M6 (CO6)** | **Parallel & Randomized** | • Randomized QuickSort<br>• Reservoir Sampling ($O(k)$)<br>• Miller-Rabin Primality<br>• Blelloch Parallel Scan<br>• Parallel Reduce<br>• Brent's Theorem Engine | Priority ranking of emergency calls, sampling unbounded IoT streams, secure sensor authentication, cumulative power consumption. |

---

## 🌟 3. Special Innovation: Bittu's Algorithm

**Bittu's Algorithm** is an innovative rolling bigram cosine vector matching algorithm designed for high-throughput string filtering with zero garbage collection allocations.

* **Vector Space:** Maps strings into a $65,536$-dimensional state vector space ($256 \times 256$ Extended-ASCII bigram table).
* **$O(1)$ Sliding Update Algebra:**
  $$\Delta(\text{dot}) = -P[\text{leavingKey}] + P[\text{enteringKey}]$$
  $$\Delta(\|W\|^2) = -(2 \cdot c_{\text{oldLeave}} - 1) + (2 \cdot c_{\text{oldEnter}} + 1)$$
* **Memory Footprint:** Evaluated entirely on flat primitive `int[]` arrays with zero heap allocations during the sliding window scan.

---

## 🚀 4. How to Compile & Run

### Prerequisites
* Java JDK 8 or higher (JDK 17/21 recommended).
* Node.js and npm to build the React web interface.

### One-Click Launch
Double-click `start.bat`. It builds the React interface, compiles the Java sources, starts the local city-data service, and opens the dashboard in your browser. The launcher selects an available local port between 8080 and 8090.

### Terminal Commands
1. **Launch the Web Interface:**
   ```powershell
   .\start.bat
   ```
2. **Launch the Interactive Terminal CLI:**
   ```powershell
   java -cp bin Main
   ```
3. **Run Automated 17-Algorithm Verification Suite (Headless Test):**
   ```powershell
   java -cp bin Main --test
   ```

The dashboard automatically fetches the project dataset when it opens: 10 example citizen reports, infrastructure assets, all sensor readings, response crews, and city documents. The sidebar opens dedicated dashboards for each area, with searchable tables, summaries, and visualizations. The web interface does not run Java demos or display terminal/code output.

The Citizen Reports page shows 10 representative sample incidents and supports new submissions with automatic dispatch to an available matching response team or manual team selection. Unassigned reports can also be assigned from the report list. Reports can be deleted with confirmation; deleting a dispatched report releases its assigned team. New reports, assignments, and deletions stay in memory while the local server is running and reset when it is restarted.

The project dataset is generated by `DatasetLoader` and is sample/synthetic data, not a live municipal feed.

To work on the React app separately, run `npm install` and `npm run dev` inside `web`; the Vite development server proxies dashboard requests to the Java data service.

---

## 📂 5. Project Directory Structure

```
C:\SmartCityX\
├── bin\                                # Compiled bytecode classes
├── DOCS\
│   ├── DSA_THEORY_GUIDE.md             # Complete mathematical theory & Big-O proofs
│   └── VIVA_DEFENSE_QUESTIONS.md       # Top 25 Viva / Oral defense questions with model answers
├── src\
│   ├── Main.java                       # Root forwarder entrypoint
│   └── smartcityx\
│       ├── Main.java                   # Primary CLI launcher
│       ├── web\
│       │   └── SmartCityXWebServer.java # Local React host and structured city-data API
│       ├── core\                       # Custom Zero-java.util Collection Engine
│       │   ├── MyArrayList.java
│       │   ├── MyLinkedList.java
│       │   ├── MyHashMap.java
│       │   ├── MyMinHeap.java
│       │   ├── MyStack.java
│       │   ├── MyQueue.java
│       │   ├── DisjointSetUnion.java
│       │   └── Pair.java
│       ├── model\                      # SmartCityX Domain Entities
│       │   ├── CitizenReport.java
│       │   ├── CityInfrastructure.java
│       │   ├── IoTSensorReading.java
│       │   ├── ServiceCrew.java
│       │   ├── UtilityNetworkNode.java
│       │   └── CityDocument.java
│       ├── algorithms\
│       │   ├── strings\                # M1: String Algorithms
│       │   ├── suffix\                 # M2: Suffix Structures
│       │   ├── dp\                     # M3: Advanced Dynamic Programming
│       │   ├── flow\                   # M4: Network Flow
│       │   ├── np\                     # M5: NP-Completeness & Reductions
│       │   └── parallel_random\        # M6: Randomized & Parallel
│       ├── service\                    # Smart City Analytics Services
│       │   ├── CivicSearchService.java
│       │   ├── DocumentIntelligenceService.java
│       │   ├── UrbanResourcePlanningService.java
│       │   ├── UtilityGridFlowService.java
│       │   ├── InfrastructureSurveillanceService.java
│       │   ├── IoTStreamAnalyticsService.java
│       │   └── BenchmarkArenaService.java
│       ├── io\                         # Data Ingestion & Synthetic Telemetry
│       │   └── DatasetLoader.java
│       └── ui\                         # User Interfaces
│           └── SmartCityXCLI.java      # Rich Interactive Terminal CLI
├── web\
│   ├── src\                            # React module pages and styles
│   ├── package.json                    # React/Vite dependencies
│   └── index.html                      # Web application entry
├── DEMO_WALKTHROUGH.md                 # Complete click-by-click CLI demo guide
├── start.bat                           # One-click start script
├── run.bat                             # One-click run script
└── README.md
```
