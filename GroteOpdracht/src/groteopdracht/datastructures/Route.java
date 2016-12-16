
package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import groteopdracht.Constants;

public class Route {

	public static final Random rand = new Random();
	public final ArrayList<Integer> route;
	public int capLeft, time;

	public Route() {
		this.route = new ArrayList<>();
		this.capLeft = Constants.MAX_CAPACITY;
		this.time = Constants.DROP_TIME;
	}

	public Route(Route copy) {
		this.route = new ArrayList<>();
		for (int i : copy.route) this.route.add(i);
		// this.route = (ArrayList<Integer>) copy.route.clone();
		this.capLeft = copy.capLeft;
		this.time = copy.time;
	}

	public int length() {
		return this.route.size();
	}

	public int get(int index) {
		return this.route.get(index);
	}

	public boolean canAdd(int order) {
		return this.capLeft >= Order.orders[order].capacity;
	}

	public void append(int order) {
		this.add(this.route.size(), order);
	}

	public void add(int index, int order) {
		// Assert: canAdd(order)
		ListIterator<Integer> li = route.listIterator(index);
		int prev = li.hasPrevious() ? li.previous() : 0;
		int next = index == route.size() ? 0 : route.get(index);
		this.capLeft -= Order.orders[order].capacity;
		this.time += Order.orders[order].timeIncrease(prev, next);
		this.route.add(index, order);
	}

	private int dist(int o1, int o2) {
		return Afstanden.tijd[Order.orders[o1].matrixID][Order.orders[o2].matrixID];
	}

	public void twoOpt() {
		// if i < j, twoOpt(i, j) is defined as:
		// (1) -> r[i] -> r[i+1] -> ... -> r[j-1] -> r[j] -> (2)
		// changing to
		// (1) -> r[j] -> r[j-1] -> ... -> r[i+1] -> r[i] -> (2)
		boolean improved = true;
		while (improved) {
			improved = false;
			outer: for (int i = 0; i < this.length(); i++) {
				for (int j = i; ++j < this.length();) {
					int li = i == 0 ? 0 : this.route.get(i - 1);
					int rj = j == this.length() - 1 ? 0 : this.route.get(j + 1);

					int curtime = dist(li, route.get(i)) + dist(route.get(j), rj);
					int nxttime = dist(li, route.get(j)) + dist(route.get(i), rj);
					for (int idx = i; idx < j; idx++) {
						curtime += dist(route.get(idx), route.get(idx + 1));
						nxttime += dist(route.get(idx + 1), route.get(idx));
					}
					if (nxttime < curtime) {
						System.err.println("2OPT:   " + i + "\t, " + j + "\t: " + curtime + " \t- " + (curtime - nxttime)
								+ " \t= " + nxttime);

						/*
						 * We know that this loop will execute at least on time,
						 * because i < j, so place the condition at the end.
						 * Code: reverse(i, i+1, ..., j-1, j)
						 */
						do {
							int t = this.route.get(i);
							this.route.set(i, this.route.get(j));
							this.route.set(j, t);
						} while (++i < --j);

						this.time += nxttime - curtime; // < 0
						improved = true;
						break outer;
					}
				}
			}

		}
	}

	/**
	 * Performs 2.5 opt on a route
	 * 
	 * 2.5 opt takes an index i, and an index j so that (j - i) > 1 (ie; they
	 * are no subsequent)
	 * 
	 * it then connects i to i+2, j to i+1, and i+1 to j+1, essentialy replacing
	 * the vertices ii+1, i+1i+2, jj+1 with ii+2, ji+1, i+1j+i
	 */
	public void twoHalfOpt() {
		// if i != j, i != j+1
		// (1) -> r[i] -> (2) ; (3) -> r[j] -> r[j+1] -> (4)
		// changing to
		// (1) -> (2) ; (3) -> r[j] -> r[i] -> r[j+1] -> (4)
		boolean improved = true;
		while (improved) {
			improved = false;
			outer: for (int i = 0; i < this.length(); i++) {
				for (int j = 0; j < this.length(); j++) {
					if (i == j || i == j + 1) continue;
					int li = (i == 0) ? 0 : this.route.get(i - 1);
					int ri = (i == this.length() - 1) ? 0 : this.route.get(i + 1);
					int rj = (j == this.length() - 1) ? 0 : this.route.get(j + 1);

					int curtime = dist(li, route.get(i)) + dist(route.get(i), ri) + dist(route.get(j), rj);
					int nxttime = dist(li, ri) + dist(route.get(j), route.get(i)) + dist(route.get(i), rj);

					if (nxttime < curtime) {
						improved = true;
						System.err.println("2.5OPT: " + i + "\t, " + j + "\t: " + curtime + " \t- " + (curtime - nxttime)
								+ " \t= " + nxttime);

						if (i < j) {
							// cyclicLeftRotate(i, ..., j)
							int t = route.get(i);
							do {
								route.set(i, route.get(i + 1));
							} while (++i < j);
							route.set(j, t);
						} else {
							// cyclicRightRotate(j+1, ..., i)
							int t = route.get(i);
							do {
								route.set(i, route.get(i - 1));
							} while (--i > j);
							route.set(j + 1, t);
						}
						this.time += nxttime - curtime; // < 0.

						break outer; // restart the 2.5opt.
					}
				}
			}
		}
	}
}
