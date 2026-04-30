import sys
import os
import json
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

def load_config(config_path):
    if not os.path.isfile(config_path):
        return None
    with open(config_path) as f:
        return json.load(f)

def config_to_text(config):
    if config is None:
        return ""
    return "\n".join(f"{k}: {v}" for k, v in config.items())

def plot_score(ax, x, y, label, color, config_text):
    ax.plot(x, y, marker="o", markersize=3, linewidth=1.8, color=color, label=label)
    ax.set_xlabel("Generation")
    ax.set_ylabel("Score")
    ax.set_title(label)
    ax.legend()
    ax.grid(True, linestyle="--", alpha=0.5)
    if config_text:
        ax.text(
            1.02, 0.5, config_text,
            transform=ax.transAxes,
            fontsize=8, verticalalignment="center",
            bbox=dict(boxstyle="round", facecolor="lightyellow", alpha=0.8)
        )

def main():
    if len(sys.argv) != 4:
        print("Usage: python3 plot_scores.py <input.csv> <input_config.json> <plot_scores.png>")
        sys.exit(1)

    csv_path = sys.argv[1]
    config_path = sys.argv[2]
    plot_scores_path = sys.argv[3]

    if not os.path.isfile(csv_path):
        print(f"Erreur : fichier introuvable : {csv_path}")
        sys.exit(1)

    config = load_config(config_path)
    config_text = config_to_text(config)

    df = pd.read_csv(csv_path)
    required_cols = {"generation", "train_score", "test_score"}
    missing = required_cols - set(df.columns.str.strip().str.lower())
    if missing:
        print(f"Erreur : colonnes manquantes dans le CSV : {missing}")
        sys.exit(1)

    df.columns = df.columns.str.strip().str.lower()

    fig, ax = plt.subplots(figsize=(10, 5))
    plot_score(ax, df["generation"], df["train_score"], "Train Score", "#2196F3", config_text)
    plot_score(ax, df["generation"], df["test_score"], "Test Score", "#FF5722", config_text)
    ax.legend()
    fig.tight_layout()

    fig.savefig(plot_scores_path, dpi=150, bbox_inches="tight")
    plt.close(fig)
    print(f"Saved: {plot_scores_path}")

if __name__ == "__main__":
    main()