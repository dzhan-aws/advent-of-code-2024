import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;


public class RedNosedReports {
	private static LinkedList<Integer> stringToIntegerList(String s) {
		String[] numbersAsString = s.split(" ");
		LinkedList<Integer> theList = new LinkedList<>();
		for (int i = 0; i < numbersAsString.length; i++) {
			theList.add(Integer.parseInt(numbersAsString[i]));
		}
		return theList;
	}

	private static LinkedList<Integer> deepClone(LinkedList<Integer> report, int exceptIndex) {
		final LinkedList<Integer> clonedReport = new LinkedList<>();
		final int length = report.size();
		for (int i = 0; i < length; i++) {
			if (i == exceptIndex) {
				continue;
			}
			clonedReport.add(report.get(i));
		}
		return clonedReport;
	}

	private static boolean isReportSafe(LinkedList<Integer> report, boolean hasRemovedItem, Comparator<Integer> comparator) {
		final int length = report.size();
		for (int i = 0; i < length - 1; i++) {
			final Integer item1 = report.get(i);
			final Integer item2 = report.get(i + 1);
			if (comparator.compare(item1, item2) >= 0 || Math.abs(item1 - item2) > 3) {
				if (hasRemovedItem) {
					return false;
				}
				return isReportSafe(deepClone(report, i), true, comparator) || isReportSafe(deepClone(report, i + 1), true, comparator);
			}
		}
		return true;
	}

	private static boolean isReportSafe(LinkedList<Integer> report) {
		return isReportSafe(report, false, (a, b) -> a - b) || isReportSafe(report, false, (a, b) -> b - a);
	}

	public static void main(String[] args) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(args[0]));
		try {
			String line = br.readLine();
			int safeReports = 0;
			while (line != null) {
				final LinkedList<Integer> report = stringToIntegerList(line);
				if (isReportSafe(report)) {
					safeReports++;
				}
				line = br.readLine();
			}
			System.out.println(String.format("Safe reports: %d", safeReports));
		} finally {
			br.close();
		}
	}
}