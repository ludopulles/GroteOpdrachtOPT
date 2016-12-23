
package groteopdracht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import checker.ui.App;
import groteopdracht.datastructures.WeekSchema;

public class Main {

	private static final boolean IN_THREADS = false;
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
			long score = sc.nextLong();
			File file = new File(Constants.SOLUTIONS_DIR + "/score" + score + ".txt");
			System.out.println("Reading solution from " + file.getName());
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				best = new WeekSchema(br);
			} catch (IOException e) {
				e.printStackTrace();
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

		System.out.println("Before: " + best.getScore());
		for (int i = 0; i < 5; i++) {
			best.removeBadOrders();
			best.doOpts();
//			best.addGreedilyRandom();
//			best.doOpts();
			best.doRandomSwaps((int) 1e6);
		}
		System.out.println("After: " + best.getScore());

		//best = best.simulatedAnnealing();
		System.err.println("Starting simanneal");
		best = best.simAnnealSwap(3.0, 0.95, (int) 1e6);
		System.err.println("Done with simanneal");
		
		for (int i = 0; i < 10000; i++) {
			WeekSchema alt = new WeekSchema(best);
			if (alt.randomSimSwap() && alt.getScore() < best.getScore()) {
				System.err.println("BETTER: " + alt.getScore() + " vs " + best.getScore());
				best = alt;
			}
		}
		
		showSolution(best);
		best.storeSafely();

		sc.close();
		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
		System.err.println("USED SEED: " + WeekSchema.SEED);
	}

	private static WeekSchema improveAsync(WeekSchema startSolution, Scanner sc) {
		final int nThreads = 4;
		RandomAdder[] adders = new RandomAdder[nThreads];
		for (int i = 0; i < nThreads; i++) {
			adders[i] = new RandomAdder(startSolution, !USE_BEST);
			adders[i].start();
		}
		while (true) {
			System.out.println("> Type stop, or best");
			String line = sc.nextLine();
			if ("stop".equalsIgnoreCase(line))
				break;
			if ("best".equalsIgnoreCase(line)) {
				double min = RandomAdder.getBest().getScore();
				System.out.println("Current best score: " + min);
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
		return RandomAdder.getBest();
	}

	private static WeekSchema improveSync(WeekSchema startSolution) {
		return RandomAdder.iterate(startSolution, 10000);
	}
}
