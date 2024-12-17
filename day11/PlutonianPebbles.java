import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PlutonianPebbles {
    private static String[] readStones(String filename) throws IOException {
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.readLine().split(" ");
        }
    }

    private static BigInteger blink(String stone, int step, Map<String, BigInteger> dp) {
        if (step == 0) {
            return BigInteger.ONE;
        }
        final String blinkKey = String.format("%s-%d", stone, step);
        if (dp.containsKey(blinkKey)) {
            return dp.get(blinkKey);
        }
        final List<String> stones = new ArrayList<>();
        if (stone.equals("0")) {
            stones.add("1");
        } else if (stone.length() % 2 == 0) {
            final int len = stone.length() / 2;
            stones.add(stone.substring(0, len));
            stones.add(new BigInteger(stone.substring(len)).toString());
        } else {
            stones.add(new BigInteger(stone).multiply(new BigInteger("2024")).toString());
        }
        dp.put(blinkKey, stones.stream()
                .map(s -> blink(s, step - 1, dp))
                .reduce(BigInteger.ZERO, BigInteger::add)
        );
        return dp.get(blinkKey);
    }

    public static void main(String[] args) throws IOException {
        final Map<String, BigInteger> dp = new HashMap<>();
        final String[] stones = readStones(args[0]);
        final BigInteger totalStones = Stream.of(stones)
                .map(s -> blink(s, 75, dp))
                .reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println(totalStones);
    }
}
