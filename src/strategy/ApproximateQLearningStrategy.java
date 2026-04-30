package strategy;

import java.util.ArrayList;

import agent.AgentAction;
import agent.PositionAgent;
import motor.PacmanGame;
import neuralNetwork.TrainExample;


public class ApproximateQLearningStrategy extends QLearningStrategy{

	double[] weights;
	int d;

	
	public ApproximateQLearningStrategy(double epsilon, double gamma, double alpha, int sizeMazeX, int sizeMazeY) {
		super(epsilon, gamma, alpha, sizeMazeX, sizeMazeY);
		this.d = 4; 
		this.weights = new double[d];
		for (int i = 0; i < d; i++) {
			this.weights[i] = 0.0;
		}		
	}

	public double[] getFeatures(PacmanGame state, AgentAction action) {
		PositionAgent pos = state.getPacmanPosition();
		pos = state.movePosition(pos, action);
		double[] features = new double[d];
		features[0] = 1.0;
		features[1] = 0.1 / (1 + state.getDistanceToClosest(pos, PacmanGame.gameElement.gum));
		features[2] = 1 / (1 + state.getDistanceToClosest(pos, PacmanGame.gameElement.capsule));
		features[3] = (state.isGhostsScarred() ? -1 : 1) / (1 + state.getDistanceToClosest(pos, PacmanGame.gameElement.ghost));
		return features;
	}

	public double getQValue(PacmanGame state, AgentAction action) {
		double[] features = getFeatures(state, action);
		double qValue = 0.0;
		for (int i = 0; i < d; i++) {
			qValue += weights[i] * features[i];
		}
		return qValue;
	}

	
	@Override
	public AgentAction chooseAction(PacmanGame state) {
		ArrayList<AgentAction> legalActions = state.getLegalPacmanActions();
		if (Math.random() < current_epsilon) {
			return state.getRandomAllowedAction(legalActions);
		}
		AgentAction bestAction = chooseBestAction(state);
		return bestAction;
	}

	public AgentAction chooseBestAction(PacmanGame state) {
		ArrayList<AgentAction> legalActions = state.getLegalPacmanActions();
		AgentAction bestAction = new AgentAction(AgentAction.STOP);
		double maxQ = Double.NEGATIVE_INFINITY;
		for (AgentAction action : legalActions) {
			double qValue = getQValue(state, action);
			if (qValue > maxQ) {
				maxQ = qValue;
				bestAction = action;
			}
			else if (qValue == maxQ) {
				if (Math.random() < 0.5) {
					bestAction = action;
				}
			}
		}
		return bestAction;
	}
	
	@Override
	public void update(PacmanGame state, PacmanGame nextState, AgentAction action, double reward, boolean isFinalState) {
		//System.out.println("Update start");
		AgentAction bestNextAction = chooseBestAction(nextState);
		double targetQ;
		if (!isFinalState) {
			targetQ = reward + gamma * getQValue(nextState, bestNextAction);
			
		}
		else {
			targetQ = reward;
		}
		double[] features = getFeatures(state, action);
		double QValue = getQValue(state, action);
		for (int i = 0; i < d; i++) {
			weights[i] = weights[i] - 2 * learningRate * features[i] * (QValue - targetQ);
		}
		System.out.println("Nouveau poids " + "w1: " + weights[0] + ", w2: " + weights[1] + ", w3: " + weights[2] + ", w4: " + weights[3]);
	}

	
	
	@Override
	public void learn(ArrayList<TrainExample> trainExamples) {
		// Not used here	
	}
	
	@Override
	public String toString() {
		return "Approximate-Q-Learning";
	}
	
	
	
	

	
	

}
