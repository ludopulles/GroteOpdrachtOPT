package groteopdracht;

import groteopdracht.datastructures.WeekSchema;

public class RandomAdder extends Thread {
	
	private static volatile WeekSchema best = new WeekSchema();
	private WeekSchema startSolution;
	private boolean tryPermutations;

	public static WeekSchema getBest() {
		return best;
	}
	
	public RandomAdder(WeekSchema solution, boolean tryPermutations) {
		this.startSolution = solution;
		this.tryPermutations = tryPermutations;
	}
	
	private static WeekSchema optimiseRandom(WeekSchema cur) {
		cur.addGreedilyRandom();
		cur.doOpts();
		cur.removeBadOrders();
		cur.doOpts();
		return cur;
	}
	
	public static WeekSchema iterate(WeekSchema solution, int n) {
		while (n-- > 0) {
			WeekSchema cur = optimiseRandom(new WeekSchema(solution));
			if (cur.compareTo(best) < 0) best = cur;
		}
		return best;
	}
	
	@Override
	public void run() {
		int k = 0;
		while (!this.isInterrupted()) {
			if (this.tryPermutations && --k < 0) k = 119;
			WeekSchema cur = optimiseRandom(new WeekSchema(startSolution, k));
			if (cur.compareTo(best) < 0) {
				best = cur;
				System.out.println("Solution found with score: " + best.getScore() + " at k = " + k);
			}
		}
	}
}
