package strategy;

import java.util.ArrayList;
import java.util.List;

import agent.Agent;
import agent.AgentAction;
import agent.PositionAgent;
import motor.Maze;
import motor.PacmanGame;
import neuralNetwork.TrainExample;

import java.util.Random;


public class ApproximateQLearningStrategy extends QLearningStrategy{

	double[] weights;
	int d;

	
	public ApproximateQLearningStrategy(double epsilon, double gamma, double alpha, int sizeMazeX, int sizeMazeY) {
		super(epsilon, gamma, alpha, sizeMazeX, sizeMazeY);
		

		
				
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
		// Not used here	
	}
	
	
	
	
	
	

	
	

}
