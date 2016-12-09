
package groteopdracht.datastructures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import groteopdracht.Constants;

public class WeekSchema {

	private DagSchema[] weekschema = new DagSchema[5];
	private int usedOrders = 0, penalty, travelTime;

	/**
	 * Houdt bij welke orders opgehaald worden.
	 */
	private BitSet isCollected = new BitSet(Constants.ORDERS_IDS);

	public WeekSchema() {
		for (int i = 0; i < 5; i++) {
			this.weekschema[i] = new DagSchema();
		}
		this.penalty = this.travelTime = 0;
		for (Order o : Order.orders) {
			this.penalty += o.penalty;
		}
		System.err.println("Current penalty: " + this.penalty);
	}

	public void printSolution(BufferedWriter w) throws IOException {
		for (int i = 0; i < this.weekschema.length; i++) {
			int dag = i + 1;
			this.weekschema[i].debugTime("Dag " + i);
			for (int j = 0; j < 2; j++) {
				List<Integer> arr = this.weekschema[i].exportRoute(j);
				int sequence = 1;
				for (int k : arr) {
					w.write((j + 1) + "; " + dag + "; " + sequence++ + "; "
							+ Order.orders[k].orderID);
					w.newLine();
				}
			}
		}
	}

	public InsertIndex bestInsertIndex(int day, int order) {
		if (isCollected.get(order)) return new InsertIndex();
		return this.weekschema[day].bestInsertIndex(order);
	}

	public void insertOrder(int order) {
		this.usedOrders++;
		this.penalty -= Order.orders[order].penalty;
	}
	
	public void insert(int day, InsertIndex index, int order) {
		this.weekschema[day].insert(index, order);
		this.travelTime += index.timeInc;
	}

	public int getUsedOrders() {
		return this.usedOrders;
	}
	
	public int getPenalty() {
		return this.penalty;
	}
	
	public int getTravelTime() {
		return this.travelTime;
	}
	
	public double getScore() {
		return 1.0D / Constants.MINUTE_CONVERSION * (this.penalty + this.travelTime);
	}
}
