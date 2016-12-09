package groteopdracht;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import groteopdracht.datastructures.InsertIndex;
import groteopdracht.datastructures.Order;
import groteopdracht.datastructures.WeekSchema;

public class Optimiser {
	
	private WeekSchema solution;
	
	public Optimiser() {
		this.solution = new WeekSchema();
	}
	
	public void addGreedily(Comparator<Integer> orderComparator) {
		Integer[] byPenalty = new Integer[Constants.ORDERS_IDS - 1];
		for (int i = 0; i < byPenalty.length; i++) {
			byPenalty[i] = i + 1;
		}
		Arrays.sort(byPenalty, orderComparator);
		for (int i : byPenalty) {
			int freq = Order.orders[i].frequency;
			// try to fit o in the solution
			InsertIndex[] indices = new InsertIndex[5];
			for (int day = 0; day < 5; day++) {
				indices[day] = this.solution.bestInsertIndex(day, i);
			}
			
			if (freq == 1) {
				// get best
				int numCan = 0, bestIndex = -1;
				for (int j = 0; j < 5; j++) {
					if (indices[j].canAdd) {
						numCan++;
						if (bestIndex == -1 || indices[bestIndex].compareTo(indices[j]) < 0) {
							bestIndex = j;
						}
					}
				}
				if (numCan > 0) {
					this.solution.insertOrder(i);
					this.solution.insert(bestIndex, indices[bestIndex], i);
				}
			} else if (freq == 2) {
				// two possibilities: (0, 3) and (1, 4)
				boolean can1 = indices[0].canAdd && indices[3].canAdd;
				boolean can2 = indices[1].canAdd && indices[4].canAdd;
				if (can1 || can2) {
					boolean pickFirst;
					if (can1 && can2) {
						int opt1 = indices[0].timeInc + indices[3].timeInc;
						int opt2 = indices[1].timeInc + indices[4].timeInc;
						pickFirst = opt1 < opt2;
					} else {
						// !can1 => can2
						pickFirst = can1;
					}
					this.solution.insertOrder(i);
					if (pickFirst) {
						this.solution.insert(0, indices[0], i);
						this.solution.insert(3, indices[3], i);
					} else {
						this.solution.insert(1, indices[1], i);
						this.solution.insert(4, indices[4], i);
					}
				}
			} else if (freq == 3) {
				if (indices[0].canAdd && indices[2].canAdd && indices[4].canAdd) {
					this.solution.insertOrder(i);
					this.solution.insert(0, indices[0], i);
					this.solution.insert(2, indices[2], i);
					this.solution.insert(4, indices[4], i);
				}
			} else if (freq == 4) {
				// remove worst
				int numCan = 0, worstIndex = -1;
				for (int j = 0; j < 5; j++) {
					if (indices[j].canAdd) {
						numCan++;
						if (worstIndex == -1 || indices[worstIndex].compareTo(indices[j]) > 0) {
							worstIndex = j;
						}
					}
				}
				if (numCan >= 4) {
					this.solution.insertOrder(i);
					for (int j = 0; j < 5; j++) {
						if (j == worstIndex) continue;
						this.solution.insert(j, indices[j], i);
					}
				}
			}
		}
	}

	public void optimiseOrders() {
		// check if we can swap orders, to other places, so that the time is reduced!
	}
	
	public void printSolution(BufferedWriter output) throws IOException {
		this.solution.printSolution(output);
		Main.infoMsg("Used orders: " + this.solution.getUsedOrders());
		Main.infoMsg("Penalty: " + this.solution.getPenalty());
		Main.infoMsg("Travel time: " + this.solution.getTravelTime());
		Main.infoMsg("Score: " + this.solution.getScore());
	}
}
