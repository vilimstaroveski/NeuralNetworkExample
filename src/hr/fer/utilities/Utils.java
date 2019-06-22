package hr.fer.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import hr.fer.constants.Cons;

public class Utils {
	
	public static void prepareData(String coin) {
		System.out.println("Preparing data...");
//		Utils.transformInput(coin);
		Utils.transformInputOneDataElem(coin);
//		Utils.prepareTrainingData(coin);
		Utils.prepareTrainingDataOneDataElem(coin);
		//split 80% of training data for training and 20% for testing
		Utils.splitTrainingData(coin);
		System.out.println("Data is ready!");
	}

	private static void transformInputOneDataElem(String coin) {
		System.out.println("Transforming input...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./HistoricalData-coinmarketcap/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.reverse(rows);
		PrintWriter p = null;
		try {
			 p = new PrintWriter(new FileWriter("./temp/temp-"+coin+".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//previous day
		String[] parts = rows.get(0).replaceAll(",", "").split("\\s+");
		//today
		double openPrice;
		double closePrice;

//		- price change in that day (%)
		double neuron1;

		String decision = "";
		boolean start = true;

		for(String row : rows) {
			parts = row.replaceAll(",", "").split("\\s+");
			
			openPrice = Double.parseDouble(parts[3]);
			closePrice = Double.parseDouble(parts[6]);
			
			neuron1 = 100* (closePrice/openPrice-1);
			

			//System.out.format("#%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", openPrice,highPrice,lowPrice,closePrice,volumeT,marketCapT);
			
			//System.out.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", neuron1,neuron2,neuron3,neuron4,neuron5);
			//based on today, i decide was it smart to sell or buy yesterday
			double roi = closePrice/openPrice-1;
			if(roi > Cons.HIGH_SELL_FROM && roi < Cons.HIGH_SELL_TO) {
				decision = "HIGH_SELL";
			}
			else if(roi > Cons.SELL_FROM && roi < Cons.SELL_TO) {
				decision = "SELL";
			}
			else if(roi > Cons.LOW_SELL_FROM && roi < Cons.LOW_SELL_TO) {
				decision = "LOW_SELL";
			}
			else if(roi > Cons.LOW_BUY_FROM && roi < Cons.LOW_BUY_TO) {
				decision = "LOW_BUY";
			}
			else if(roi > Cons.BUY_FROM && roi < Cons.BUY_TO) {
				decision = "BUY";
			}
			else if(roi > Cons.HIGH_BUY_FROM && roi < Cons.HIGH_BUY_TO) {
				decision = "HIGH_BUY";
			}
			
			if(start) {
				p.print(neuron1+"\t");
				start = false;
			}
			else {
				p.println(decision);
				p.print(neuron1+"\t");
			}
			
		}
		p.close();
			
	}

	/**
	 * Takes file containing TRANSFORMED input data, and creates 2 new files, one for training and one for testing
	 * all paths should be provided as parameters
	 * @param pathToInputData
	 * @param pathOutputTest
	 * @param pathOutputTrain
	 */
	private static void splitTrainingData(String coin) {
		System.out.println("Splitting training data...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./temp/temp-TD-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		PrintWriter pTraining = null;
		try {
			 pTraining = new PrintWriter(new FileWriter("./TrainingData/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pTesting = null;
		try {
			 pTesting = new PrintWriter(new FileWriter("./TestingData/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.shuffle(rows);
		int boundry = rows.size()/5;
		for(int i = 0; i<rows.size(); i++) {
			if(i<boundry) 
				pTesting.println(rows.get(i));
			else
				pTraining.println(rows.get(i));
		}
		pTesting.close();
		pTraining.close();
	}
	
	private static void prepareTrainingData(String coin) {
		System.out.println("Preparing training data...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./temp/temp-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		PrintWriter p = null;
		try {
			 p = new PrintWriter(new FileWriter("./temp/temp-TD-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] parts = null;
		for(int i = 0; i<rows.size()-Cons.WINDOW_TO_PAST; i++) {
			for(int j=0; j<Cons.WINDOW_TO_PAST; j++) {
				parts = rows.get(i+j).split("\t");
				p.print(parts[0]+"\t"+parts[1]+"\t"+parts[2]+"\t"+parts[3]+"\t"+parts[4]+"\t");
			}
			p.println(parts[5]);
		}
		p.close();
	}
	private static void prepareTrainingDataOneDataElem(String coin) {
		System.out.println("Preparing training data...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./temp/temp-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		PrintWriter p = null;
		try {
			 p = new PrintWriter(new FileWriter("./temp/temp-TD-"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] parts = null;
		for(int i = 0; i<rows.size()-Cons.WINDOW_TO_PAST; i++) {
			for(int j=0; j<Cons.WINDOW_TO_PAST; j++) {
				parts = rows.get(i+j).split("\t");
				p.print(parts[0]+"\t");
			}
			p.println(parts[1]);
		}
		p.close();
	}
	private static void transformInput(String coin) {
		System.out.println("Transforming input...");
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./HistoricalData-coinmarketcap/"+coin+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.reverse(rows);
		PrintWriter p = null;
		try {
			 p = new PrintWriter(new FileWriter("./temp/temp-"+coin+".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//previous day
		String[] parts = rows.get(0).replaceAll(",", "").split("\\s+");
		double volumeY = Double.parseDouble(parts[7]);
		double marketCapY = Double.parseDouble(parts[8]);
		//today
		double openPrice;
		double highPrice;
		double lowPrice;
		double closePrice;
		double volumeT;
		double marketCapT;

//		- price change in that day (%)
		double neuron1;
//		- highest price as in % from opening
		double neuron2;
//		- lowest price as in % from opening
		double neuron3;
//		- volume increase (%) in compare to last day
		double neuron4;
//		- market cap change (%)
		double neuron5;
//		- percentage in the global marketcap
//		double neuron6;

		String decision = "";
		boolean start = true;

		for(String row : rows) {
			parts = row.replaceAll(",", "").split("\\s+");
			
			openPrice = Double.parseDouble(parts[3]);
			highPrice = Double.parseDouble(parts[4]);
			lowPrice = Double.parseDouble(parts[5]);
			closePrice = Double.parseDouble(parts[6]);
			volumeT = Double.parseDouble(parts[7]);
			marketCapT = Double.parseDouble(parts[8]);
			
			neuron1 = 100* (closePrice/openPrice-1);
			neuron2 = 100* (highPrice/openPrice-1);
			neuron3 = 100* (lowPrice/openPrice-1);
			neuron4 = 100* (volumeT/volumeY-1);
			neuron5 = 100* (marketCapT/marketCapY-1);
			

			//System.out.format("#%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", openPrice,highPrice,lowPrice,closePrice,volumeT,marketCapT);
			
			//System.out.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", neuron1,neuron2,neuron3,neuron4,neuron5);
			volumeY = volumeT;
			marketCapY = marketCapT;
			//based on today, i decide was it smart to sell or buy yesterday
			double roi = closePrice/openPrice-1;
			if(roi > Cons.HIGH_SELL_FROM && roi < Cons.HIGH_SELL_TO) {
				decision = "HIGH_SELL";
			}
			else if(roi > Cons.SELL_FROM && roi < Cons.SELL_TO) {
				decision = "SELL";
			}
			else if(roi > Cons.LOW_SELL_FROM && roi < Cons.LOW_SELL_TO) {
				decision = "LOW_SELL";
			}
			else if(roi > Cons.LOW_BUY_FROM && roi < Cons.LOW_BUY_TO) {
				decision = "LOW_BUY";
			}
			else if(roi > Cons.BUY_FROM && roi < Cons.BUY_TO) {
				decision = "BUY";
			}
			else if(roi > Cons.HIGH_BUY_FROM && roi < Cons.HIGH_BUY_TO) {
				decision = "HIGH_BUY";
			}
			
			if(start) {
				p.print(neuron1+"\t"+neuron2+"\t"+neuron3+"\t"+neuron4+"\t"+neuron5+"\t");
				start = false;
			}
			else {
				p.println(decision);
				p.print(neuron1+"\t"+neuron2+"\t"+neuron3+"\t"+neuron4+"\t"+neuron5+"\t");
			}
			
		}
		p.close();
	}
	
	public static boolean isItYourFirstTime(String coin) {
		List<String> rows = null;
		try {
			rows = Files.readAllLines(Paths.get("./MemoryBase/memory-"+coin+".txt"));
		} catch (IOException e) {
			return true;
		}
		return rows.isEmpty();
	}

	public static double sigmoid(double x) {
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}
	
	public static int[] getRandomIndicies(List<Integer> indicies, int size) {
		
        Collections.shuffle(indicies);
        int[] indiciesRet = new int[size];
        for (int i=0; i<size; i++) {
            indiciesRet[i] = indicies.get(i);
        }
		return indiciesRet;
	}

	public static double sigmoidDerivate(double x) {
		double s = sigmoid(x);
		return s*(1-s);
	}
	
	public static String convertDecisionReverse(int indexOfActivatedNeuron) {
		if(indexOfActivatedNeuron == 0) {
			return "HIGH_SELL";
		}
		else if(indexOfActivatedNeuron == 1) {
			return "SELL";
		}
		else if(indexOfActivatedNeuron == 2) {
			return "LOW_SELL";
		}
		else if(indexOfActivatedNeuron == 3) {
			return "LOW_BUY";
		}
		else if(indexOfActivatedNeuron == 4) {
			return "BUY";
		}
		else if(indexOfActivatedNeuron == 5) {
			return "HIGH_BUY";
		}
		return "";
	}

	public static int convertDecision(String decision) {
		if(decision.equals("HIGH_SELL")) {
			return 0;
		}
		else if(decision.equals("SELL")) {
			return 1;
		}
		else if(decision.equals("LOW_SELL")) {
			return 2;
		}
		else if(decision.equals("LOW_BUY")) {
			return 3;
		}
		else if(decision.equals("BUY")) {
			return 4;
		}
		else if(decision.equals("HIGH_BUY")) {
			return 5;
		}
		return 8;
	}
}
