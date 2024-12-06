import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class PrintQueue {
	private static class Result {
		private int value;
		private boolean modified;

		private Result() {
			this.value = 0;
			this.modified = false;
		}
	}

	private static Result getMiddleValue(String line, Map<Integer, Set<Integer>> dependencyMap) {
		final Result result = new Result();
		final List<Integer> pages = Arrays
			.stream(toIntArray(line, ","))
			.boxed()
			.collect(Collectors.toList());
		final Set<Integer> pagesSet = new HashSet<>(pages);
		final Set<Integer> printedPages = new HashSet<>();
		final int length = pages.size();
		int i = 0;
		while (i < length) {
			final Set<Integer> dependencies = dependencyMap.computeIfAbsent(pages.get(i), p -> new HashSet<>());
			final Set<Integer> subDependencies = intersect(pagesSet, dependencies);
			if (printedPages.containsAll(subDependencies)) {
				printedPages.add(pages.get(i++));
			} else {
				subDependencies.removeAll(printedPages);
				for (Integer dependency : subDependencies) {
					pages.remove(dependency);
					pages.add(i, dependency);
				}
				result.modified = true;
			}
		}
		result.value = pages.get((int)((length - 1) / 2));
		return result;
	}

	private static Set<Integer> intersect(Set<Integer> a, Set<Integer> b) {
		final Set<Integer> intersection = new HashSet<>(a);
		intersection.retainAll(b);
		return intersection;
	}

	private static int[] toIntArray(String s, String separator) {
		return Arrays
			.stream(s.split(separator))
			.mapToInt(Integer::parseInt)
			.toArray();
	}

	public static void main(String[] args) throws IOException {
		final Map<Integer, Set<Integer>> dependencyMap = new HashMap<>();
		final BufferedReader br = new BufferedReader(new FileReader(args[0]));
		boolean readingDependencies = true;
		int sum = 0;
		int modifiedSum = 0;
		String line = br.readLine();
		while (line != null) {
			if ("".equals(line)) {
				readingDependencies = false;
			} else if (readingDependencies) {
				final int[] pair = toIntArray(line, "\\|");
				final Set<Integer> dependencies = dependencyMap.computeIfAbsent(pair[1], d -> new HashSet<>());
				dependencies.add(pair[0]);
			} else {
				final Result result = getMiddleValue(line, dependencyMap);
				if (result.modified) {
					modifiedSum += result.value;
				} else {
					sum += result.value;
				}
			}
			line = br.readLine();
		}
		System.out.println(String.format("First part: %d", sum));
		System.out.println(String.format("Second part: %d", modifiedSum));
	}
}