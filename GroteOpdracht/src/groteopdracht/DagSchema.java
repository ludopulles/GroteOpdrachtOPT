package groteopdracht;

import java.util.LinkedList;

public class DagSchema {

	public static int MAX_TIME = Main.MINUTE_CONVERSION * 12 * 60;
	public static int MAX_CAPACITY = 20000 * 5;

	private LinkedList<Integer> v1, v2;
	public int t1, t2, c1, c2;

	public DagSchema() {
		v1 = new LinkedList<Integer>();
		v2 = new LinkedList<Integer>();
		t1 = t2 = MAX_TIME;
		c1 = c2 = MAX_CAPACITY;
	}
	
	public boolean add(int vehicle, int order, int index) {
		LinkedList<Integer> v = vehicle == 0 ? v1 : v2;
		int newt = (vehicle == 0 ? t1 : t2);
		
		v.add()
	}

	public boolean isValid() {
		if (v1.getLast() != 0 || v2.getLast() != 0) {
			throw new Error("Dumb ass.");
		}
		
		// assumption: v1 and v2 have 0 as the last order.
		for (int k = 0; k < 2; k++) {
			LinkedList<Integer> ref = k == 0 ? v1 : v2;
			int totalTime = MAX_TIME, capacity = MAX_CAPACITY;
			Integer i = 0;
			for (Integer j : ref) {
				totalTime -= Main.time[Main.orders[i].matrixID][Main.orders[j].matrixID];
				totalTime -= Main.orders[j].leegTijd;
				capacity -= Main.orders[j].volume * Main.orders[j].numContainers;
				if (totalTime < 0 || capacity < 0)
					return false;
				i = j;
			}
		}
		return true;
	}
}
