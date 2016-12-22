
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
	public static final long SEED = System.currentTimeMillis();
	private static final Random RAND = new Random(SEED);
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

	public WeekSchema(WeekSchema copy, int perm) {
		for (int i = 0; i < 5; i++) {
			this.weekschema[i] = new DagSchema(
					copy.weekschema[Constants.fivePerms[perm][i]]);
		}
		this.penalty = copy.penalty;
		this.travelTime = copy.travelTime;
		this.usedOrders = copy.usedOrders;
		this.isCollected = (BitSet) copy.isCollected.clone();
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
					this.insert(day,
							new InsertIndex(vNr, Constants.DROP_TIME
									+ Order.orders[order].timeIncrease(0, 0)),
							order);
					makeNew = false;
				} else {
					this.insert(day,
							new InsertIndex(vNr, routeNr, routeIndex,
									Order.orders[order].timeIncrease(prev, 0)),
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
					w.write((vNr + 1) + "; " + (day + 1) + "; " + sequence++
							+ "; " + Order.orders[k].orderID);
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
		if (isCollected.get(order)) return new InsertIndex();
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
		return 1.0D / Constants.MINUTE_CONVERSION
				* (this.penalty + this.travelTime);
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

	private void removeOrder(int order) {
		this.isCollected.clear(order);
		this.penalty += Order.orders[order].penalty;
		int diff = 0;
		for (int day = 0; day < 5; day++) {
			diff += this.weekschema[day].removeOrder(0, order);
			diff += this.weekschema[day].removeOrder(1, order);
		}
		this.travelTime -= diff;
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
	
	public void addOrder(int order) {
		int freq = Order.orders[order].frequency;
		InsertIndex[] indices = new InsertIndex[5];
		for (int day = 0; day < 5; day++) {
			indices[day] = bestInsertIndex(day, order);
		}
		if (freq == 1) {
			// get best
			int numCan = 0, bestIndex = -1;
			for (int j = 0; j < 5; j++) {
				if (indices[j].canAdd) {
					numCan++;
					if (bestIndex == -1 || indices[bestIndex]
							.compareTo(indices[j]) < 0) {
						bestIndex = j;
					}
				}
			}
			if (numCan > 0) {
				insertOrder(order);
				insert(bestIndex, indices[bestIndex], order);
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
				insertOrder(order);
				if (pickFirst) {
					insert(0, indices[0], order);
					insert(3, indices[3], order);
				} else {
					insert(1, indices[1], order);
					insert(4, indices[4], order);
				}
			}
		} else if (freq == 3) {
			if (indices[0].canAdd && indices[2].canAdd
					&& indices[4].canAdd) {
				insertOrder(order);
				insert(0, indices[0], order);
				insert(2, indices[2], order);
				insert(4, indices[4], order);
			}
		} else if (freq == 4) {
			// remove worst
			int numCan = 0, worstIndex = -1, notDrop = -1;
			for (int j = 0; j < 5; j++) {
				if (indices[j].canAdd) {
					numCan++;
					if (worstIndex == -1 || indices[worstIndex]
							.compareTo(indices[j]) > 0) {
						worstIndex = j;
					}
				} else {
					notDrop = j;
				}
			}
			if (numCan == 4) worstIndex = notDrop;
			if (numCan >= 4) {
				insertOrder(order);
				for (int j = 0; j < 5; j++) {
					if (j == worstIndex) continue;
					insert(j, indices[j], order);
				}
			}
		}
	}

	public void addInOrder(List<Integer> permutation) {
		for (int i : permutation) {
			// if (freq == 1) continue;
			// try to fit o in the solution
			addOrder(i);
			
		}
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
			
			diff = 0;
			for (int day = 0; day < 5; day++) {
				
			}
			if (diff > 0) improved = true;
			this.travelTime -= diff;
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
								if (isCollected(order)
										|| curOrder.frequency != 1
										|| !r.canAdd(order))
									continue;
								int newTime = availableTime - r.time
										- curOrder.timeIncrease(prev, 0);
								if (newTime < 0) continue;
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
	
	public WeekSchema simAnnealSwap(double startT, double alpha, int maxIterations) {
		/*
		 * Ongeveer 8 keer de buurtruimte, we swappen twee orders, dit kan op 1150^2 manieren,
		 * maar dan tellen we dubbel, dus delen door 2.
		 */
		int Q = 4 * 1150 * 1150;
		double T = startT;
		
		WeekSchema best = this;
		WeekSchema current = this;
		boolean once = true;
		boolean stop = false;
		int iterations = 1;
		
		int totalBads = 0;
		int badAccepts = 0;
		
		while(!stop) {			
			// copy our object
			WeekSchema newSol = new WeekSchema(current);
			// do a random swap
			while(!newSol.randomSimSwap());
			double newSolScore = newSol.getScore();
			//newSol.doOpts();
			
			double curScore = current.getScore();
			if (newSolScore <= curScore) {
				//System.out.println("Accepted improvement!");
				//System.out.println(newSolScore + " vs " + curScore + " BEST: " + best.getScore());
				current = newSol;
			} else {
				totalBads++;
				if (accept(curScore, newSolScore, T)) {//accept it
				//System.out.println("Accepted worse solution with chance " + Math.exp((curScore - newSolScore)/T));
				//System.out.println(newSolScore + " vs " + curScore + " BEST: " + best.getScore());
					if(once) {
						newSol.storeSafely();
						System.out.println("Saved " + newSol.getScore());
						once = false;
					}
					current = newSol;
					badAccepts++;
				} 
			}
			if (current.getScore() <= best.getScore()) { //update the overall best if needed
				best = current;
			}
			if (iterations % 1000 == 0) {
				System.err.println(iterations);
			}
			if (iterations > maxIterations) {
				stop = true;
			}
			if ((iterations++) % Q == 0) {
				T *= alpha;
				double badsPercent = (1.0 * badAccepts) / totalBads;
				System.err.println("Accepted " + badsPercent*100 + "% bad neighbors");
				stop = true;
				if (badsPercent <= 0.02) {
					stop = true;
				}
				badAccepts = 0;
				totalBads = 0;
			}
		}
		return best;
		
	}
	
	private boolean randomSimSwap() {
		int day1 = RAND.nextInt(5);
		int day2 = RAND.nextInt(5);
		int v1 = RAND.nextInt(2);
		int v2 = RAND.nextInt(2);
		Route r1 = this.weekschema[day1].getRandomRoute(v1, RAND);
		Route r2 = this.weekschema[day2].getRandomRoute(v2, RAND);
		
		int orderIdx1 = RAND.nextInt(r1.length());
		int order1 = r1.get(orderIdx1);
		int lorder1 = (orderIdx1 == 0) ? 0 : r1.get(orderIdx1 - 1);
		int rorder1 = (orderIdx1 == r1.length() - 1) ? 0
				: r1.get(orderIdx1 + 1);
		
		int orderIdx2 = RAND.nextInt(r2.length());
		int order2 = r2.get(orderIdx2);
		int lorder2 = (orderIdx2 == 0) ? 0 : r2.get(orderIdx2 - 1);
		int rorder2 = (orderIdx2 == r2.length() - 1) ? 0
				: r2.get(orderIdx2 + 1);
		
		if (r1 == r2) {
			return false;
		}
		// for now, consider only frequency 1 orders for swap.
		if (Order.orders[order1].frequency != 1
				|| Order.orders[order2].frequency != 1) {
			return false;
		}
		// Check if we have capacity
		if (!r1.canSet(orderIdx1, order2) || !r2.canSet(orderIdx2, order1)) {
			return false;
		}
		// old distances.
		int oldRoute1 = dist(lorder1, order1) + dist(order1, rorder1)
				+ Order.orders[order1].emptyTime;
		int oldRoute2 = dist(lorder2, order2) + dist(order2, rorder2)
				+ Order.orders[order2].emptyTime;
		int newRoute1 = dist(lorder1, order2) + dist(order2, rorder1)
				+ Order.orders[order2].emptyTime;
		int newRoute2 = dist(lorder2, order1) + dist(order1, rorder2)
				+ Order.orders[order1].emptyTime;

		if (this.weekschema[day1].getTime(v1) - (newRoute1 - oldRoute1) <= 0) {
			return false;
		}
		if (this.weekschema[day2].getTime(v2) - (newRoute2 - oldRoute2) <= 0) {
			return false;
		}
		r1.set(orderIdx1, order2);
		this.weekschema[day1].addTime(v1, newRoute1 - oldRoute1);
		r2.set(orderIdx2, order1);
		this.weekschema[day2].addTime(v2, newRoute2 - oldRoute2);
		this.travelTime += (newRoute1 - oldRoute1);
		this.travelTime += (newRoute2 - oldRoute2);
		return true;
	}
	
	private boolean accept(double x, double y, double T) {
		double chance = Math.exp((x - y)/T);
		return WeekSchema.RAND.nextDouble() <= chance;
	}
	
	public void doRandomSwaps(int MAX_ITER) {
		int iter = 0;
		while (iter++ < MAX_ITER) { // try 100k random swaps.
			randomSwap();
		}
	}

	public void randomSwap() {
		
		int day1 = RAND.nextInt(5);
		int day2 = RAND.nextInt(5);
		int v1 = RAND.nextInt(2);
		int v2 = RAND.nextInt(2);
		Route r1 = this.weekschema[day1].getRandomRoute(v1, RAND);
		Route r2 = this.weekschema[day2].getRandomRoute(v2, RAND);
		
		if (r1 == r2) {
			return;
		}
		int orderIdx1 = RAND.nextInt(r1.length());
		int order1 = r1.get(orderIdx1);
		int lorder1 = (orderIdx1 == 0) ? 0 : r1.get(orderIdx1 - 1);
		int rorder1 = (orderIdx1 == r1.length() - 1) ? 0
				: r1.get(orderIdx1 + 1);
		int orderIdx2 = RAND.nextInt(r2.length());
		int order2 = r2.get(orderIdx2);
		int lorder2 = (orderIdx2 == 0) ? 0 : r2.get(orderIdx2 - 1);
		int rorder2 = (orderIdx2 == r2.length() - 1) ? 0
				: r2.get(orderIdx2 + 1);
		// for now, consider only frequency 1 orders for swap.
		if (Order.orders[order1].frequency != 1
				|| Order.orders[order2].frequency != 1) {
			return;
		}
		// Check if we have capacity
		if (!r1.canSet(orderIdx1, order2) || !r2.canSet(orderIdx2, order1)) {
			return;
		}
		// old distances.
		int oldRoute1 = dist(lorder1, order1) + dist(order1, rorder1)
				+ Order.orders[order1].emptyTime;
		int oldRoute2 = dist(lorder2, order2) + dist(order2, rorder2)
				+ Order.orders[order2].emptyTime;
		int newRoute1 = dist(lorder1, order2) + dist(order2, rorder1)
				+ Order.orders[order2].emptyTime;
		int newRoute2 = dist(lorder2, order1) + dist(order1, rorder2)
				+ Order.orders[order1].emptyTime;
		int oldTime = oldRoute1 + oldRoute2;
		int newTime = newRoute1 + newRoute2;
		if (newTime >= oldTime) {
			return;
		}
		if (this.weekschema[day1].getTime(v1) - (newRoute1 - oldRoute1) <= 0) {
			return;
		}
		if (this.weekschema[day2].getTime(v2) - (newRoute2 - oldRoute2) <= 0) {
			return;
		}
		// System.out.println("Found better time: " + new_time + " VS " +
		// old_time);
		r1.set(orderIdx1, order2);
		this.weekschema[day1].addTime(v1, newRoute1 - oldRoute1);
		r2.set(orderIdx2, order1);
		this.weekschema[day2].addTime(v2, newRoute2 - oldRoute2);
		this.travelTime += (newRoute1 - oldRoute1);
		this.travelTime += (newRoute2 - oldRoute2);
	}

	public void doOpts() {
		for (int day = 0; day < 5; day++) {
			for (int vNr = 0; vNr < 2; vNr++) {
				opts(day, vNr);
			}
		}
	}

	public WeekSchema simulatedAnnealing() {
		WeekSchema best = this;
		WeekSchema s = this;
		int j = 0;
		for (double T = Constants.startT; j < 10; T *= Constants.alpha, j++) {
			int downgrades = 0, acceptedDowngrade = 0;
			for (int i = Constants.Q; i-- > 0;) {
//				System.out.println("Iteratie " + (j * Constants.Q + i));
				int orderIdx = RAND.nextInt(Constants.ORDER_IDS);
				WeekSchema sNew = new WeekSchema(s);
				if (s.isCollected(orderIdx)) {
					// remove the order
					sNew.removeOrder(orderIdx);
				} else {
					// add the order
					sNew.addOrder(orderIdx);
				}
				double delta = s.getScore() - sNew.getScore();
				if (delta < 0) downgrades++;
				if (RAND.nextDouble() <= Math.exp(delta / T)) {
					s = sNew;
					if (delta < 0) acceptedDowngrade++;
					if (s.getScore() < best.getScore()) {
						best = s;
						System.out.println("BETTER SCORE: " + best.getScore());
					}
				}
			}
			System.out.println(acceptedDowngrade + " / " + downgrades);
		}
		return best;
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
		try (FileWriter fw = new FileWriter(
				Constants.SOLUTIONS_DIR + "/" + name)) {
			BufferedWriter output = new BufferedWriter(fw);
			this.printSolution(output);
			output.flush();
			output.close();
		} catch (IOException e) {
			System.err.println(
					"Failed storing solution with score " + score + " safely.");
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(WeekSchema rhs) {
		return Double.compare(getScore(), rhs.getScore());
	}
}
