
package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import groteopdracht.Constants;
import groteopdracht.Main;

public class WeekSchema implements Comparable<WeekSchema> {

	private DagSchema[] weekschema = new DagSchema[5];
	private int usedOrders = 0, penalty, travelTime;

	private Random rand = new Random();
	private BitSet isCollected = new BitSet(Constants.ORDER_IDS);

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
		this.usedOrders = copy.usedOrders;
		this.isCollected = (BitSet) copy.isCollected.clone();
	}

	public WeekSchema(WeekSchema copy, int permutationIndex) {
		this(copy);
	}

	public WeekSchema(BufferedReader br) throws IOException {
		this();
		String line;

		int lastVNr = -1, lastDay = -1;
		int routeNr = -1, routeIndex = -1, prev = 0;
		boolean makeNew = true;

		HashSet<Integer> usedOrders = new HashSet<>();

		while ((line = br.readLine()) != null) {
			String[] parts = line.split(";");
			for (int p = 0; p < parts.length; p++)
				parts[p] = parts[p].trim();
			int vNr = Integer.parseInt(parts[0]) - 1;
			int day = Integer.parseInt(parts[1]) - 1;
			// int seq = Integer.parseInt(parts[2]) - 1;
			int order = Order.invOrderIDs.get(Integer.parseInt(parts[3]));
			usedOrders.add(order);

			if (day != lastDay || vNr != lastVNr) {
				routeNr = 0;
				routeIndex = 0;
				prev = 0;
			}
			if (order == 0) {
				makeNew = true;
				routeNr++;
				routeIndex = 0;
				prev = 0;
			} else {
				if (makeNew) {
					this.insert(day, new InsertIndex(vNr, Constants.DROP_TIME + Order.orders[order].timeIncrease(0, 0)),
							order);
					makeNew = false;
				} else {
					this.insert(day,
							new InsertIndex(vNr, routeNr, routeIndex, Order.orders[order].timeIncrease(prev, 0)),
							order);
				}
				routeIndex++;
			}

			lastVNr = vNr;
			lastDay = day;
			prev = order;
		}
		for (int order : usedOrders) {
			this.insertOrder(order);
		}
	}

	public void printSolution(BufferedWriter w) throws IOException {
		for (int day = 0; day < 5; day++) {
			for (int vNr = 0; vNr < 2; vNr++) {
				List<Integer> arr = this.weekschema[day].getIds(vNr);
				int sequence = 1;
				for (int k : arr) {
					w.write((vNr + 1) + "; " + (day + 1) + "; " + sequence++ + "; " + Order.orders[k].orderID);
					w.newLine();
				}
			}
		}
		Main.infoMsg("Used orders: " + getUsedOrders());
		Main.infoMsg("Travel time: " + getTravelTime());
		Main.infoMsg("Penalty:     " + getPenalty());
		Main.infoMsg("Score:       " + getScore());
	}

	public InsertIndex bestInsertIndex(int day, int order) {
		if (isCollected.get(order))
			return new InsertIndex();
		return this.weekschema[day].bestInsertIndex(order);
	}

	public void addRoute(int day, int vNr, Route r) {
		// add to isCollected
		for (Integer i : r.route) {
			if (Order.orders[i].frequency != 1) {
				throw new Error("WAT");
			}
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

	/**
	 * Throws away orders, which cost more time, than the penalty.
	 */
	public void removeBadOrders() {
		// calculate the score improvement if we don't do this order.
		boolean improved = true;
		int[] times = new int[Constants.ORDER_IDS];
		while (improved) {
			improved = false;
			for (int i = 1; i < Constants.ORDER_IDS; i++) {
				times[i] = Order.orders[i].penalty;
			}

			for (int day = 0; day < 5; day++) {
				this.weekschema[day].removeTimes(times, 0);
				this.weekschema[day].removeTimes(times, 1);
			}

			int diff = 0;
			for (int day = 0; day < 5; day++) {
				diff += this.weekschema[day].removeAllNegatives(times, 0);
				diff += this.weekschema[day].removeAllNegatives(times, 1);
			}
			this.travelTime -= diff;
			for (int i = 1; i < Constants.ORDER_IDS; i++) {
				if (times[i] < 0 && this.isCollected(i)) {
					this.penalty += Order.orders[i].penalty;
					this.isCollected.clear(i);
					this.usedOrders--;
					improved = true;
				}
			}
		}
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

		if (r1 == r2) {
			return;
		}

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

		// System.out.println("Found better time: " + new_time + " VS " +
		// old_time);

		r1.set(order_idx1, order2);
		this.weekschema[dag1].addTime(wagen1, new_route1 - old_route1);

		r2.set(order_idx2, order1);
		this.weekschema[dag2].addTime(wagen2, new_route2 - old_route2);

		this.travelTime += (new_route1 - old_route1);
		this.travelTime += (new_route2 - old_route2);
	}

	/***************************************************************************
	 * OPTIMISE STUFF
	 ***************************************************************************
	 */

	public void addGreedilyRandom() {
		List<Integer> random = new ArrayList<Integer>();
		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			random.add(i);
		}
		Collections.shuffle(random, Constants.RANDOM);
		this.addInOrder(random);
	}

	public void addGreedily(Comparator<Integer> orderComparator) {
		List<Integer> sorted = new ArrayList<Integer>();
		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			sorted.add(i);
		}
		Collections.sort(sorted, orderComparator);
		this.addInOrder(sorted);
	}

	public void addInOrder(List<Integer> permutation) {
		for (int i : permutation) {
			int freq = Order.orders[i].frequency;
			// if (freq == 1) continue;
			// try to fit o in the solution
			InsertIndex[] indices = new InsertIndex[5];
			for (int day = 0; day < 5; day++) {
				indices[day] = bestInsertIndex(day, i);
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
					insertOrder(i);
					insert(bestIndex, indices[bestIndex], i);
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
					insertOrder(i);
					if (pickFirst) {
						insert(0, indices[0], i);
						insert(3, indices[3], i);
					} else {
						insert(1, indices[1], i);
						insert(4, indices[4], i);
					}
				}
			} else if (freq == 3) {
				if (indices[0].canAdd && indices[2].canAdd && indices[4].canAdd) {
					insertOrder(i);
					insert(0, indices[0], i);
					insert(2, indices[2], i);
					insert(4, indices[4], i);
				}
			} else if (freq == 4) {
				// remove worst
				int numCan = 0, worstIndex = -1, notDrop = -1;
				for (int j = 0; j < 5; j++) {
					if (indices[j].canAdd) {
						numCan++;
						if (worstIndex == -1 || indices[worstIndex].compareTo(indices[j]) > 0) {
							worstIndex = j;
						}
					} else {
						notDrop = j;
					}
				}
				if (numCan == 4)
					worstIndex = notDrop;
				if (numCan >= 4) {
					insertOrder(i);
					for (int j = 0; j < 5; j++) {
						if (j == worstIndex)
							continue;
						insert(j, indices[j], i);
					}
				}
			}
		}
	}

	public void addClosest() {
		// consider only 1PWK orders

		boolean routeAdded = true;
		while (routeAdded) {
			routeAdded = false;
			for (int day = 0; day < 5; day++) {
				for (int vNr = 0; vNr < 2; vNr++) {
					Route r = new Route();
					int availableTime = getTime(day, vNr);
					int prev = 0;
					while (true) {
						int minOrder = -1, minTime = 1000 * 1000 * 1000;
						for (int matrixID = 0; matrixID < Constants.MATRIX_IDS; matrixID++) {
							for (int order : Order.atLocation.get(matrixID)) {
								Order curOrder = Order.orders[order];
								if (isCollected(order) || curOrder.frequency != 1 || !r.canAdd(order))
									continue;
								int newTime = availableTime - r.time - curOrder.timeIncrease(prev, 0);
								if (newTime < 0)
									continue;
								int alt = Afstanden.tijd[Order.orders[prev].matrixID][matrixID];
								// if (alt < minTime || (alt == minTime &&
								// Math.random() < 0.5)) {
								if (alt < minTime) {
									minTime = alt;
									minOrder = order;
								}
							}
						}
						if (minOrder == -1) {
							break;
						}
						r.append(minOrder);
						insertOrder(minOrder);
						prev = minOrder;
					}
					if (r.length() == 0) {
						continue;
					}
					routeAdded = true;
					addRoute(day, vNr, r);
				}
			}
		}
	}

	public void doRandomSwaps(int MAX_ITER) {
		int iter = 0;
		while (iter++ < MAX_ITER) { // try 100k random swaps.
			randomSwap();
		}
	}

	public void doOpts() {
		for (int day = 0; day < 5; day++) {
			for (int vNr = 0; vNr < 2; vNr++) {
				opts(day, vNr);
				// twoOpt(day, vNr);
				// twoHalfOpt(day, vNr);
			}
		}
	}

	public void optimiseOrders() {
		// check if we can swap orders, to other places, so that the time is
		// reduced!
	}

	public void storeSafely() {
		File dir = new File(Constants.SOLUTIONS_DIR);

		// Assume a score between 1000 and 9999
		long score = Math.round(this.getScore());
		String name = "score" + score + ".txt";

		int idx = 0; // number of files which has a score less than this one
		for (File f : dir.listFiles()) {
			if (name.compareTo(f.getName()) >= 0) {
				// there are too many files better than this one.
				if (name.equalsIgnoreCase(f.getName()) || ++idx >= 10) {
					return;
				}
			} else {
				break;
			}
		}

		try (FileWriter fw = new FileWriter(Constants.SOLUTIONS_DIR + "/" + name)) {
			BufferedWriter output = new BufferedWriter(fw);
			this.printSolution(output);
			output.flush();
			output.close();
		} catch (IOException e) {
			System.err.println("Failed storing solution with score " + score + " safely.");
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(WeekSchema rhs) {
		return Double.compare(getScore(), rhs.getScore());
	}
}
