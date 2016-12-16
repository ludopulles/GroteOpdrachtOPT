
package groteopdracht;

import java.io.IOException;
import java.util.Scanner;

import checker.ui.App;

public class Main {

	private static final boolean IN_THREADS = true;

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		Optimiser startSolution = new Optimiser();
		startSolution.addClosest();

		Optimiser best;
		if (Main.IN_THREADS) {
			best = improveAsync(startSolution);
		} else {
			best = improveSync(startSolution);
		}

		System.out.println("Before: " + best.getScore());
		best.doRandomSwaps();
		System.out.println("After: " + best.getScore());
		
		App checker = new App();
		checker.setSize(800, 600);
		checker.setLocationRelativeTo(null);
		checker.setVisible(true);

		checker.setSolution(best.getSolution());
		best.storeSafely();

		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
	}

	private static Optimiser improveAsync(Optimiser startSolution) {
		final int nThreads = 4;
		RandomAdder[] adders = new RandomAdder[nThreads];
		for (int i = 0; i < nThreads; i++) {
			adders[i] = new RandomAdder(startSolution);
			adders[i].start();
		}
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("? ");
			String line = sc.nextLine();
			if ("stop".equalsIgnoreCase(line))
				break;
			if ("best".equalsIgnoreCase(line)) {
				double min = Double.POSITIVE_INFINITY;
				for (int i = 0; i < nThreads; i++)
					min = Math.min(min, adders[i].getBestScore());
				System.out.println("BEST SCORE: " + min);
			}
		}
		sc.close();

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
		Optimiser best = startSolution;
		for (int i = 0; i < nThreads; i++) {
			if (adders[i].getBest().compareTo(best) < 0) {
				best = adders[i].getBest();
			}
		}
		return best;
	}

	private static Optimiser improveSync(Optimiser startSolution) {
		return RandomAdder.iterate(startSolution, 10);
	}
}
