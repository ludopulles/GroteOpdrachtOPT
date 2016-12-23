
package groteopdracht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import checker.ui.App;
import groteopdracht.datastructures.WeekSchema;

public class Main {

	private static final boolean IN_THREADS = true;
	private static final boolean USE_BEST = true;

	public static void infoMsg(String s) {
		System.out.println("INFO: " + s);
	}

	public static void showSolution(WeekSchema solution) {
		App checker = new App();
		checker.setSize(800, 600);
		checker.setLocationRelativeTo(null);
		checker.setVisible(true);
		checker.setSolution(solution);
	}

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		long startTime = System.currentTimeMillis();
		WeekSchema best = null;
		if (USE_BEST) {
			System.out.print("Which file do you want to optimise? ");
			try {
				long score = sc.nextLong();
				File file = new File(Constants.SOLUTIONS_DIR + "/score" + score + ".txt");
				System.out.println("Reading solution from " + file.getName());
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					best = new WeekSchema(br);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (InputMismatchException e) {
			}
		}
		if (best == null) {
			best = new WeekSchema();
			best.addClosest();
			best.doOpts();
		}

		if (Main.IN_THREADS) {
			best = improveAsync(best, sc);
		} else {
			best = improveSync(best);
		}

		System.out.println("Starting small improvements; score: " + best.getScore());
		best.addImprovements();
		for (int i = 0; i < 5; i++) {
			best.removeBadOrders();
			best.doOpts();
			// best.addGreedilyRandom();
			// best.doOpts();
			best.doRandomSwaps((int) 1e6);
		}
		System.out.println("Starting simulated annealing; score: " + best.getScore());

		// best = best.simulatedAnnealing();
		best = best.simAnnealSwap(1.0, 0.99, (int) 3e5);

		System.out.println("Starting small improvements; score: " + best.getScore());
		for (int i = 0; i < 5; i++) {
			// best.addGreedilyRandom();
			best.removeBadOrders();
			best.doOpts();
			best.doRandomSwaps((int) 1e6);
			best.doOpts();
		}

		for (int i = 0; i < 10000; i++) {
			WeekSchema alt = new WeekSchema(best);
			if (alt.randomSimSwap() && alt.getScore() < best.getScore()) {
				System.err.println("BETTER: " + alt.getScore() + " vs " + best.getScore());
				best = alt;
			}
		}

		System.out.println("Finished; score: " + best.getScore());
		showSolution(best);
		best.storeSafely();
		
		System.err.println("VALID: " + best.checkScore(true));

		sc.close();
		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
		System.err.println("USED SEED: " + WeekSchema.SEED);
	}

	private static WeekSchema improveAsync(WeekSchema startSolution, Scanner sc) {
		final int nThreads = 4;
		Thread[] adders = new Thread[nThreads];
		for (int i = 0; i < nThreads; i++) {
			// adders[i] = new RandomAdder(startSolution, !USE_BEST);
			adders[i] = i < 2 ? new RandomAdder(startSolution, !USE_BEST) : new RandomShuffler(startSolution);
			adders[i].start();
		}
		final boolean waitForStop = true;
		if (waitForStop) {
			while (true) {
				System.out.println("> Type stop, or best");
				String line = sc.nextLine();
				if ("stop".equalsIgnoreCase(line))
					break;
				if ("best".equalsIgnoreCase(line)) {
					double min = RandomAdder.best.getScore();
					System.out.println("Current best score: " + min);
				}
			}
		} else {
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("Stop trying...");
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
		return RandomAdder.best;
	}

	private static WeekSchema improveSync(WeekSchema startSolution) {
		return RandomAdder.iterate(startSolution, 100);
	}
}
