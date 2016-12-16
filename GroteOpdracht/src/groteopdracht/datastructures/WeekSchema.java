
package groteopdracht.datastructures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import groteopdracht.Constants;

public class WeekSchema {

	private DagSchema[] weekschema = new DagSchema[5];
	private int usedOrders = 0, penalty, travelTime;

	private Random rand = new Random();

	private BitSet isCollected = new BitSet(Constants.ORDERS_IDS);

	public WeekSchema() {
		for (int i = 0; i < 5; i++) {
			this.weekschema[i] = new DagSchema();
		}
		this.penalty = this.travelTime = 0;
		for (Order o : Order.orders) {
			this.penalty += o.penalty;
		}
	}

	public WeekSchema(WeekSchema copy) {
		for (int i = 0; i < 5; i++) {
			this.weekschema[i] = new DagSchema(copy.weekschema[i]);
		}
		this.penalty = copy.penalty;
		this.travelTime = copy.travelTime;
		this.isCollected = (BitSet) copy.isCollected.clone();
	}

	public void printSolution(BufferedWriter w) throws IOException {
		for (int i = 0; i < this.weekschema.length; i++) {
			int dag = i + 1;
			//			this.weekschema[i].debugTime("Dag " + i);
			for (int j = 0; j < 2; j++) {
				List<Integer> arr = this.weekschema[i].getIds(j);
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

	public void addRoute(int day, int vNr, Route r) {
		// add to isCollected
		for (Integer i : r.route) {
			if (Order.orders[i].frequency != 1) {
				throw new Error("WAT");
			}
			//			this.insertOrder(i);
		}
		this.weekschema[day].addRoute(vNr, r);
		this.travelTime += r.time;
	}

	public void insertOrder(int order) {
		this.isCollected.set(order);
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

	public boolean isCollected(int order) {
		return this.isCollected.get(order);
	}

	public void twoOpt(int day, int vNr) {
		this.travelTime += this.weekschema[day].twoOpt(vNr);
	}

	public void twoHalfOpt(int day, int vNr) {
		this.travelTime += this.weekschema[day].twoHalfOpt(vNr);
	}

	public void opts(int day, int vNr) {
		this.travelTime += this.weekschema[day].opts(vNr);
	}

	public int getTime(int day, int vNr) {
		return this.weekschema[day].getTime(vNr);
	}

	public List<Integer> exportOrderIds(int day, int vNr) {
		return this.weekschema[day].getOrderIds(vNr);
	}

	private int dist(int o1, int o2) {
		return Afstanden.tijd[Order.orders[o1].matrixID][Order.orders[o2].matrixID];
	}

	public void randomSwap() {
		int dag1 = rand.nextInt(5);
		int dag2 = rand.nextInt(5);

		int wagen1 = rand.nextInt(2);
		int wagen2 = rand.nextInt(2);

		Route r1 = this.weekschema[dag1].getRandomRoute(wagen1, rand);
		Route r2 = this.weekschema[dag2].getRandomRoute(wagen2, rand);

		int order_idx1 = rand.nextInt(r1.length());
		int order1 = r1.get(order_idx1);
		int lorder1 = (order_idx1 == 0) ? 0 : r1.get(order_idx1 - 1);
		int rorder1 = (order_idx1 == r1.length() - 1) ? 0 : r1.get(order_idx1 + 1);

		int order_idx2 = rand.nextInt(r2.length());
		int order2 = r2.get(order_idx2);
		int lorder2 = (order_idx2 == 0) ? 0 : r2.get(order_idx2 - 1);
		int rorder2 = (order_idx2 == r2.length() - 1) ? 0 : r2.get(order_idx2 + 1);

		// for now, consider only frequency 1 orders for swap.
		if (Order.orders[order1].frequency != 1 || Order.orders[order2].frequency != 1) {
			return;
		}

		// Check if we have capacity
		if (!r1.canSet(order_idx1, order2) || !r2.canSet(order_idx2, order1)) {
			return;
		}
		
		// old distances.
		int lo1_to_o1 = dist(lorder1, order1);
		int o1_to_ro1 = dist(order1, rorder1);
		int lo2_to_o2 = dist(lorder2, order2);
		int o2_to_ro2 = dist(order2, rorder2);

		int old_route1 = lo1_to_o1 + o1_to_ro1 + Order.orders[order1].emptyTime;
		int old_route2 = lo2_to_o2 + o2_to_ro2 + Order.orders[order2].emptyTime;
		
		int old_time = old_route1 + old_route2;
		
		int lo1_to_o2 = dist(lorder1, order2);
		int o2_to_ro1 = dist(order2, rorder1);
		int lo2_to_o1 = dist(lorder2, order1);
		int o1_to_ro2 = dist(order1, rorder2);
		
		int new_route1 = lo1_to_o2 + o2_to_ro1 + Order.orders[order2].emptyTime;
		int new_route2 = lo2_to_o1 + o1_to_ro2 + Order.orders[order1].emptyTime;
		
		int new_time = new_route1 + new_route2;
		
		
		if (new_time >= old_time) {
			return;
		}
		if (this.weekschema[dag1].getTime(wagen1) - (new_route1 - old_route1) <= 0) {
			return;
		}
		if (this.weekschema[dag2].getTime(wagen2) - (new_route2 - old_route2) <= 0) {
			return;
		}
		
		System.out.println("Found better time: " + new_time + " VS " + old_time);
		int old = r1.time;
		r1.set(order_idx1, order2);
		int newt = r1.time;
		
		System.out.println("ROUTE: " + (newt - old) + ", MANUAL: " + (new_route1 - old_route1));
		this.weekschema[dag1].addTime(wagen1, new_route1 - old_route1);
		
		int old2 = r2.time;
		r2.set(order_idx2, order1);
		int newt2 = r2.time;
		
		System.out.println("ROUTE: " + (newt2 - old2) + ", MANUAL: " + (new_route2 - old_route2));
		this.weekschema[dag2].addTime(wagen2, new_route2 - old_route2);
		
		this.travelTime += (new_route1 - old_route1);
		this.travelTime += (new_route2 - old_route2);
	}
}
