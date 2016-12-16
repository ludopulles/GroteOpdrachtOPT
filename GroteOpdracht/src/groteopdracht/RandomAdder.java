package groteopdracht;

import groteopdracht.datastructures.WeekSchema;

public class RandomAdder extends Thread {
	
	private static volatile WeekSchema best = new WeekSchema();
	private WeekSchema startSolution;

	public static WeekSchema getBest() {
		return best;
	}
	
	public RandomAdder(WeekSchema solution) {
		this.startSolution = solution;
		// this.best = new WeekSchema(startSolution);
	}
	
	private static WeekSchema WeekSchemaandom(WeekSchema solution) {
		WeekSchema cur = new WeekSchema(solution);
		cur.addGreedilyRandom();
		cur.doOpts();
		cur.removeBadOrders();
		cur.doOpts();
		return cur;
	}
	
	public static WeekSchema iterate(WeekSchema solution, int n) {
		while (n-- > 0) {
			WeekSchema cur = WeekSchemaandom(solution);
			if (cur.compareTo(best) < 0) best = cur;
		}
		return best;
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			WeekSchema cur = WeekSchemaandom(startSolution);
			if (cur.compareTo(best) < 0) {
				best = cur;
				System.out.println("Solution found with score: " + best.getScore());
			}
		}
	}
}
