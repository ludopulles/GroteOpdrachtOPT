package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import groteopdracht.Main;

public class Order {

	public static final int ORDERS_IDS = 1178;
	
	public final static Order[] orders;
	
	static {
		Order[] t_orders = new Order[ORDERS_IDS];
		
		try(BufferedReader orderReader = new BufferedReader(new FileReader("../Orderbestand.txt"))) {
			orderReader.readLine();
			String line;
			t_orders[0] = new Order();
			for (int i = 1; (line = orderReader.readLine()) != null; i++) {
				String[] parts = line.split(";");
				t_orders[i] = new Order(Integer.parseInt(parts[0]), parts[2], Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), (int) (Main.MINUTE_CONVERSION * Double.parseDouble(parts[5])), Integer.parseInt(parts[6]));
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
		this.leegTijd = Main.MINUTE_CONVERSION * 30;
		this.matrixID = 0;
	}
	
	public Order(int orderID, String frequentie, int numContainers, int volume, int leegTijd, int matrixID) {
		this.orderID = orderID;
		this.frequentie = frequentie.charAt(0) - '0';
		this.numContainers = numContainers;
		this.volume = volume;
		this.leegTijd = leegTijd;
		this.matrixID = matrixID;
	}
	
	public static void main(String[] args) {
		int count = 0;
		int total_penalty = 0;
		for(Order o : orders) {
			if(o.frequentie != 1) {
				total_penalty += 3*o.leegTijd;
			}
		}
		System.out.println(total_penalty / 10);
		System.out.println(count);
		System.out.println(orders[1176].leegTijd);
	}
}
