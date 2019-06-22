package hr.fer.constants;

/**
 * Program preferences
 * @author gilles
 *
 */
public class Cons {

	//network preferences
	public static int NUM_HIDDEN_NEURONS = 16;
	public static double LEARNING_RATE = 0.01;
	public static int WINDOW_TO_PAST = 7;
	public static int NUM_OUTPUT_NEURONS = 6;
	public static int NUM_DATA=1;	//size of a data point
	public static int NUM_INPUT_NEURONS = WINDOW_TO_PAST*NUM_DATA;
	
	//labeling the data
	public static double HIGH_SELL_FROM = -1;
	public static double HIGH_SELL_TO = -0.1500001;
	public static double SELL_FROM = -0.15;
	public static double SELL_TO = -0.0500001;
	public static double LOW_SELL_FROM = -0.05;
	public static double LOW_SELL_TO = -0.0000001;
	
	public static double LOW_BUY_FROM = 0;
	public static double LOW_BUY_TO = 0.10;
	public static double BUY_FROM = 0.1000001;
	public static double BUY_TO = 0.30;
	public static double HIGH_BUY_FROM = 0.300001;
	public static double HIGH_BUY_TO = 100.0;

	//learning
	//num of iterations for learning
	public static int LEARNING_ITERATIONS = 200000;
	public static int TRAINING_BATCH_SIZE = 50;
}
