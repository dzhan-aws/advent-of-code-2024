import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MullItOver {

	public static void main(String[] args) throws IOException {
		final String content = new String(Files.readAllBytes(Paths.get(args[0])));
		final Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
		final Matcher matcher = pattern.matcher(content);
		long result = 0;
		boolean active = true;
		while (matcher.find()) {
			final String theMatch = matcher.group(0);
			if (theMatch.equals("do()")) {
				active = true;
			} else if (theMatch.equals("don't()")) {
				active = false;
			} else if (active) {
				final int x = Integer.parseInt(matcher.group(1));
				final int y = Integer.parseInt(matcher.group(2));
				result += x * y;
			}
		}
		System.out.println(String.format("Multiplication: %d", result));
	}
}