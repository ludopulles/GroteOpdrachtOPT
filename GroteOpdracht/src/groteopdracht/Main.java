package groteopdracht;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static final int MATRIX_IDS = 1099;
	public static final int ORDER_IDS = 1177;
	public static final int MINUTE_CONVERSION = 600;

	public static int[][] time = new int[MATRIX_IDS][MATRIX_IDS];
	public static Order[] orders = new Order[ORDER_IDS + 1];
	
	public static void main(String[] args) throws IOException {
		BufferedReader distanceReader = new BufferedReader(new FileReader("../AfstandenMatrix.txt"));
		distanceReader.readLine();
		for (int i = 0; i < MATRIX_IDS; i++) {
			for (int j = 0; j < MATRIX_IDS; j++) {
				String[] parts = distanceReader.readLine().split(";");
				time[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])] = MINUTE_CONVERSION / 60 * Integer.parseInt(parts[3]);
			}
		}
		distanceReader.close();

		BufferedReader orderReader = new BufferedReader(new FileReader("../Orderbestand.txt"));
		orderReader.readLine();
		String line;

		orders[0] = new Order();
		for (int i = 1; (line = orderReader.readLine()) != null; i++) {
			String[] parts = line.split(";");
			orders[i] = new Order(Integer.parseInt(parts[0]), parts[2], Integer.parseInt(parts[3]),
					Integer.parseInt(parts[4]), (int) (MINUTE_CONVERSION * Double.parseDouble(parts[5])), Integer.parseInt(parts[6]));
		}
		orderReader.close();
		
		DagSchema ds = new DagSchema();
		ds.v1.add(1);
		ds.v1.add(2);
		ds.v1.add(3);
		ds.v1.add(0);
		ds.v2.add(3);
		ds.v2.add(2);
		ds.v2.add(1);
		ds.v2.add(0);
		
		System.out.println(ds.isValid());
	}

}
