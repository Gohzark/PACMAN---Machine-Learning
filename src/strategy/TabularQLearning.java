package strategy;

import java.util.ArrayList;
import java.util.Random;

import agent.Agent;
import agent.AgentAction;
import agent.PositionAgent;
import motor.Maze;
import motor.PacmanGame;
import neuralNetwork.TrainExample;

import java.util.HashMap;


public class TabularQLearning  extends QLearningStrategy{


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

	





	@Override
	public AgentAction chooseAction(PacmanGame state) {

		return null;

	}





	@Override
	public void update(PacmanGame state, PacmanGame nextState, AgentAction action, double reward, boolean isFinalState) {



	}



	@Override
	public void learn(ArrayList<TrainExample> trainExamples) {
		// TODO Auto-generated method stub
	}







}
