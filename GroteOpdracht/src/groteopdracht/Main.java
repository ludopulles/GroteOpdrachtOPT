package groteopdracht;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import groteopdracht.datastructures.DagSchema;
import groteopdracht.datastructures.InsertIndex;
import groteopdracht.datastructures.Order;
import groteopdracht.datastructures.WeekSchema;

public class Main {

	public static final String[] DAYS = { "ma", "di", "wo", "do", "vr" };
	public static final boolean DEBUG = true;
	public static final int MINUTE_CONVERSION = 600;

	public static void main(String[] args) throws IOException {
		// WeekSchema.main(args);
		// Order.main(args);
		
		Integer[] byPenalty = new Integer[Order.ORDERS_IDS - 1];
		for (int i = 0; i < byPenalty.length; i++) {
			byPenalty[i] = i + 1;
		}
		// FREQUENCY DESCENDING 
		Arrays.sort(byPenalty, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Order.orders[o2].penalty() - Order.orders[o1].penalty();
			}
		});
		WeekSchema ws = new WeekSchema();
		for (int nr = 0; nr < byPenalty.length; nr++) {
			int i = byPenalty[nr];
			int freq = Order.orders[i].frequentie;
			// try to fit o in the solution
			InsertIndex[] indices = new InsertIndex[5];
			for (int day = 0; day < 5; day++) {
				indices[day] = ws.bestInsertIndex(day, i);
//				if (nr < 10)
//					System.out.println(i + ", " + day + ": " + Order.orders[i].penalty() + " : " + indices[day].canAdd);
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
					ws.insert(bestIndex, indices[bestIndex], i);
					System.out.println("INSERT " + i + " ON " + DAYS[bestIndex]);
				}
			} else if (freq == 2) {
				// two possibilities: (0, 3) and (1, 4)
				boolean can1 = indices[0].canAdd && indices[3].canAdd;
				boolean can2 = indices[1].canAdd && indices[4].canAdd;
//				System.out.println(can1 + ", " + can2);
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
					if (pickFirst) {
						ws.insert(0, indices[0], i);
						ws.insert(3, indices[3], i);
						System.out.println("INSERT " + i + " ON " + DAYS[0] + " AND " + DAYS[3]);
					} else {
						ws.insert(1, indices[1], i);
						ws.insert(4, indices[4], i);
						System.out.println("INSERT " + i + " ON " + DAYS[1] + " AND " + DAYS[4]);
					}
				}
			} else if (freq == 3) {
				if (indices[0].canAdd && indices[2].canAdd && indices[4].canAdd) {
					ws.insert(0, indices[0], i);
					ws.insert(2, indices[2], i);
					ws.insert(4, indices[4], i);
					System.out.println("INSERT " + i + " ON " + DAYS[0] + ", " + DAYS[2] + " AND " + DAYS[4]);
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
					for (int j = 0; j < 5; j++) {
						if (j == worstIndex) continue;
						ws.insert(j, indices[j], i);
					}
					System.out.println("DONT INSERT " + i + " ON " + DAYS[worstIndex]);
				}
			}
		}
		// TODO: dump time
		BufferedWriter output = new BufferedWriter(new FileWriter("solution.txt"));
		

//		WeekSchema pureShit = new WeekSchema();
//		pureShit.insert(0, new InsertIndex(0, 20), 964);
//		pureShit.printSolutionFormat(output);
		
		ws.printSolutionFormat(output);
		output.flush();
		output.close();
	}
}
