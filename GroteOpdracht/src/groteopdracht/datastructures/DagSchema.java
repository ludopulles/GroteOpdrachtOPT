package groteopdracht.datastructures;

import java.util.LinkedList;

import groteopdracht.Main;

/*
 * Aannames: we splitsen een order niet, dus als we het ophalen, halen we gelijk alles op.
 * verder zorgen we ervoor dat we geen dingen dubbel bezoeken, want twee keer legen heeft geen nut (behalve order 0)
 */
public class DagSchema {

	public final static int MAX_TIME = Main.MINUTE_CONVERSION * 12 * 60;
	public final static int MAX_CAPACITY = 20000 * 5;
	
	
	private LinkedList<Integer> v1, v2;
	
	//bijhouden welke orders we hebben, 0 staat hier ook in, voor makkelijk indexeren, maar is niet relevant.
	private boolean[] orders = new boolean[Order.ORDERS_IDS];
	
	public DagSchema() {
		v1 = new LinkedList<Integer>();
		v2 = new LinkedList<Integer>();
	}
	
	//misschien handig later
	public DagSchema(LinkedList<Integer> v1, LinkedList<Integer> v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public void add(int vehicle, int order, int index) {
		if (order != 0 && orders[order]) {
			throw new Error("Already visited today!");
		}
		LinkedList<Integer> v = vehicle == 1 ? v1 : v2;
		v.add(index, order);
		orders[order] = true;
	}
	
	//voeg een order toe aan het einde van de route van vehicle
	public void add(int vehicle, int order) {
		if (order != 0 && orders[order]) {
			throw new Error("Already visited today!");
		}
		LinkedList<Integer> v = vehicle == 1 ? v1 : v2;
		v.add(order);
		orders[order] = true;
	}
	
	public void remove(int vehicle, int order) {
		if (order == 0) {
			throw new Error("How to implement this properly? can be visited multiple times");
		}
		LinkedList<Integer> v = vehicle == 1 ? v1 : v2;
		v.remove(order);
		orders[order] = false;
	}
	
	public void removeAtIndex(int vehicle, int index) {
		LinkedList<Integer> v = vehicle == 1 ? v1 : v2;
		if(index >= v.size()) {
			throw new Error("index kn");
		}
		int i = v.remove(index);
		orders[i] = false; // als 0, dan kan het nog steeds visited zijn, maar op 0 wordt niet gecheckt, dus boeit niet
	}
	
	public boolean[] getOrders() {
		return orders;
	}
	
	public LinkedList<Integer> getv1() {
		return v1;
	}
	
	public LinkedList<Integer> getv2() {
		return v2;
	}
	/*
	 * we berekenen nu alles steeds opnieuw, kan beter geloof ik
	 */
	public boolean isValid() { 
		// Je moet sowieso aan het eind van de route terug zijn be het begin.
		if (v1.getLast() != 0 || v2.getLast() != 0) {
			throw new Error("Dumb ass.");
		}
		
		for (int k = 0; k < 2; k++) {
			LinkedList<Integer> ref = k == 0 ? v1 : v2;
			int totalTime = MAX_TIME, capacity = MAX_CAPACITY;
			Integer i = 0;
			for (Integer j : ref) {
				totalTime -= Afstanden.tijd[Order.orders[i].matrixID][Order.orders[j].matrixID];
				totalTime -= Order.orders[j].leegTijd;
				capacity -= Order.orders[j].volume * Order.orders[j].numContainers;
				if (totalTime < 0 || capacity < 0) { //tijd overschrijden of capaciteit
					return false;
				}
				if(j == 0) { //we hebben afval gedumpt, dus capaciteit weer max
					capacity = MAX_CAPACITY;
				}
				i = j;
			}
		}
		return true;
	}
	
}