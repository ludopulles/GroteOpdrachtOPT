package groteopdracht;

public class RandomAdder extends Thread {
	
	private Optimiser startSolution, best;
	
	public RandomAdder(Optimiser solution) {
		this.startSolution = solution;
		this.best = new Optimiser(startSolution);
	}
	
	public static Optimiser iterate(Optimiser solution, int n) {
		Optimiser best = solution;
		while (n-- > 0) {
			Optimiser cur = new Optimiser(solution);
			cur.addGreedilyRandom();
			cur.doOpts();
			if (cur.compareTo(best) < 0) {
				best = cur;
			}
		}
		return best;
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Optimiser cur = new Optimiser(startSolution);
			cur.addGreedilyRandom();
			cur.doOpts();
			if (cur.compareTo(best) < 0) {
				best = cur;
				System.out.println("Solution found with score: " + best.getScore());
			}
		}
	}
	
	public Optimiser getBest() {
		return best;
	}
	
	public double getBestScore() {
		return best.getScore();
	}
}
