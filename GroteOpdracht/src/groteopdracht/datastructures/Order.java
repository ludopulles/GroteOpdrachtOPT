
package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import groteopdracht.Constants;

public class Order {

	public static final Order[] orders;
	/**
	 * atLocation[i] = { j | orders[j] = i }
	 */
	public static final ArrayList<ArrayList<Integer>> atLocation;
	
	static {
		Order[] t_orders = new Order[Constants.ORDERS_IDS];
		atLocation = new ArrayList<>(Constants.MATRIX_IDS);
		for (int i = 0; i < Constants.MATRIX_IDS; i++) {
			atLocation.add(new ArrayList<>());
		}
		HashMap<Integer, Integer> frequency = new HashMap<>(), penaltySum = new HashMap<>();
		try (BufferedReader orderReader = new BufferedReader(
				new FileReader("../Orderbestand.txt"))) {
			orderReader.readLine();
			String line;
			t_orders[0] = new Order();
			for (int i = 1; (line = orderReader.readLine()) != null; i++) {
				String[] parts = line.split(";");

				int leegTijd = (int) Math.round(Double.parseDouble(parts[5]) * Constants.MINUTE_CONVERSION);
//				if (Math.floor(leegTijdD) != leegTijdD) {
//					System.err.println("Strange time: " + parts[5] + " : " + leegTijdD + " VS " + Math.floor(leegTijdD));
//				}
				
				int orderID = Integer.parseInt(parts[0]);
				int numContainers = Integer.parseInt(parts[3]);
				int volume = Integer.parseInt(parts[4]);
//				int leegTijd = (int) (Double.parseDouble(parts[5])
//						* Constants.MINUTE_CONVERSION);
				int matrixID = Integer.parseInt(parts[6]);
				t_orders[i] = new Order(orderID, parts[2], numContainers,
						volume, leegTijd, matrixID);
				atLocation.get(matrixID).add(i);
				
				if (!frequency.containsKey(t_orders[i].frequency)) {
					frequency.put(t_orders[i].frequency, 0);
				}
				frequency.put(t_orders[i].frequency, 1 + frequency.get(t_orders[i].frequency));
				if (!penaltySum.containsKey(t_orders[i].frequency)) {
					penaltySum.put(t_orders[i].frequency, 0);
				}
				penaltySum.put(t_orders[i].frequency, t_orders[i].penalty + penaltySum.get(t_orders[i].frequency));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		orders = t_orders;
		int maxAtLoc = 0;
		for (int i = 0; i < Constants.MATRIX_IDS; i++) {
			atLocation.get(i).sort(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return Integer.compare(orders[o1].capacity, orders[o2].capacity);
				}
				
			});
			maxAtLoc = Math.max(maxAtLoc, atLocation.get(i).size());
		}
		
		for (int f : frequency.keySet()) {
			System.out.println("Frequency " + f + ": " + frequency.get(f) + ", penalty: " + penaltySum.get(f)*1.0/600);
		}
	}
	public final int orderID, frequency, numContainers, volume, emptyTime;
	public final int matrixID, penalty, capacity;

	/*
	 * Leeg order, oftewel dumpen bij de dumpplaats. Dit kost 30 minuten
	 */
	public Order() {
		this(0, "0", 0, 0, Constants.DROP_TIME, Constants.DUMP_LOCATION);
	}

	public Order(int orderID, String frequency, int numContainers, int volume,
			int emptyTime, int matrixID) {
		this.orderID = orderID;
		this.frequency = frequency.charAt(0) - '0';
		this.numContainers = numContainers;
		this.volume = volume;
		this.emptyTime = emptyTime;
		this.matrixID = matrixID;
		this.penalty = 3 * this.emptyTime * this.frequency;
		this.capacity = this.volume * this.numContainers;
	}

	/**
	 * Returns the increase in time when a route from prev to next, is changed
	 * to a route from prev via this to next.
	 * 
	 * @param prev
	 *            the order index after which the current order would be
	 *            inserted
	 * @param next
	 *            the order index before which the current order would be
	 *            inserted
	 * @return increase in time units
	 */
	public int timeIncrease(int prev, int next) {
		int l = orders[prev].matrixID, m = this.matrixID,
				r = orders[next].matrixID;
		return Afstanden.tijd[l][m] + Afstanden.tijd[m][r]
				- Afstanden.tijd[l][r] + this.emptyTime;
	}
}
