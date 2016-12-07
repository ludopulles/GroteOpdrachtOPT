package groteopdracht.datastructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import groteopdracht.Main;

/*
 * letterlijk alleen om een soort namespace te hebben
 */
public final class Afstanden {
	
	private static final int MATRIX_IDS = 1099;
	public static final int[][] tijd;
	static {
		int[][] t_time = new int[MATRIX_IDS][MATRIX_IDS];
		try(BufferedReader distanceReader = new BufferedReader(new FileReader("../AfstandenMatrix.txt"))) {
			distanceReader.readLine();
			for (int i = 0; i < MATRIX_IDS; i++) {
				for (int j = 0; j < MATRIX_IDS; j++) {
					String[] parts = distanceReader.readLine().split(";");
					t_time[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])] = Main.MINUTE_CONVERSION / 60 * Integer.parseInt(parts[3]);
				}
			}
		} catch (FileNotFoundException e) {
			// zou niet moeten gebeuren
			e.printStackTrace();
		} catch (IOException e) {
			// zou niet moeten gebeuren
			e.printStackTrace();
		}
		tijd = t_time;
	}
	
	private Afstanden() {
	}
}
