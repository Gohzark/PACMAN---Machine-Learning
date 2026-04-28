package strategy;

import java.util.ArrayList;
import agent.AgentAction;
import motor.PacmanGame;
import neuralNetwork.TrainExample;

import java.util.HashMap;


public class TabularQLearning extends QLearningStrategy{


	HashMap<String, double[]> QTable;



	int sizeMazeX;
	int sizeMazeY;




	public TabularQLearning( double epsilon, double gamma, double learningrate,  int sizeMazeX, int sizeMazeY, int nbWalls) {
		
		super( epsilon, gamma, learningrate, sizeMazeX, sizeMazeY);

		this.sizeMazeX = sizeMazeX;
		this.sizeMazeY = sizeMazeY;

		System.out.println("sizeX labyrinth " + this.sizeMazeX);
		System.out.println("sizeY labyrinth " + this.sizeMazeY);
		
		int numberCellsWithoutWall = sizeMazeX*sizeMazeY - nbWalls;
				
		System.out.println("NumberCells without wall " + numberCellsWithoutWall);

		int numberStates =  (int) Math.pow( 4, numberCellsWithoutWall);

		System.out.println("Max number different states " + numberStates);

		QTable = new HashMap<>();


	}

	public String encodeState(PacmanGame pacmanGame) {
		int x = pacmanGame.getMaze().getSizeX();
		int y = pacmanGame.getMaze().getSizeY();
		String encodeState = "";
		for (int i=0; i<x; i++) {
			for (int j=0; j<y; j++) {
				if (pacmanGame.isCapsuleAtPosition(i, j)) {
					encodeState+="c";
				}
				else if (pacmanGame.isGumAtPosition(i, j)) {
					encodeState+="g";
				}
				else if (pacmanGame.isWallAtPosition(i, j)) {
					encodeState+="w";
				}
				else if (pacmanGame.isGhostAtPosition(i, j)) {
					encodeState+="G";
				}
				else if (pacmanGame.isPacmanAtPosition(i, j)) {
					encodeState+="P";
				}
				else {
					encodeState+="e";
				}
			}
		}
		return encodeState;
	} 


	@Override
	public AgentAction chooseAction(PacmanGame state) {
		String encodedState = encodeState(state);
		QTable.putIfAbsent(encodedState, new double[5]);
		ArrayList<AgentAction> legalActions = state.getLegalPacmanActions();
		//exploration
		if(Math.random() < this.current_epsilon) {
			return state.getRandomAllowedAction(legalActions);
		}
		//exploitation 
		else {
			double maxval = Double.NEGATIVE_INFINITY;
			int bestAction = 0;
			for (int i=0; i<legalActions.size(); i++) {
				if (this.QTable.get(encodedState)[legalActions.get(i).get_idAction()] > maxval) {
					maxval = this.QTable.get(encodedState)[legalActions.get(i).get_idAction()];
					bestAction = legalActions.get(i).get_idAction();
				}
			}
			return new AgentAction(bestAction);
		}
	}

	@Override
	public void update(PacmanGame state, PacmanGame nextState, AgentAction action, double reward, boolean isFinalState) {
		String encodedState = encodeState(state);
		String encodedNextState = encodeState(nextState);
		QTable.putIfAbsent(encodedState, new double[5]);
    	QTable.putIfAbsent(encodedNextState, new double[5]);
		double maxNextQ = Double.NEGATIVE_INFINITY;
		ArrayList<AgentAction> legalActions = state.getLegalPacmanActions();
		for (AgentAction q : legalActions) {
			if (QTable.get(encodedNextState)[q.get_idAction()] > maxNextQ) {
				maxNextQ = QTable.get(encodedNextState)[q.get_idAction()];
			}
		} 
		this.QTable.get(encodedState)[action.get_idAction()] += this.learningRate * (reward + this.gamma * maxNextQ - this.QTable.get(encodedState)[action.get_idAction()]);	
	}

	public void learn(ArrayList<TrainExample> trainExamples) {

	}

	@Override
	public String toString() {
		return "Tabular Q-Learning";
	}
}
