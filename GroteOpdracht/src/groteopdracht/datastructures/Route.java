
package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import groteopdracht.Constants;

public class Route {

	public static final Random rand = new Random();
	public final List<Integer> route;
	public int capLeft, time;

	public Route() {
		this.route = new ArrayList<>();
		this.capLeft = Constants.MAX_CAPACITY;
		this.time = Constants.DROP_TIME;
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
		// TODO: magics
//		int ntimes = 100;
//		while (ntimes-- > 0) {
		boolean improved = true;
		while (improved) {
			improved = false;
			outer:
			for (int i = 0; i < this.length(); i++) {
				for (int j = i; ++j < this.length();) {
					// int i = rand.nextInt(this.length());
					// int j = rand.nextInt(this.length());
					// if (j == i) continue;
					// if (i > j) {
					// int t = i;
					// i = j;
					// j = t;
					// }
					// i < j
					int li = i == 0 ? 0 : this.route.get(i - 1);
					int rj = j == this.length() - 1 ? 0 : this.route.get(j + 1);
					// change li -> r[i] -> r[i + 1] -> ... -> r[j - 1] -> r[j]
					// -> rj
					// to li -> r[j] -> r[j - 1] -> ... -> r[i + 1] -> r[i] ->
					// rj
					int curtime = dist(li, route.get(i)) + dist(route.get(j), rj);
					int nxttime = dist(li, route.get(j)) + dist(route.get(i), rj);
					for (int idx = i; idx < j; idx++) {
						curtime += dist(route.get(idx), route.get(idx + 1));
						nxttime += dist(route.get(idx + 1), route.get(idx));
					}
					if (nxttime < curtime) {
						improved = true;
						System.err.println("2OPT: " +
								i + ", " + j + ": " + curtime + " VS " + nxttime
										+ " impr: " + (nxttime - curtime));
						// beter!
						while (i < j) {
							int t = this.route.get(i);
							this.route.set(i, this.route.get(j));
							this.route.set(j, t);
							i++;
							j--;
						}
						this.time += nxttime - curtime; // < 0
						
						break outer;
					}
				}
			}
			
		}
	}

	/**
	 * Performs 2.5 opt on a route
	 * 
	 * 2.5 opt takes an index i, and an index j so that (j - i) > 1 (ie; they are no subsequent)
	 * 
	 * it then connects i to i+2, j to i+1, and i+1 to j+1, essentialy replacing the vertices ii+1, i+1i+2, jj+1 with ii+2, ji+1, i+1j+i
	 */
	public void twoHalfOpt() {
		boolean improved = true;
		while (improved) {
			improved = false;
			outer:
			for (int i = 0; i < this.length(); i++) {
				for (int j = i; ++j < this.length();) {
					//System.out.println("Considering: " + i + " " + j);
					// 2.5-opt:
					int li = (i == 0) ? 0 : this.route.get(i-1);
					int rj = (j == this.length() - 1) ? 0 : this.route.get(j + 1);
					
					int curtime = dist(li, route.get(i)) + dist(route.get(i), route.get(i+1)) + dist(route.get(j), rj);
					int nxttime = dist(li, route.get(i + 1)) + dist(route.get(j), route.get(i)) + dist(route.get(i), rj);
					
					if (nxttime < curtime) {
						improved = true;
						System.err.println("2.5OPT: " + i + ", " + j + ": " + curtime + " VS " + nxttime + " impr: " + (nxttime - curtime));
						
						//rebuild the route;
						//everything until j shifts left by one, then i takes that place.
						int routei = route.get(i);
						for (int k = i; k < j; k++) {
							int t = route.get(k + 1);
							route.set(k, t);
						}
						route.set(j, routei);
						this.time += nxttime - curtime; // < 0.
						
						break outer; //restart the 2.5opt.
					}
				}
			}
		}
	}
}
