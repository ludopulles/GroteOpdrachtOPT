
package groteopdracht;

import java.io.IOException;
import java.util.Scanner;

import checker.ui.App;

public class Main {

	private static final boolean IN_THREADS = true;

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}
	
	public static void showSolution(Optimiser solution) {
		App checker = new App();
		checker.setSize(800, 600);
		checker.setLocationRelativeTo(null);
		checker.setVisible(true);

		checker.setSolution(solution.getSolution());
		solution.storeSafely();
	}

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		Optimiser startSolution = new Optimiser();
		startSolution.addClosest();
		startSolution.doOpts();

		Optimiser best = startSolution;
		if (Main.IN_THREADS) {
			best = improveAsync(startSolution);
		} else {
			best = improveSync(startSolution);
		}
		best.removeBadOrders();

		showSolution(best);

		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
		System.err.println("USED SEED: we don't know...");
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
				double min = RandomAdder.getBest().getScore();
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
		return RandomAdder.getBest();
	}

	private static Optimiser improveSync(Optimiser startSolution) {
		return RandomAdder.iterate(startSolution, 100);
	}
}
