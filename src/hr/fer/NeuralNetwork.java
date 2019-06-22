package hr.fer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hr.fer.constants.Cons;
import hr.fer.utilities.Utils;

public class NeuralNetwork {

	private double[][] WEIGHTS_INPUT_HIDDEN;
	private double[][] WEIGHTS_HIDDEN_OUTPUT;
	private double[] BIASES_HIDDEN;
	private double[] BIASES_OUTPUT;	

	private double[] INPUT_NEURONS;
	private double[] HIDDEN_NEURONS;
	private double[] OUTPUT_NEURONS;
			
		
	public NeuralNetwork() {
		WEIGHTS_INPUT_HIDDEN = new double[Cons.NUM_INPUT_NEURONS][Cons.NUM_HIDDEN_NEURONS];
		WEIGHTS_HIDDEN_OUTPUT = new double[Cons.NUM_HIDDEN_NEURONS][Cons.NUM_OUTPUT_NEURONS];
		BIASES_HIDDEN = new double[Cons.NUM_HIDDEN_NEURONS];
		BIASES_OUTPUT = new double[Cons.NUM_OUTPUT_NEURONS];	

		INPUT_NEURONS = new double[Cons.NUM_INPUT_NEURONS];
		HIDDEN_NEURONS = new double[Cons.NUM_HIDDEN_NEURONS];
		OUTPUT_NEURONS = new double[Cons.NUM_OUTPUT_NEURONS];
	}


	public void learn(String coin) {
		if(Utils.isItYourFirstTime(coin)) {
			Utils.prepareData(coin);
			randomizeWeightsAndBiases();
		}
		else {
			System.out.println("Network already has some knowledge of the coin.");
			loadMemory(coin);
		}
		learnEngine(coin);
		overrideMemory(coin);
		
//		boolean exportKnowledge = false; //set to true, if want to save learned knowledge (e.g. as a backup)
//		if(exportKnowledge) {
//			String nameOfTheKnowledge = "ethereum";
//			
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//			LocalDateTime now = LocalDateTime.now();
//			
//			exportKnowledge(nameOfTheKnowledge+" - "+dtf.format(now)+".knw");
//		}	
		
	}
	
	private  void learnEngine(String coin) {
		System.out.println("Learning started...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./TrainingData/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		List<Integer> indicies = new ArrayList<Integer>();
		for(int i=0; i<rows.size();i++) {
			indicies.add(i);
		}
		String randomPoint;
		double[] target = new double[Cons.NUM_OUTPUT_NEURONS];
		String[] temp;
		int[] randomIndicies = new int[Cons.TRAINING_BATCH_SIZE];	//it will contain batch_size number of training entries
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//learning will take place in 100000 iterations in which there is 50 random entries and updating weights and biases every iteration
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for(int numOfIters = 0; numOfIters < Cons.LEARNING_ITERATIONS; numOfIters++) {
			randomIndicies = Utils.getRandomIndicies(indicies, Cons.TRAINING_BATCH_SIZE);
			double[][] reduceWeight_IH = new double[Cons.NUM_INPUT_NEURONS][Cons.NUM_HIDDEN_NEURONS];
			double[][] reduceWeight_HO = new double[Cons.NUM_HIDDEN_NEURONS][Cons.NUM_OUTPUT_NEURONS];
			double[] reduceBiases_H = new double[Cons.NUM_HIDDEN_NEURONS];
			double[] reduceBiases_O = new double[Cons.NUM_OUTPUT_NEURONS];
			//see how would that batch change weights and biases
			//MAIN derivations:
			double[][] dCostdWeight_IH = new double[Cons.NUM_INPUT_NEURONS][Cons.NUM_HIDDEN_NEURONS];
			double[][] dCostdWeight_HO = new double[Cons.NUM_HIDDEN_NEURONS][Cons.NUM_OUTPUT_NEURONS];
			double[] dCostdBiasesHidden = new double[Cons.NUM_HIDDEN_NEURONS];
			double[] dCostdBiasesOutput = new double[Cons.NUM_OUTPUT_NEURONS];
			
			double[] dCostdOutput = new double[Cons.NUM_OUTPUT_NEURONS]; //treci element u videu
			
			double[] costs = new double[Cons.NUM_OUTPUT_NEURONS]; //how far away values on output neurons are (AVERAGE FROM 50 random entries)
			//this is for BP1
			double[] errorOutputNeurons = new double[Cons.NUM_OUTPUT_NEURONS];
			//this is for BP2
			double[] errorHiddenNeurons = new double[Cons.NUM_HIDDEN_NEURONS];
			
			//start for one batch (50 entries)
			for(int randomIndex : randomIndicies) {
				// pick a random point
				randomPoint = rows.get(randomIndex);
				for(int i=0; i<6;i++) {
					target[i]=0;
				}
				temp = randomPoint.split("\t");
				for(int i=0; i<temp.length-1; i++) {
					INPUT_NEURONS[i]=Double.parseDouble(temp[i]);								//a^1
				}
				target[Utils.convertDecision(temp[temp.length-1])] = 1.0;
				
																									//z^1 NE POSTOJI
				// feed forward
				double[] weightedSumHidden = new double[Cons.NUM_HIDDEN_NEURONS];					//z^2
				double[] weightedSumOutput = new double[Cons.NUM_OUTPUT_NEURONS];					//z^3
				
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					weightedSumHidden[i] += BIASES_HIDDEN[i];
					for(int j=0; j<Cons.NUM_INPUT_NEURONS; j++) {
						weightedSumHidden[i] += INPUT_NEURONS[j]*WEIGHTS_INPUT_HIDDEN[j][i];
					}
					HIDDEN_NEURONS[i] = Utils.sigmoid(weightedSumHidden[i]);						//a^2
				}
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					weightedSumOutput[i] += BIASES_OUTPUT[i];
					for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) {
						weightedSumOutput[i] += HIDDEN_NEURONS[j]*WEIGHTS_HIDDEN_OUTPUT[j][i];
					}
					OUTPUT_NEURONS[i] = Utils.sigmoid(weightedSumOutput[i]);						//a^3
				}
			
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					costs[i] = (Math.pow((OUTPUT_NEURONS[i]-target[i]), 2)) / 2*Cons.TRAINING_BATCH_SIZE;	//needed??
				}
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					dCostdOutput[i] = (OUTPUT_NEURONS[i]-target[i]);										//grad aC
				}
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					errorOutputNeurons[i] = dCostdOutput[i] * Utils.sigmoidDerivate(weightedSumOutput[i]);	//delta L (delta l+1)
				}
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++) {
						errorHiddenNeurons[i] += WEIGHTS_HIDDEN_OUTPUT[i][j] * errorOutputNeurons[j];		//delta l
					}
					errorHiddenNeurons[i] *= Utils.sigmoidDerivate(weightedSumHidden[i]);
				}
				
				for(int i=0; i<Cons.NUM_INPUT_NEURONS; i++) {
					for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) {
						dCostdWeight_IH[i][j] = INPUT_NEURONS[i]*errorHiddenNeurons[j];
					}
				}
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++) {
						dCostdWeight_HO[i][j] = HIDDEN_NEURONS[i] * errorOutputNeurons[j];
					}
				}
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					dCostdBiasesHidden[i] = errorHiddenNeurons[i];
				}
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					dCostdBiasesOutput[i] = errorOutputNeurons[i];
				}
				
				
				for(int i=0; i<Cons.NUM_INPUT_NEURONS; i++) {
					for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) {
						reduceWeight_IH[i][j] += (Cons.LEARNING_RATE/Cons.TRAINING_BATCH_SIZE)*dCostdWeight_IH[i][j];
					}
				}
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++) {
						reduceWeight_HO[i][j] += (Cons.LEARNING_RATE/Cons.TRAINING_BATCH_SIZE)*dCostdWeight_HO[i][j];
					}
				}
				for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
					reduceBiases_H[i] += (Cons.LEARNING_RATE/Cons.TRAINING_BATCH_SIZE)*dCostdBiasesHidden[i];
				}
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					reduceBiases_O[i] += (Cons.LEARNING_RATE/Cons.TRAINING_BATCH_SIZE)*dCostdBiasesOutput[i];
				}
				//end of one training example from a batch (1/50)
			}
//			System.out.println("End of one batch, indexes used for that batch:");
//			for(int ind : randomIndicies) {
//				System.out.print(ind+",");
//			}
//			System.out.println();
//			System.out.println("Cost: ");
//			for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
//				System.out.println(costs[i]);
//			}
			
			
			//update weights and biases
//			System.out.println("Updating weights and biases...");
			for(int i = 0; i<Cons.NUM_INPUT_NEURONS; i++) {
				for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++){
					WEIGHTS_INPUT_HIDDEN[i][j] -= reduceWeight_IH[i][j];
				}
			}
			for(int i = 0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
				for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++){
					WEIGHTS_HIDDEN_OUTPUT[i][j] -= reduceWeight_HO[i][j];
				}
			}
			for(int i = 0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
				BIASES_HIDDEN[i] -= reduceBiases_H[i];
			}
			for(int i = 0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
				BIASES_OUTPUT[i] -= reduceBiases_O[i];
			}
			
			if(numOfIters%5000 == 0) {
				System.out.println("\nEnd of "+(numOfIters+1)+"th iteration.");
				double sum = 0;
				for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
					sum+=costs[i];
				}
				System.out.println("COST SUM: "+sum);
				System.out.println("#############################");
			}
			
		}
			
	}


	public void test(String coin) {
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./TestingData/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadMemory(coin);
		int[] myanswers = new int[6];
		int[] correctanswers = new int[6];
		int numOfCorrect = 0;
		for(String row : rows) {
			String[] temp = row.split("\t");
			String correctPrediction = temp[temp.length-1];
			String myPrediction = predict(row);
			if(correctPrediction.equals(myPrediction)) {
				numOfCorrect++;
			}
			myanswers[Utils.convertDecision(myPrediction)]++;
			correctanswers[Utils.convertDecision(correctPrediction)]++;
		}
		System.out.println("Testing mode ended.");
		System.out.format("Neural network predicted correct in %d (%.2f %%) cases "
				+ "out of total %d tested examples.",numOfCorrect,(100*(double)numOfCorrect/rows.size()),rows.size());
		System.out.println();
		System.out.println("My answers:");
		for(int i=0; i<6; i++) {
			System.out.println(Utils.convertDecisionReverse(i)+" x"+myanswers[i]);
		}
		System.out.println("Correct answers:");
		for(int i=0; i<6; i++) {
			System.out.println(Utils.convertDecisionReverse(i)+" x"+correctanswers[i]);
		}
	}

	public String predict(String data) {
		String[] temp = data.split("\t");
		for(int i=0; i<Cons.WINDOW_TO_PAST*Cons.NUM_DATA; i++) {
			INPUT_NEURONS[i] = Double.parseDouble(temp[i]);
		}
		// feed forward
		double[] weightedSumHidden = new double[Cons.NUM_HIDDEN_NEURONS];					//z^2
		double[] weightedSumOutput = new double[Cons.NUM_OUTPUT_NEURONS];					//z^3
		
		for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
			weightedSumHidden[i] += BIASES_HIDDEN[i];
			for(int j=0; j<Cons.NUM_INPUT_NEURONS; j++) {
				weightedSumHidden[i] += INPUT_NEURONS[j]*WEIGHTS_INPUT_HIDDEN[j][i];
			}
			HIDDEN_NEURONS[i] = Utils.sigmoid(weightedSumHidden[i]);						//a^2
		}
		for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
			weightedSumOutput[i] += BIASES_OUTPUT[i];
			for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) {
				weightedSumOutput[i] += HIDDEN_NEURONS[j]*WEIGHTS_HIDDEN_OUTPUT[j][i];
			}
			OUTPUT_NEURONS[i] = Utils.sigmoid(weightedSumOutput[i]);						//a^3
		}
		int indexOfMaxValue = 0;
		for(int i=0; i<Cons.NUM_OUTPUT_NEURONS; i++) {
			if(OUTPUT_NEURONS[i] > OUTPUT_NEURONS[indexOfMaxValue]) {
				indexOfMaxValue = i;
			}
		}
//		System.out.println("###########################");
		return Utils.convertDecisionReverse(indexOfMaxValue);
	}

	
	private void randomizeWeightsAndBiases() {
		System.out.println("Randomizing the weights...");
		for(int i = 0; i<Cons.NUM_INPUT_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++){
				WEIGHTS_INPUT_HIDDEN[i][j] = Math.random();
			}
		}
		for(int i = 0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++){
				WEIGHTS_HIDDEN_OUTPUT[i][j] = Math.random();
			}
		}
		for(int i = 0; i<BIASES_HIDDEN.length; i++) {
			BIASES_HIDDEN[i] = Math.random();
		}
		for(int i = 0; i<BIASES_OUTPUT.length; i++) {
			BIASES_OUTPUT[i] = Math.random();
		}
		System.out.println("Weights has been randomized.");
	}
	
	private void loadMemory(String coin) {
		System.out.println("Loading knowledge from memory...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./MemoryBase/memory-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator<String> iter = rows.iterator();
		for(int i=0; i<Cons.NUM_INPUT_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) {
				WEIGHTS_INPUT_HIDDEN[i][j] = Double.parseDouble(iter.next());
			}
		}
		for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++) {
				WEIGHTS_HIDDEN_OUTPUT[i][j] = Double.parseDouble(iter.next());
			}
		}
		for(int i = 0; i<BIASES_HIDDEN.length; i++) {
			BIASES_HIDDEN[i] = Double.parseDouble(iter.next());
		}
		for(int i = 0; i<BIASES_OUTPUT.length; i++) {
			BIASES_OUTPUT[i] = Double.parseDouble(iter.next());
		}
		if(iter.hasNext()) {
			System.err.println("HAS NEXT IN MEMORY: "+iter.next());
		}
	}
	
	public void overrideMemory(String coin) {
		PrintWriter p = null;
		try {
			 p = new PrintWriter(new FileWriter("./MemoryBase/memory-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0; i<Cons.NUM_INPUT_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_HIDDEN_NEURONS; j++) { 
				p.println(WEIGHTS_INPUT_HIDDEN[i][j]);
			}
		}
		for(int i=0; i<Cons.NUM_HIDDEN_NEURONS; i++) {
			for(int j=0; j<Cons.NUM_OUTPUT_NEURONS; j++) { 
				p.println(WEIGHTS_HIDDEN_OUTPUT[i][j]);
			}
		}
		for(double x : BIASES_HIDDEN) {
			p.println(x);
		}
		for(double x : BIASES_OUTPUT) {
			p.println(x);
		}
		p.close();
	}
}
