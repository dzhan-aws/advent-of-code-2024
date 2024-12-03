import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistorianHysteria {
	private static class Locations {
		private List<Integer> firstList;
		private List<Integer> secondList;
		private long distance;
		private long similarity;

		private Locations() {
			this.firstList = new ArrayList<>();
			this.secondList = new ArrayList<>();
			this.distance = 0;
			this.similarity = 0;
		}

		private void calculate() {
			Collections.sort(this.firstList);
			Collections.sort(this.secondList);
			this.distance = 0;
			this.similarity = 0;
			
			final Map<Integer, Integer> similarityMap = new HashMap<>();
			final int length = firstList.size();

			for (int i = 0; i < length; i++) {
				final int firstValue = this.firstList.get(i);
				final int secondValue = this.secondList.get(i);
				this.distance += Math.abs(firstValue - secondValue);
				similarityMap.put(secondValue, similarityMap.getOrDefault(secondValue, 0) + 1);
			}

			for (int firstValue : this.firstList) {
				this.similarity += firstValue * similarityMap.getOrDefault(firstValue, 0);
			}
		}
	}

	private static Locations readLocations(String filename) throws IOException {
		final Locations locations = new Locations();
		final BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			while (line != null) {
				String[] numbersAsString = line.split("   ");
				locations.firstList.add(Integer.parseInt(numbersAsString[0]));
				locations.secondList.add(Integer.parseInt(numbersAsString[1]));
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return locations;
	}

	public static void main(String[] args) throws IOException {
		final Locations locations = readLocations(args[0]);
		locations.calculate();
		System.out.println(String.format("Distance: %d", locations.distance));
		System.out.println(String.format("Similarity: %d", locations.similarity));
	}
}