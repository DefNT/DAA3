import csv
from pathlib import Path

import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[2]
CSV_FILE = ROOT / "results" / "tables" / "benchmark_results.csv"
PLOT_DIR = ROOT / "results" / "plots"

PLOT_SERIES = [
    ("Kruskal", "UNDIRECTED_CONNECTED", "Kruskal"),
    ("Prim", "UNDIRECTED_CONNECTED", "Prim"),
    ("Dijkstra", "UNDIRECTED_CONNECTED", "Dijkstra"),
    ("SCC", "CYCLIC", "Kosaraju SCC (cyclic graph)"),
    ("TopologicalSort", "DAG", "Topological sort (DAG)"),
]


def read_rows():
    if not CSV_FILE.exists():
        raise SystemExit(
            "Benchmark CSV not found"
        )

    with CSV_FILE.open("r", newline="", encoding="utf-8") as file:
        return list(csv.DictReader(file))


def make_plot(rows, experiment, x_field, x_label, output_name):
    plt.figure(figsize=(9, 5.5))

    for algorithm, graph_type, label in PLOT_SERIES:
        points = []

        for row in rows:
            if (
                    row["experiment"] == experiment
                    and row["algorithm"] == algorithm
                    and row["graph_type"] == graph_type
            ):
                x_value = int(row[x_field])
                y_value = float(row["average_time_ns"]) / 1_000_000.0
                points.append((x_value, y_value))

        if points:
            points.sort(key=lambda point: point[0])
            plt.plot(
                [point[0] for point in points],
                [point[1] for point in points],
                marker="o",
                label=label,
            )

    plt.xlabel(x_label)
    plt.ylabel("Average execution time (milliseconds)")
    plt.title("Algorithm execution time vs. " + x_label.lower())
    plt.grid(True, alpha=0.3)
    plt.legend()
    plt.tight_layout()
    plt.savefig(PLOT_DIR / output_name, dpi=160)
    plt.close()


def main():
    PLOT_DIR.mkdir(parents=True, exist_ok=True)
    rows = read_rows()

    make_plot(
        rows,
        "SIZES",
        "vertices",
        "Number of vertices (V)",
        "time_vs_vertices.png",
    )

    make_plot(
        rows,
        "EDGES",
        "edges",
        "Number of edges (E), with V fixed at 1000",
        "time_vs_edges.png",
    )

    print("Plots saved in", PLOT_DIR)


if __name__ == "__main__":
    main()
