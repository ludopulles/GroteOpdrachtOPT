
package groteopdracht;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		
		Optimiser theBest = new Optimiser();
//		for (int i = 0; i < 100; i++) {
//			Optimiser optimiser = new Optimiser();
//			optimiser.addGreedily(new Comparator<Integer>() {
//
//				@Override
//				public int compare(Integer o1, Integer o2) {
//					return Order.orders[o2].penalty - Order.orders[o1].penalty;
//				}
//			});
//			optimiser.optimiseOrders();
//			
//			if (theBest.getScore() > optimiser.getScore()) {
//				theBest = optimiser;
//			}
//		}
		theBest.addClosest();
		
		long endTime = System.currentTimeMillis();
		
		System.err.println("TIME TAKEN: " + (endTime - startTime));

		File f = new File("solutions/thebest" + ".txt");
		System.err.println(f.getAbsolutePath());
		
		try (BufferedWriter output = new BufferedWriter(
				new FileWriter(f))) {
			theBest.printSolution(output);
		} catch (IOException e) {
			throw e;
		}
	}
}
