
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
			this.weekschema[i] = new DagSchema(copy.weekschema[Constants.fivePerms[perm][i]]);
		}
		this.penalty = copy.penalty;
		this.travelTime = copy.travelTime;
		this.usedOrders = copy.usedOrders;
		this.isCollected = (BitSet) copy.isCollected.clone();
	}

	public WeekSchema(WeekSchema copy, int[] tenPerm) {
		for (int i = 0; i < 5; i++) {
			List<Route> r1, r2;
			int t1, t2, i1 = tenPerm[2 * i], i2 = tenPerm[2 * i + 1];
			r1 = i1 % 2 == 0 ? copy.weekschema[i1 / 2].v1 : copy.weekschema[i1 / 2].v2;
			t1 = i1 % 2 == 0 ? copy.weekschema[i1 / 2].t1 : copy.weekschema[i1 / 2].t2;
			r2 = i2 % 2 == 0 ? copy.weekschema[i2 / 2].v1 : copy.weekschema[i2 / 2].v2;
			t2 = i2 % 2 == 0 ? copy.weekschema[i2 / 2].t1 : copy.weekschema[i2 / 2].t2;
			this.weekschema[i] = new DagSchema(r1, r2, t1, t2);
		}
		this.penalty = copy.penalty;
		this.travelTime = copy.travelTime;
		this.usedOrders = copy.usedOrders;
		this.isCollected = (BitSet) copy.isCollected.clone();

		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			if (!isCollected(i) || Order.orders[i].frequency == 1)
				continue;
			this.removeOrder(i);
		}
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

	private boolean checkScore(boolean print) {
		int _travelTime = 0, _penalty = 0;
		BitSet collected = new BitSet(Constants.ORDER_IDS);
		for (int day = 0; day < 5; day++) {
			_travelTime += this.weekschema[day].checkTime(0);
			_travelTime += this.weekschema[day].checkTime(1);
			this.weekschema[day].checkOrders(collected);
		}
		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			if (!collected.get(i))
				_penalty += Order.orders[i].penalty;
		}
		if (travelTime != _travelTime || penalty != _penalty) {
			if (print) {
				System.err.println("Different score!");
				System.err.println("TRAVELT Found: " + travelTime + ", actual: " + _travelTime);
				System.err.println("PENALTY Found: " + penalty + ", actual: " + _penalty);
			}
			return false;
		}
		return true;
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
		this.addInOrder(random, true);
	}

	public void addGreedily(Comparator<Integer> orderComparator) {
		List<Integer> sorted = new ArrayList<Integer>();
		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			sorted.add(i);
		}
		Collections.sort(sorted, orderComparator);
		this.addInOrder(sorted, true);
	}

	public void addImprovements() {
		List<Integer> random = new ArrayList<Integer>();
		for (int i = 1; i < Constants.ORDER_IDS; i++) {
			random.add(i);
		}
		Collections.shuffle(random, Constants.RANDOM);
		this.addInOrder(random, false);
	}

	public void addOrder(int order, boolean addAlways) {
		if (isCollected(order))
			return;

		int penaltyDec = Order.orders[order].penalty; // decrease in penalty
														// when added
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
					if (bestIndex == -1 || indices[bestIndex].compareTo(indices[j]) < 0) {
						bestIndex = j;
					}
				}
			}
			if (numCan > 0) {
				if (addAlways || indices[bestIndex].timeInc <= penaltyDec) {
					insertOrder(order);
					insert(bestIndex, indices[bestIndex], order);
				}
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
				if (pickFirst) {
					if (addAlways || indices[0].timeInc + indices[3].timeInc <= penaltyDec) {
						insertOrder(order);
						insert(0, indices[0], order);
						insert(3, indices[3], order);
					}
				} else {
					if (addAlways || indices[1].timeInc + indices[4].timeInc <= penaltyDec) {
						insertOrder(order);
						insert(1, indices[1], order);
						insert(4, indices[4], order);
					}
				}
			}
		} else if (freq == 3) {
			if (indices[0].canAdd && indices[2].canAdd && indices[4].canAdd) {
				if (addAlways || indices[0].timeInc + indices[2].timeInc + indices[4].timeInc <= penaltyDec) {
					insertOrder(order);
					insert(0, indices[0], order);
					insert(2, indices[2], order);
					insert(4, indices[4], order);
				}
			}
		} else if (freq == 4) {
			// remove worst
			int numCan = 0, worstIndex = -1, notDrop = -1;
			int tInc = 0;
			for (int j = 0; j < 5; j++) {
				if (indices[j].canAdd) {
					numCan++;
					tInc += indices[j].timeInc;
					if (worstIndex == -1 || indices[worstIndex].compareTo(indices[j]) > 0) {
						worstIndex = j;
					}
				} else {
					notDrop = j;
				}
			}
			if (numCan == 4) {
				worstIndex = notDrop;
			} else {
				tInc -= indices[worstIndex].timeInc;
			}
			if (numCan >= 4) {
				if (addAlways || tInc <= penaltyDec) {
					insertOrder(order);
					for (int j = 0; j < 5; j++) {
						if (j == worstIndex)
							continue;
						insert(j, indices[j], order);
					}
				}
			}
		}
	}

	public void addInOrder(List<Integer> permutation, boolean addAlways) {
		for (int i : permutation) {
			// if (freq == 1) continue;
			// try to fit o in the solution
			addOrder(i, addAlways);
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
			if (diff > 0)
				improved = true;
			this.travelTime -= diff;
		}
	}

	public void addClosest() {
		// consider only 1PWK orders
		boolean[][] cantAdd = new boolean[5][2];
		while (true) {
			int bday = -1, btruck = -1, btime = -1;
			for (int day = 0; day < 5; day++) {
				for (int truck = 0; truck < 2; truck++) {
					if (cantAdd[day][truck])
						continue;
					if (this.weekschema[day].getTime(truck) > btime) {
						bday = day;
						btruck = truck;
						btime = this.weekschema[day].getTime(truck);
					}
				}
			}
			if (bday == -1)
				break;
			Route r = new Route();
			int availableTime = this.weekschema[bday].getTime(btruck);
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
				cantAdd[bday][btruck] = true;
			} else {
				addRoute(bday, btruck, r);
			}
		}
	}

	public WeekSchema simAnnealSwap(double T, double alpha, int maxIterations) {
		// double avgDiff = 0.0; int nCounted = 0;
		/*
		 * Ongeveer 8 keer de buurtruimte, we swappen twee orders, dit kan op
		 * 1150^2 manieren, maar dan tellen we dubbel, dus delen door 2.
		 */
		// int Q = 4 * 1150 * 1150;
		int Q = 10000;

		WeekSchema best = this, curSol = this;
		double bestScore = best.getScore(), curScore = curSol.getScore();

		int totalBads = 0, badAccepts = 0;
		int tDecrease = Q;
		while (maxIterations-- > 0) {
			// copy our object
			WeekSchema newSol = new WeekSchema(curSol);
			if (RAND.nextInt(100) < 0) {
				int orderIdx = RAND.nextInt(Constants.ORDER_IDS);
				if (newSol.isCollected(orderIdx))
					newSol.removeOrder(orderIdx);
				else
					newSol.addOrder(orderIdx, true);

				// nCounted++;
				// avgDiff += newSol.getScore() - curScore;
				// if (newSol.getScore() < curScore) {
				// System.err.println("Improvement by random " +
				// newSol.isCollected(orderIdx) + " at " + orderIdx + " -> "
				// + newSol.getScore() + " VS " + curScore);
				// }
			} else {
				for (int maxTries = 10000; maxTries > 0 && !newSol.randomSimSwap(); maxTries--) {
				}
			}

			// do a random swap

			// System.err.print(maxIterations);
			// newSol.doOpts();
			// System.err.println(" - " + maxIterations);

			double newScore = newSol.getScore();
			boolean accept = newScore < curScore;
			if (accept && newScore < bestScore) {
				System.err.println("SIMANNEAL FOUND: " + newScore);
				best = newSol;
				bestScore = newScore;
			}
			if (!accept) {
				totalBads++;
				accept = accept(curScore, newScore, T);
				if (accept)
					badAccepts++;
			}
			if (accept) {
				curSol = newSol;
				curScore = newScore;
				// System.err.println("CURRENT FOUND: " + newScore);
			}

			if (--tDecrease == 0) {
				tDecrease = Q;
				System.err
						.println(String.format("%.4f", 100.0 * badAccepts / totalBads) + "% bad acc, iterations left: "
								+ maxIterations + ", best: " + bestScore + ". cur: " + curScore);
				if (100 * badAccepts <= 2 * totalBads)
					break;

				T *= alpha;
				badAccepts = totalBads = 0;
			}
		}
		System.err.println("SIMULATED ANNEALING ENDED WITH BEST: " + bestScore + ", CURRENT: " + curScore);
		// System.err.println("AVERAGE SCORE DIFF " + (avgDiff / nCounted));
		return best;

	}

	public boolean randomSimSwap() {
		int day1 = RAND.nextInt(5), v1 = RAND.nextInt(2);
		if (this.weekschema[day1].routesEmpty(v1))
			return false;
		Route r1 = this.weekschema[day1].getRandomRoute(v1, RAND);
		int orderIdx1 = RAND.nextInt(r1.length()), order1 = r1.get(orderIdx1);
		int lorder1 = (orderIdx1 == 0) ? 0 : r1.get(orderIdx1 - 1);
		int rorder1 = (orderIdx1 == r1.length() - 1) ? 0 : r1.get(orderIdx1 + 1);

		int day2 = RAND.nextInt(5), v2 = RAND.nextInt(2);
		if (this.weekschema[day2].routesEmpty(v2))
			return false;
		Route r2 = this.weekschema[day2].getRandomRoute(v2, RAND);
		int orderIdx2 = RAND.nextInt(r2.length()), order2 = r2.get(orderIdx2);
		int lorder2 = (orderIdx2 == 0) ? 0 : r2.get(orderIdx2 - 1);
		int rorder2 = (orderIdx2 == r2.length() - 1) ? 0 : r2.get(orderIdx2 + 1);

		// for now, consider only frequency 1 orders for swap.
		if (Order.orders[order1].frequency != 1 || Order.orders[order2].frequency != 1) {
			return false;
		}

		if (r1 == r2 && Math.abs(orderIdx1 - orderIdx2) <= 1) {
			if (orderIdx1 == orderIdx2)
				return false;

			// special case
			int l = Math.min(orderIdx1, orderIdx2), r = l + 1;
			int orderl = r1.get(l), orderr = r1.get(r);
			int ll = l == 0 ? 0 : r1.get(l - 1);
			int rr = r == r1.length() - 1 ? 0 : r1.get(r + 1);
			int oldRoute = dist(ll, orderl) + dist(orderl, orderr) + dist(orderr, rr);
			int newRoute = dist(ll, orderr) + dist(orderr, orderl) + dist(orderl, rr);
			if (this.weekschema[day1].getTime(v1) + oldRoute - newRoute <= 0)
				return false;

			r1.set(l, orderr);
			r1.set(r, orderl);
			this.weekschema[day1].addTime(v1, newRoute - oldRoute);
			this.travelTime += newRoute - oldRoute;
			return true;
		}
		// Check if we have capacity
		if (!r1.canSet(orderIdx1, order2) || !r2.canSet(orderIdx2, order1))
			return false;

		// old distances.
		int oldRoute1 = dist(lorder1, order1) + dist(order1, rorder1) + Order.orders[order1].emptyTime;
		int oldRoute2 = dist(lorder2, order2) + dist(order2, rorder2) + Order.orders[order2].emptyTime;
		int newRoute1 = dist(lorder1, order2) + dist(order2, rorder1) + Order.orders[order2].emptyTime;
		int newRoute2 = dist(lorder2, order1) + dist(order1, rorder2) + Order.orders[order1].emptyTime;

		if (this.weekschema[day1].getTime(v1) + oldRoute1 - newRoute1 < 0)
			return false;
		if (this.weekschema[day2].getTime(v2) + oldRoute2 - newRoute2 < 0)
			return false;

		r1.set(orderIdx1, order2);
		this.weekschema[day1].addTime(v1, newRoute1 - oldRoute1);
		r2.set(orderIdx2, order1);
		this.weekschema[day2].addTime(v2, newRoute2 - oldRoute2);
		this.travelTime += (newRoute1 - oldRoute1);
		this.travelTime += (newRoute2 - oldRoute2);
		return true;
	}

	/**
	 * @param x
	 *            the score of the old solution
	 * @param y
	 *            the score of the new solution
	 * @param T
	 *            tweak parameter (higher means higher acceptance rate when
	 *            solution is worse)
	 * @return whether to accept the new solution
	 */
	private boolean accept(double x, double y, double T) {
		double chance = Math.exp((x - y) / T);
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
		if (this.weekschema[day1].routesEmpty(v1) || this.weekschema[day2].routesEmpty(v2))
			return;
		Route r1 = this.weekschema[day1].getRandomRoute(v1, RAND);
		Route r2 = this.weekschema[day2].getRandomRoute(v2, RAND);

		if (r1 == r2) {
			return;
		}
		int orderIdx1 = RAND.nextInt(r1.length());
		int order1 = r1.get(orderIdx1);
		int lorder1 = (orderIdx1 == 0) ? 0 : r1.get(orderIdx1 - 1);
		int rorder1 = (orderIdx1 == r1.length() - 1) ? 0 : r1.get(orderIdx1 + 1);
		int orderIdx2 = RAND.nextInt(r2.length());
		int order2 = r2.get(orderIdx2);
		int lorder2 = (orderIdx2 == 0) ? 0 : r2.get(orderIdx2 - 1);
		int rorder2 = (orderIdx2 == r2.length() - 1) ? 0 : r2.get(orderIdx2 + 1);
		// for now, consider only frequency 1 orders for swap.
		if (Order.orders[order1].frequency != 1 || Order.orders[order2].frequency != 1) {
			return;
		}
		// Check if we have capacity
		if (!r1.canSet(orderIdx1, order2) || !r2.canSet(orderIdx2, order1)) {
			return;
		}
		// old distances.
		int oldRoute1 = dist(lorder1, order1) + dist(order1, rorder1) + Order.orders[order1].emptyTime;
		int oldRoute2 = dist(lorder2, order2) + dist(order2, rorder2) + Order.orders[order2].emptyTime;
		int newRoute1 = dist(lorder1, order2) + dist(order2, rorder1) + Order.orders[order2].emptyTime;
		int newRoute2 = dist(lorder2, order1) + dist(order1, rorder2) + Order.orders[order1].emptyTime;
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
			// int downgrades = 0, acceptedDowngrade = 0;
			for (int i = Constants.Q; i-- > 0;) {
				// System.out.println("Iteratie " + (j * Constants.Q + i));
				int orderIdx = RAND.nextInt(Constants.ORDER_IDS);
				WeekSchema sNew = new WeekSchema(s);
				if (s.isCollected(orderIdx)) {
					// remove the order
					sNew.removeOrder(orderIdx);
				} else {
					// add the order
					sNew.addOrder(orderIdx, true);
				}
				double delta = s.getScore() - sNew.getScore();
				// if (delta < 0)
				// downgrades++;
				if (RAND.nextDouble() <= Math.exp(delta / T)) {
					s = sNew;
					if (delta < 0)
						// acceptedDowngrade++;
						if (s.getScore() < best.getScore()) {
						best = s;
						System.out.println("BETTER SCORE: " + best.getScore());
						}
				}
			}
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
