package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileReader;
import groteopdracht.Constants;

/*
 * letterlijk alleen om een soort namespace te hebben
 */
public final class Afstanden {

	public static final int[][] tijd;

	static {
		int[][] t_time = new int[Constants.MATRIX_IDS][Constants.MATRIX_IDS];
		try (BufferedReader distanceReader = new BufferedReader(new FileReader("../AfstandenMatrix.txt"))) {
			distanceReader.readLine();
			for (int i = 0; i < Constants.MATRIX_IDS; i++) {
				for (int j = 0; j < Constants.MATRIX_IDS; j++) {
					String[] parts = distanceReader.readLine().split(";");
					t_time[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])] = Constants.MINUTE_CONVERSION / 60
							* Integer.parseInt(parts[3]);
				}
			}
		} catch (Exception e) {
			// zou niet moeten gebeuren
			e.printStackTrace();
		}
//		for (int i = 0; i < MATRIX_IDS; i++) {
//			for (int j = 0; j < MATRIX_IDS; j++) {
//				for (int k = 0; k < MATRIX_IDS; k++) {
//					if (t_time[i][k] > t_time[i][j] + t_time[j][k]) {
//						System.out.println("Optimize matrix");
//					}
//				}
//			}
//		}
		tijd = t_time;
	}

	private Afstanden() {
	}
}
