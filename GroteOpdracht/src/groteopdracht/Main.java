
package groteopdracht;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import groteopdracht.datastructures.Order;

public class Main {

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}
	
	public static void main(String[] args) throws IOException {
		Optimiser optimiser = new Optimiser();
		optimiser.addGreedily(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Order.orders[o2].penalty - Order.orders[o1].penalty;
			}
		});
		
		optimiser.optimiseOrders();

		try (BufferedWriter output = new BufferedWriter(new FileWriter("solution.txt"))) {
			optimiser.printSolution(output);
		} catch (IOException e) {
			throw e;
		}
	}
}
