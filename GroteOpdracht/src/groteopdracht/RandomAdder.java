package groteopdracht;

public class RandomAdder extends Thread {
	
	private static volatile Optimiser best = new Optimiser();
	private Optimiser startSolution;

	public static Optimiser getBest() {
		return best;
	}
	
	public RandomAdder(Optimiser solution) {
		this.startSolution = solution;
		// this.best = new Optimiser(startSolution);
	}
	
	private static Optimiser optimiseRandom(Optimiser solution) {
		Optimiser cur = new Optimiser(solution);
		cur.addGreedilyRandom();
		cur.doOpts();
		cur.removeBadOrders();
		cur.doOpts();
		return cur;
	}
	
	public static Optimiser iterate(Optimiser solution, int n) {
		while (n-- > 0) {
			Optimiser cur = optimiseRandom(solution);
			if (cur.compareTo(best) < 0) best = cur;
		}
		return best;
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Optimiser cur = optimiseRandom(startSolution);
			if (cur.compareTo(best) < 0) {
				best = cur;
				System.out.println("Solution found with score: " + best.getScore());
			}
		}
	}
}
