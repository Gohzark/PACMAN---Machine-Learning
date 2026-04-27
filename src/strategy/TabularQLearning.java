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




	public TabularQLearning( double epsilon, double gamma, double alpha,  int sizeMazeX, int sizeMazeY, int nbWalls) {
		
		super( epsilon, gamma, alpha, sizeMazeX, sizeMazeY);

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



	//TODO
	@Override
	public AgentAction chooseAction(PacmanGame state) {
		String encodedState = encodeState(state);
		QTable.putIfAbsent(encodedState, new double[4]);
		ArrayList<AgentAction> legalActions = state.getLegalPacmanActions();
		#exploration
		if(Math.random()< this.current_epsilon) {
			return state.getRandomAllowedAction(legalActions);
		}
		#exploitation 
		else {
			double maxval = this.QTable.get(encodedState)[0];
			int bestAction = 0;
			for (int i=1; i<legalActions.length; i++) {
				if (this.QTable.get(encodedState)[i] > maxval) {
					maxval = this.QTable.get(encodedState)[legalActions[i].get_idAction()];
					bestAction = legalActions[i].get_idAction();
				}
			}
			return new AgentAction(bestAction);
		}
	}

	@Override
	public void update(PacmanGame state, PacmanGame nextState, AgentAction action, double reward, boolean isFinalState) {
		String encodedState = encodeState(state);
		String encodedNextState = encodeState(nextState);
		double maxNextQ = this.QTable.get(encodedNextState)[0];
		for (double q : this.QTable.get(encodedNextState)) {
			if (q > maxNextQ)
				maxNextQ = q;
		} 
		this.QTable.get(encodedState)[action.get_idAction()] += this.learning_rate * (reward + this.gamma * maxNextQ - this.QTable.get(encodedState)[action.get_idAction()]);
	}

	public void learn(ArrayList<TrainExample> trainExamples) {

	}

}
