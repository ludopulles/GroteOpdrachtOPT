
package groteopdracht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import checker.ui.App;
import groteopdracht.datastructures.WeekSchema;

public class Main {

	private static final boolean IN_THREADS = true;
	private static final boolean USE_BEST = false;

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
		long startTime = System.currentTimeMillis();
		WeekSchema startSolution = new WeekSchema();
		if (USE_BEST) {
			File dir = new File(Constants.SOLUTIONS_DIR);
			File[] files = dir.listFiles();
			if (files.length > 0) {
				System.out.println("Reading solution from " + files[0].getName());
				try (BufferedReader br = new BufferedReader(new FileReader(files[0]))) {
					startSolution = new WeekSchema(br);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		startSolution.addClosest();
		startSolution.doOpts();

		WeekSchema best = startSolution;
		if (Main.IN_THREADS) {
			best = improveAsync(startSolution);
		} else {
			best = improveSync(startSolution);
		}
		best.removeBadOrders();

		System.out.println("Before: " + best.getScore());
		best.doRandomSwaps((int) 1e7);
		System.out.println("After: " + best.getScore());

		showSolution(best);
		best.storeSafely();

		long endTime = System.currentTimeMillis();
		System.err.println("TIME TAKEN: " + (endTime - startTime) + "ms");
		System.err.println("USED SEED: we don't know...");
	}

	private static WeekSchema improveAsync(WeekSchema startSolution) {
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

	private static WeekSchema improveSync(WeekSchema startSolution) {
		return RandomAdder.iterate(startSolution, 100);
	}
}
