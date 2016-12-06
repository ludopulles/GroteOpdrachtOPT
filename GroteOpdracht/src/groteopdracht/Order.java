package groteopdracht;

public class Order {

	public int orderID, frequentie, numContainers, volume, leegTijd, matrixID;

	public Order() {
		this.leegTijd = Main.MINUTE_CONVERSION * 30;
	}
	
	public Order(int orderID, String frequentie, int numContainers, int volume, int leegTijd, int matrixID) {
		this.orderID = orderID;
		this.frequentie = frequentie.charAt(0) - '0';
		this.numContainers = numContainers;
		this.volume = volume;
		this.leegTijd = leegTijd;
		this.matrixID = matrixID;
	}
}
