
package groteopdracht;

import java.io.IOException;
import java.util.Comparator;

import checker.ui.App;
import groteopdracht.datastructures.Order;

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
		theBest.doOpts();
		theBest.addGreedily(new Comparator<Integer>() {
			
			@Override
			public int compare(Integer o1, Integer o2) {
				return Order.orders[o2].penalty - Order.orders[o1].penalty;
			}
		});
		theBest.doOpts();
		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime));
		theBest.storeSafely();

		App checker = new App();
		checker.setSize(800, 600);
		checker.setLocationRelativeTo(null);
		checker.setVisible(true);
		
		checker.setSolution(theBest.getSolution());
	}
}
