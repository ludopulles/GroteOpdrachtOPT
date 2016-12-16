
package groteopdracht;

import java.io.IOException;
import java.util.Scanner;

import checker.ui.App;

public class Main {

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		
		Optimiser beginOplossing = new Optimiser();
		beginOplossing.addClosest();
//		beginOplossing.doOpts();
		
//		theBest.addGreedily(new Comparator<Integer>() {
//
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return Order.orders[o2].penalty - Order.orders[o1].penalty;
//			}
//		});
//		theBest.doOpts();
		
		final int nThreads = 4;
		RandomAdder[] adders = new RandomAdder[nThreads];
		for (int i = 0; i < nThreads; i++) {
			adders[i] = new RandomAdder(beginOplossing);
			adders[i].start();
		}
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("? ");
			String line = sc.nextLine();
			if ("stop".equalsIgnoreCase(line)) break;
			if ("best".equalsIgnoreCase(line)) {
				double min = Double.POSITIVE_INFINITY;
				for (int i = 0; i < nThreads; i++) min = Math.min(min, adders[i].getBestScore());
				System.out.println("BEST SCORE: " + min);
			}
		}
		for (int i = 0; i < nThreads; i++) {
			adders[i].interrupt();
		}
		try {
			for (int i = 0; i < nThreads; i++) {
				adders[i].join();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Optimiser best = beginOplossing;
		for (int i = 0; i < nThreads; i++) {
			if (adders[i].getBest().compareTo(best) < 0) {
				best = adders[i].getBest();
			}
		}

		App checker = new App();
		checker.setSize(800, 600);
		checker.setLocationRelativeTo(null);
		checker.setVisible(true);
		
		checker.setSolution(best.getSolution());
		best.storeSafely();

		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
		
		sc.close();
	}
}
