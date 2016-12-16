
package groteopdracht;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import groteopdracht.datastructures.Afstanden;
import groteopdracht.datastructures.InsertIndex;
import groteopdracht.datastructures.Order;
import groteopdracht.datastructures.Route;
import groteopdracht.datastructures.WeekSchema;

public class Optimiser {

	private WeekSchema solution;

	public Optimiser() {
		this.solution = new WeekSchema();
	}

	public void addGreedily(Comparator<Integer> orderComparator) {
		List<Integer> byPenalty = new ArrayList<Integer>();
		for (int i = 1; i < Constants.ORDERS_IDS; i++) {
			byPenalty.add(i);
		}
		// Arrays.sort(byPenalty, orderComparator);
		Collections.shuffle(byPenalty);
		for (int i : byPenalty) {
			int freq = Order.orders[i].frequency;
			// if (freq == 1) continue;
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
					this.solution.insertOrder(i);
					for (int j = 0; j < 5; j++) {
						if (j == worstIndex)
							continue;
						this.solution.insert(j, indices[j], i);
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
					int availableTime = this.solution.getTime(day, vNr);
					int prev = 0;
					while (true) {
						int minOrder = -1, minTime = 1000 * 1000 * 1000;
						for (int matrixID = 0; matrixID < Constants.MATRIX_IDS; matrixID++) {
							for (int order : Order.atLocation.get(matrixID)) {
								Order curOrder = Order.orders[order];
								if (this.solution.isCollected(order) || curOrder.frequency != 1 || !r.canAdd(order))
									continue;
								int newTime = availableTime - r.time - curOrder.timeIncrease(prev, 0);
								if (newTime < 0)
									continue;
								int alt = Afstanden.tijd[Order.orders[prev].matrixID][matrixID];
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
						this.solution.insertOrder(minOrder);
						prev = minOrder;
					}
					if (r.length() == 0) {
						continue;
					}
					routeAdded = true;
					this.solution.addRoute(day, vNr, r);
				}
			}
		}
	}

	public void doOpts() {
		for (int day = 0; day < 5; day++) {
			for (int vNr = 0; vNr < 2; vNr++) {
				this.solution.twoOpt(day, vNr);
				this.solution.twoHalfOpt(day, vNr);
			}
		}
	}

	public void optimiseOrders() {
		// check if we can swap orders, to other places, so that the time is
		// reduced!
	}

	public void printSolution(BufferedWriter output) throws IOException {
		this.solution.printSolution(output);
		Main.infoMsg("Used orders: " + this.solution.getUsedOrders());
		Main.infoMsg("Penalty: " + this.solution.getPenalty());
		Main.infoMsg("Travel time: " + this.solution.getTravelTime());
		Main.infoMsg("Score: " + this.solution.getScore());
	}

	public double getScore() {
		return this.solution.getScore();
	}

	public WeekSchema getSolution() {
		return this.solution;
	}

	public void storeSafely() {
		long score = Math.round(this.getScore());
		try (FileWriter fw = new FileWriter("solutions/score" + score + ".txt");) {
			BufferedWriter output = new BufferedWriter(fw);
			this.printSolution(output);
			output.flush();
			output.close();
		} catch (IOException e) {
			// throw e;
			System.err.println("Failed storing solution with score " + score + " safely.");
		}
	}
}
