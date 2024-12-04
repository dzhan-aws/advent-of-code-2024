import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CeresSearch {
	private static List<String> readMatrix(String filename) throws IOException {
		final List<String> matrix = new ArrayList<>();
		final BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			while (line != null) {
				matrix.add(line);
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return matrix;
	}

	private static boolean isValidLocation(List<String> matrix, int locationX, int locationY) {
		final int rowSize = matrix.size();
		final int columnSize = matrix.get(0).length();
		return locationX >= 0 && locationX < rowSize && locationY >= 0 && locationY < columnSize;
	}

	private static String constructWord(List<String> matrix, int locationX, int locationY, int directionX, int directionY) {
		final StringBuilder sb = new StringBuilder();
		int x = locationX;
		int y = locationY;
		int lettersCount = 0;
		while (isValidLocation(matrix, x, y) && lettersCount < 4) {
			sb.append(matrix.get(x).charAt(y));
			x += directionX;
			y += directionY;
			lettersCount++;
		}
		return sb.toString();
	}

	private static boolean findXmas(List<String> matrix, int locationX, int locationY, int directionX, int directionY) {
		if (!isValidLocation(matrix, locationX, locationY)) {
			return false;
		}
		if (matrix.get(locationX).charAt(locationY) != 'X') {
			return false;
		}
		final String word = constructWord(matrix, locationX, locationY, directionX, directionY);
		return "XMAS".equals(word);
	}

	private static boolean isMAS(String word) {
		return "MAS".equals(word) || "SAM".equals(word);
	}

	private static boolean findX(List<String> matrix, int locationX, int locationY) {
		if (!isValidLocation(matrix, locationX, locationY)) {
			return false;
		}
		if (matrix.get(locationX).charAt(locationY) != 'A') {
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(matrix.get(locationX - 1).charAt(locationY - 1));
		sb.append('A');
		sb.append(matrix.get(locationX + 1).charAt(locationY + 1));
		final String firstWord = sb.toString();
		sb = new StringBuilder();
		sb.append(matrix.get(locationX + 1).charAt(locationY - 1));
		sb.append('A');
		sb.append(matrix.get(locationX - 1).charAt(locationY + 1));
		final String secondWord = sb.toString();
		return isMAS(firstWord) && isMAS(secondWord);
	}

	public static void main(String[] args) throws IOException {
		final List<String> matrix = readMatrix(args[0]);
		final int rowSize = matrix.size();
		final int columnSize = matrix.get(0).length();
		long count = 0;
		for (int lx = 0; lx < rowSize; lx++) {
			for (int ly = 0; ly < columnSize; ly++) {
				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) {
							continue;
						}
						if (findXmas(matrix, lx, ly, dx, dy)) {
							count++;
						}
					}
				}
			}
		}
		System.out.println(String.format("XMAS count: %d", count));

		count = 0;
		for (int lx = 1; lx < rowSize - 1; lx++) {
			for (int ly = 1; ly < columnSize - 1; ly++) {
				if (findX(matrix, lx, ly)) {
					count++;
				}
			}
		}
		System.out.println(String.format("X count: %d", count));
	}
}