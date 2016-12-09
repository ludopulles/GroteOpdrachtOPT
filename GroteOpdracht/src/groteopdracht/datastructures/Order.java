
package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileReader;
import groteopdracht.Constants;

public class Order {

	public static final Order[] orders;
	static {
		Order[] t_orders = new Order[Constants.ORDERS_IDS];
		try (BufferedReader orderReader = new BufferedReader(
				new FileReader("../Orderbestand.txt"))) {
			orderReader.readLine();
			String line;
			t_orders[0] = new Order();
			for (int i = 1; (line = orderReader.readLine()) != null; i++) {
				String[] parts = line.split(";");
				int orderID = Integer.parseInt(parts[0]);
				int numContainers = Integer.parseInt(parts[3]);
				int volume = Integer.parseInt(parts[4]);
				int leegTijd = (int) (Double.parseDouble(parts[5])
						* Constants.MINUTE_CONVERSION);
				int matrixID = Integer.parseInt(parts[6]);
				t_orders[i] = new Order(orderID, parts[2], numContainers,
						volume, leegTijd, matrixID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		orders = t_orders;
	}
	public final int orderID, frequentie, numContainers, volume, leegTijd,
			matrixID;

	/*
	 * Leeg order, oftewel dumpen bij de dumpplaats. Dit kost 30 minuten
	 */
	public Order() {
		this(0, "0", 0, 0, Constants.DROP_TIME, Constants.DUMP_LOCATION);
	}

	public Order(int orderID, String frequentie, int numContainers, int volume,
			int leegTijd, int matrixID) {
		this.orderID = orderID;
		this.frequentie = frequentie.charAt(0) - '0';
		this.numContainers = numContainers;
		this.volume = volume;
		this.leegTijd = leegTijd;
		this.matrixID = matrixID;
	}

	public int penalty() {
		return this.leegTijd * this.frequentie;
	}

	public int capacity() {
		return this.volume * this.numContainers;
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
				- Afstanden.tijd[l][r] + this.leegTijd;
	}
}
