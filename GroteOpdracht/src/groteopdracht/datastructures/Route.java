
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
		for (int i : copy.route)
			this.route.add(i);

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
	
	public boolean canSet(int idx, int order) {
		int currentcap = Order.orders[this.route.get(idx)].capacity;
		int newcap = Order.orders[order].capacity;
		int delta = newcap - currentcap;
		return delta <= this.capLeft;
	}
	
	public void set(int index, int order) {
		int prev = (index == 0) ? 0 : route.get(index - 1);
		int next = (index == this.route.size() - 1) ? 0 : route.get(index + 1);
		
		int old_time = dist(prev, route.get(index)) + dist(route.get(index), next);
		int new_time = dist(prev, route.get(index)) + dist(route.get(index), next);
		this.time += new_time;
		this.time -= old_time;
		
		this.capLeft += Order.orders[route.get(index)].capacity;
		this.capLeft -= Order.orders[order].capacity;
		
		this.route.set(index, order);
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
		int N = this.length();
		boolean improved = true;
		while (improved) {
			improved = false;
			outer: for (int i = 0; i < N; i++) {
				for (int j = i; ++j < N;) {
					if (twoOpt(i, j)) {
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
		int N = this.length();
		boolean improved = true;
		while (improved) {
			improved = false;
			outer: for (int i = 0; i < N; i++) {
				for (int j = 0; j < i; j++) {
					if (twoHalfOpt(i, j)) {
						improved = true;
						break outer;
					}
				}
				for (int j = i + 2; j < N; j++) {
					if (twoHalfOpt(i, j)) {
						improved = true;
						break outer;
					}
				}
			}
		}
	}

	public void opts() {
		// combine twoOpt and twoHalfOpt
		int N = this.length();
		boolean improved = true;
		
//		int k = 0;
		while (improved) {
//			System.out.println("IMPROVED: " + this.time);
//			if (++k > 25) System.exit(1);
			improved = false;
			outer: for (int i = 0; i < N; i++) {
				for (int j = 0; j + 1 < i; j++) {
					if (twoHalfOpt(i, j)) {
//						System.out.println("BETTER 2.5op: " + i + ", " + j);
						improved = true;
						break outer;
					}
				}
				for (int j = i + 1; j < N; j++) {
					if (twoHalfOpt(i, j)) {
//						System.out.println("BETTER 2.5o: " + i + ", " + j);
						improved = true;
						break outer;
					}
				}
				for (int j = i; ++j < N; j++) {
					if (twoOpt(i, j)) {
//						System.out.println("BETTER 2o: " + i + ", " + j);
						improved = true;
						break outer;
					}
				}
			}
		}
//		System.out.println("STOP");
	}

	private boolean twoOpt(int i, int j) {
		// assert(i < j);

		// (1) -> r[i] -> r[i+1] -> ... -> r[j-1] -> r[j] -> (2)
		// changing to
		// (1) -> r[j] -> r[j-1] -> ... -> r[i+1] -> r[i] -> (2)
		int li = i == 0 ? 0 : this.route.get(i - 1);
		int rj = j == this.length() - 1 ? 0 : this.route.get(j + 1);

		int curtime = dist(li, route.get(i)) + dist(route.get(j), rj);
		int nxttime = dist(li, route.get(j)) + dist(route.get(i), rj);
		for (int idx = i; idx < j; idx++) {
			curtime += dist(route.get(idx), route.get(idx + 1));
			nxttime += dist(route.get(idx + 1), route.get(idx));
		}
		if (nxttime >= curtime)
			return false;

		/*
		 * We know that this loop will execute at least on time, because i < j,
		 * so place the condition at the end. Code: reverse(i, i+1, ..., j-1, j)
		 */
		do {
			int t = this.route.get(i);
			this.route.set(i, this.route.get(j));
			this.route.set(j, t);
		} while (++i < --j);

		this.time += nxttime - curtime; // < 0
		return true;
	}

	private boolean twoHalfOpt(int i, int j) {
		// assert(i != j && i != j + 1);

		// (1) -> r[i] -> (2) ; (3) -> r[j] -> r[j+1] -> (4)
		// changing to
		// (1) -> (2) ; (3) -> r[j] -> r[i] -> r[j+1] -> (4)
		int li = (i == 0) ? 0 : this.route.get(i - 1);
		int ri = (i == this.length() - 1) ? 0 : this.route.get(i + 1);
		int rj = (j == this.length() - 1) ? 0 : this.route.get(j + 1);

		int curtime = dist(li, route.get(i)) + dist(route.get(i), ri) + dist(route.get(j), rj);
		int nxttime = dist(li, ri) + dist(route.get(j), route.get(i)) + dist(route.get(i), rj);

		if (nxttime >= curtime)
			return false;

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
		return true;
	}
}
