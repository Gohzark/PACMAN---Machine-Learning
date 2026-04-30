package main;

import java.util.ArrayList;

import controller.GameController;
import motor.Game;
import motor.Maze;
import motor.PacmanGame;
import data.TrainingOutput;
import plot.PlotRunner;
import neuralNetwork.TrainExample;
import strategy.ApproximateQLearningStrategy;
import strategy.ApproximateQLearningStrategyWithNN;
import strategy.QLearningStrategy;
import strategy.DeepQLearningStrategy;
import strategy.TabularQLearning;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import view.View;

public class main_standardMode {

	public static void main(String[] args) {

		///// Paramétrage à modifier ici : choix du niveau et de la stratégie

		//// Choix du niveau
		int level = 2;

		//// Choix de la strategie du pacman

		int strategyID = 0;

		// 0 : Tabular_Qlearning
		// 1 : Approximate Qlearning with linear model
		// 2 : Approximate Qlearning with neural network
		// 3 : DeepQlearning

		// Mode nightMare : les fantômes ont une stratégie A* pour se diriger vers le
		// pacman
		boolean nightmareMode = true;

		// Mode visualization
		boolean visualizationMode = false;

		int maxTurnPacmanGame = -1;
		String chemin_maze = "";

		if (level == 0) {

			chemin_maze = "layout/level0.lay";
			maxTurnPacmanGame = 15;

		} else if (level == 1) {

			chemin_maze = "layout/level1.lay";
			maxTurnPacmanGame = 30;

		} else if (level == 2) {

			chemin_maze = "layout/level2.lay";
			maxTurnPacmanGame = 100;

		}

		Maze _maze = null;

		try {
			_maze = new Maze(chemin_maze);
		} catch (Exception e) {
			e.printStackTrace();
		}

		QLearningStrategy strat = null;

		double gamma = 0.95;
		double epsilon = 0.2;
		double learningRate = 1;

		int nbEpoch = 100;
		int batchSize = 100;

		if (strategyID == 0) {

			learningRate = 0.1;

			strat = new TabularQLearning(epsilon, gamma, learningRate, _maze.getSizeX(), _maze.getSizeY(),
					_maze.getNbWalls());

		} else if (strategyID == 1) {

			learningRate = 0.01;
			strat = new ApproximateQLearningStrategy(epsilon, gamma, learningRate, _maze.getSizeX(), _maze.getSizeY());

		} else if (strategyID == 2) {

			learningRate = 0.001;
			strat = new ApproximateQLearningStrategyWithNN(epsilon, gamma, learningRate, nbEpoch, batchSize,
					_maze.getSizeX(), _maze.getSizeY());

		} else if (strategyID == 3) {

			learningRate = 0.001;

			int range = 4;

			strat = new DeepQLearningStrategy(epsilon, gamma, learningRate, range, nbEpoch, batchSize, _maze.getSizeX(),
					_maze.getSizeY(), true, _maze.getNbWalls());

		}
		String version =  "level" + level + "_" + strat.toString() + "_version";

		// Nombre de simulations lancees en séquentiel en mode train
		int Ntrain = 100;

		// Nombre de simulations lancees en parallèle pour calculer la recompense
		// moyenne en mode test
		int Ntest = 100;
		int generation = 0;
		int maxGeneration = 100;

		// A changer pour ne pas écraser les résultats précédents
		int versionNb = 1;

		version += versionNb;
		String str_dir = "outputs/level" + level + "/" + strat.toString() + "/" + "version" + versionNb;
		PrintWriter writer = initializeCSV(str_dir, version, strat.toString(), level, gamma, epsilon, learningRate);
		while (generation < maxGeneration) {

			System.out.println("Generation : " + generation);

			// Joue N simulations du jeu en mode apprentissage
			strat.setModeTrain(true);
			System.out.println("Play and collect examples - train mode");
			TrainingOutput sortieTrain = play(Ntrain, maxTurnPacmanGame, chemin_maze, strat, nightmareMode);

			// Apprend a partir des exemples d'entrainement
			System.out.println("Learn model on " + sortieTrain.getExamples().size() + " training examples");
			strat.learn(sortieTrain.getExamples());
			strat.trainExamples.clear();

			// Evaluation du score moyen de la strategie
			strat.setModeTrain(false);
			System.out.println("Eval average score - test mode");
			float testScore = eval(Ntest, maxTurnPacmanGame, chemin_maze, strat, nightmareMode);
			writeScoreToCSV(writer, generation, sortieTrain.getAverageReward(), testScore);
			if (visualizationMode || (generation > maxGeneration-10)) {
				System.out.println("Visualization mode");
				vizualize(maxTurnPacmanGame, chemin_maze, strat, nightmareMode);
			}

			generation += 1;
		}
		try {
			PlotRunner.generate(
				java.nio.file.Path.of(str_dir),
				version
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writeScoreToCSV(PrintWriter writer, int generation, float trainScore, float testScore) {
		writer.println(generation + "," + trainScore + "," + testScore);
		writer.flush();
	}

	public static PrintWriter initializeCSV(String dirPath, String version, String strategy, int level, double gamma, double epsilon, double learningRate) {
		try {
			File dir = new File(dirPath);
			dir.mkdirs();
			File file_score = new File(dirPath + "/" + version + "_scores.csv");
			File file_config = new File(dirPath + "/" + version + "_config.json");
			PrintWriter writerScore = new PrintWriter(new FileWriter(file_score, false));
			PrintWriter writerConfig = new PrintWriter(new FileWriter(file_config, false));
			writerScore.println("generation,train_score,test_score");
			writerScore.flush();
			writerConfig.println("{");
			writerConfig.println("  \"strategy\": \"" + strategy + "\",");
			writerConfig.println("  \"level\": " + level + ",");
			writerConfig.println("  \"gamma\": " + gamma + ",");
			writerConfig.println("  \"epsilon\": " + epsilon + ",");
			writerConfig.println("  \"learningRate\": " + learningRate);
			writerConfig.println("}");
			writerConfig.flush();
			writerConfig.close();
			return writerScore;
		} catch (Exception e) {
			System.err.println("Error initializing CSV file: " + e.getMessage());
			return null;
		}
	}

	public static TrainingOutput play(int nbSimulations, int maxTurnPacmanGame, String chemin_maze,
			QLearningStrategy strat, boolean nightmareMode) {

		int globalReward = 0;

		for (int i = 0; i < nbSimulations; i++) {

			PacmanGame _motor = new PacmanGame(chemin_maze, maxTurnPacmanGame, (long) -1);
			_motor.initGameQLearning(strat, nightmareMode);

			_motor.launch();

			try {
				((Game) _motor).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			globalReward += _motor.getScore();

		}

		System.out.println("Average global reward - mode train: " + globalReward / nbSimulations);

		ArrayList<TrainExample> trainExamples = strat.trainExamples;
		return new TrainingOutput(trainExamples, (float) globalReward / nbSimulations);
	}

	public static float eval(int nbSimulations, int maxTurnPacmanGame, String chemin_maze, QLearningStrategy strat,
			boolean nightmareMode) {

		ArrayList<PacmanGame> pacmanGames = new ArrayList<PacmanGame>();

		for (int i = 0; i < nbSimulations; i++) {
			PacmanGame _motor = new PacmanGame(chemin_maze, maxTurnPacmanGame, (long) -1);
			_motor.initGameQLearning(strat, nightmareMode);
			pacmanGames.add(_motor);
		}

		for (int i = 0; i < nbSimulations; i++) {

			pacmanGames.get(i).launch();
		}

		for (int i = 0; i < nbSimulations; i++) {

			try {
				((Game) pacmanGames.get(i)).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int globalReward = 0;

		for (int i = 0; i < nbSimulations; i++) {
			globalReward += pacmanGames.get(i).getScore();
		}

		System.out.println("Average global reward - mode test : " + globalReward / nbSimulations);
		return globalReward / nbSimulations;
	}

	private static void vizualize(int maxTurnPacmanGame, String chemin_maze, QLearningStrategy strat,
			boolean nightmareMode) {

		PacmanGame _motor = new PacmanGame(chemin_maze, maxTurnPacmanGame, (long) 200);
		GameController controller = GameController.getInstance(_motor);
		View _view = View.getInstance(controller, _motor, false);

		_motor.initGameQLearning(strat, nightmareMode);

		_view.btnRun.setEnabled(false);
		_view.btnPause.setEnabled(true);

		controller._motor = _motor;

		_view._motor = _motor;
		_view.btnRun.setEnabled(false);
		_view.btnPause.setEnabled(true);
		_motor.addObserver(_view);

		_motor.launch();

		try {
			((Game) _motor).join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
