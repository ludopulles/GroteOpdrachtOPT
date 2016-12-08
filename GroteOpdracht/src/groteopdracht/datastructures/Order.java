package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import groteopdracht.Main;

public class Order {

	public static final int DROP_TIME = Main.MINUTE_CONVERSION * 30;
	public static final int ORDERS_IDS = 1178;
	public static final Order[] orders;

	static {
		Order[] t_orders = new Order[ORDERS_IDS];

		try (BufferedReader orderReader = new BufferedReader(new FileReader("../Orderbestand.txt"))) {
			orderReader.readLine();
			String line;
			t_orders[0] = new Order();
			for (int i = 1; (line = orderReader.readLine()) != null; i++) {
				String[] parts = line.split(";");
				t_orders[i] = new Order(Integer.parseInt(parts[0]), parts[2], Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), (int) (Main.MINUTE_CONVERSION * Double.parseDouble(parts[5])),
						Integer.parseInt(parts[6]));
			}
		} catch (FileNotFoundException e) {
			// zou niet moeten gebeuren
			e.printStackTrace();
		} catch (IOException e) {
			// zou niet moeten gebeuren
			e.printStackTrace();
		}
		orders = t_orders;
	}

	public final int orderID, frequentie, numContainers, volume, leegTijd, matrixID;

	/*
	 * Leeg order, oftewel dumpen bij de dumpplaats. Dit kost 30 minuten
	 */
	public Order() {
		this.orderID = 0;
		this.frequentie = 0;
		this.numContainers = 0;
		this.volume = 0;
		this.leegTijd = DROP_TIME;
		this.matrixID = 287;
	}

	public Order(int orderID, String frequentie, int numContainers, int volume, int leegTijd, int matrixID) {
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

	public static void main(String[] args) {
		HashMap<Integer, Integer> m = new HashMap<>();
		HashMap<Integer, Integer> c = new HashMap<>();

		for (Order o : orders) {
			if (!m.containsKey(o.frequentie))
				m.put(o.frequentie, 0);
			if (!c.containsKey(o.frequentie))
				c.put(o.frequentie, 0);

			m.put(o.frequentie, m.get(o.frequentie) + o.leegTijd * o.frequentie);
			c.put(o.frequentie, c.get(o.frequentie) + 1);
		}

		for (Integer i : m.keySet()) {
			System.out.println(
					"Frequentie " + i + ", geeft penalty 3 * " + ((double) m.get(i)) / Main.MINUTE_CONVERSION * 60);
			System.out.println(
					"Frequentie " + i + ", geeft count " + c.get(i) + ", avg = " + ((double) m.get(i)) / c.get(i) / 10);
		}
	}

	public int capacity() {
		return this.volume * this.numContainers;
	}
}
