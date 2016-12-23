package groteopdracht;

import java.util.Random;

import groteopdracht.datastructures.WeekSchema;

public class RandomShuffler extends Thread {

	public static volatile WeekSchema best = new WeekSchema();
	private Random r;
	private WeekSchema startSolution;
	
	public RandomShuffler(WeekSchema startSolution) {
		this.startSolution = startSolution;
		this.r = new Random();
		if (best.compareTo(startSolution) > 0) best = startSolution;
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			int[] tenPerm = new int[10];
			for (int i = 0; i < 10; i++) tenPerm[i] = i;
			for (int i = 10; i-- > 0; ) {
				int j = r.nextInt(i + 1);
				int tmp = tenPerm[i];
				tenPerm[i] = tenPerm[j];
				tenPerm[j] = tmp;
			}

			WeekSchema start = new WeekSchema(this.startSolution, tenPerm);
			int n = 1000;
			while (n-- > 0 && !this.isInterrupted()) {
				WeekSchema cur = RandomAdder.optimiseRandom(new WeekSchema(start));
				if (cur.compareTo(best) < 0) {
					best = cur;
					System.out.println("Solution found with score: " + best.getScore());
				}
			}
		}
	}
}
