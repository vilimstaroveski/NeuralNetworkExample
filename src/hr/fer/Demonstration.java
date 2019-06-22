package hr.fer;

public class Demonstration {

	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork();
		
//		nn.learn("ethereum");
//		
//		nn.test("ethereum");
//		
//		nn.predict("ethereum");
		
		nn.learn("bitcoin");
		
		nn.test("bitcoin");
		
		//TODO: if above works with correct score >75% (result: ~49%)
//		nn.predict("bitcoin");
		
		
	}
}
