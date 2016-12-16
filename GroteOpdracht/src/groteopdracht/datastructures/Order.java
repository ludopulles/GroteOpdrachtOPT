
package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import groteopdracht.Constants;

public class Order {

	public static Order[] orders;
	public static ArrayList<ArrayList<Integer>> atLocation;
	// order ID -> index in orders
	public static HashMap<Integer, Integer> invOrderIDs = new HashMap<>();

	static {
		Order[] t_orders = new Order[Constants.ORDER_IDS];
		invOrderIDs.put(0, 0);
		atLocation = new ArrayList<>(Constants.MATRIX_IDS);
		for (int i = 0; i < Constants.MATRIX_IDS; i++) {
			atLocation.add(new ArrayList<>());
		}
		HashMap<Integer, Integer> frequency = new HashMap<>(), penaltySum = new HashMap<>();
		String file = Constants.RESOURCES_DIR + "/Orderbestand.txt";
		try (BufferedReader orderReader = new BufferedReader(new FileReader(file))) {
			orderReader.readLine();
			String line;
			t_orders[0] = new Order();
			for (int i = 1; (line = orderReader.readLine()) != null; i++) {
				String[] parts = line.split(";");
				for (int p = 0; p < parts.length; p++) parts[p] = parts[p].trim();
				int leegTijd = (int) Math.round(Double.parseDouble(parts[5]) * Constants.MINUTE_CONVERSION);
				int orderID = Integer.parseInt(parts[0]);
				int numContainers = Integer.parseInt(parts[3]);
				int volume = Integer.parseInt(parts[4]);
				int matrixID = Integer.parseInt(parts[6]);
				t_orders[i] = new Order(orderID, parts[2], numContainers, volume, leegTijd, matrixID);
				atLocation.get(matrixID).add(i);

				// invOrder
				invOrderIDs.put(orderID, i);
				
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
			maxAtLoc = Math.max(maxAtLoc, atLocation.get(i).size());
		}

		// Statistics:
		System.out.println("Max " + maxAtLoc + " at one location");
		for (int f : frequency.keySet()) {
			System.out.println(
					"Frequency " + f + ": " + frequency.get(f) + ", penalty: " + penaltySum.get(f) * 1.0 / 600);
		}
	}
	public final int orderID, frequency, numContainers, volume, emptyTime;
	public final int matrixID, penalty, capacity;

	public Order() {
		this(0, "0", 0, 0, Constants.DROP_TIME, Constants.DUMP_LOCATION);
	}

	public Order(int orderID, String frequency, int numContainers, int volume, int emptyTime, int matrixID) {
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
	 * @param prev the order no. from which we go to this order
	 * @param next the order no. where we are heading after this order
	 * @return the increase in total time when we empty the bins at this order
	 */
	public int timeIncrease(int prev, int next) {
		int l = orders[prev].matrixID, m = this.matrixID, r = orders[next].matrixID;
		return Afstanden.tijd[l][m] + Afstanden.tijd[m][r] - Afstanden.tijd[l][r] + this.emptyTime;
	}
}
