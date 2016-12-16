package checker.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public final class ProblemReader {
	public static final ProblemReader reader = new ProblemReader();

	public int[][] readDistances(BufferedReader br) throws IOException {
		ArrayList<DistanceData> data = new ArrayList<>();

		int maxIndex = -1;

		br.readLine();

		String line = br.readLine();
		int to;
		while (line != null) {
			String[] lineData = line.split(";");

			int from = Integer.parseInt(lineData[0].trim());
			to = Integer.parseInt(lineData[1].trim());
			int dist = Integer.parseInt(lineData[3].trim());

			data.add(new DistanceData(from, to, dist));

			maxIndex = Math.max(maxIndex, Math.max(from, to));

			line = br.readLine();
		}

		maxIndex++;
		int[][] matrix = new int[maxIndex][maxIndex];

		for (DistanceData d : data) {
			matrix[d.from][d.to] = d.dist;
		}

		return matrix;
	}

	public TreeMap<Integer, Order> readOrders(BufferedReader br) throws IOException {
		TreeMap<Integer, Order> orders = new TreeMap<>();

		br.readLine();

		String line = br.readLine();

		orders.put(Integer.valueOf(0), new Order(0, "Maarheeze", 0, 0, 0, 0.0D, 287, 56071576L, 513090749L));

		while (line != null) {
			String[] lineData = line.split(";");

			int orderId = Integer.parseInt(lineData[0].trim());
			String plaats = lineData[1].trim();
			int freq = Integer.parseInt(lineData[2].trim().substring(0, 1));
			int aantCont = Integer.parseInt(lineData[3].trim());
			int volumeCont = Integer.parseInt(lineData[4].trim());
			double legingTijd = Double.parseDouble(lineData[5].trim()) * 60.0D;
			int matrixId = Integer.parseInt(lineData[6].trim());
			long xCoord = Long.parseLong(lineData[7].trim());
			long yCoord = Long.parseLong(lineData[8].trim());

			Order order = new Order(orderId, plaats, freq, aantCont, volumeCont, legingTijd, matrixId, xCoord, yCoord);

			orders.put(Integer.valueOf(orderId), order);

			line = br.readLine();
		}

		return orders;
	}

	public final class DistanceData {
		public final int from;
		public final int to;
		public final int dist;

		public DistanceData(int from, int to, int dist) {
			this.from = from;
			this.to = to;
			this.dist = dist;
		}
	}
}
