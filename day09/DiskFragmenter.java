import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DiskFragmenter {
    private static String readDiskMap(String filename) throws IOException {
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.readLine();
        }
    }

    private static List<String> buildBlocks(String diskMap) {
        final List<String> blocks = new ArrayList<>();
        int id = 0;
        for (int index = 0; index < diskMap.length(); index++) {
            final int digit = diskMap.charAt(index) - '0';
            final String stringToAdd;
            if (index % 2 == 0) {
                stringToAdd = String.valueOf(id);
            } else {
                stringToAdd = ".";
            }
            for (int i = 0; i < digit; i++) {
                blocks.add(stringToAdd);
            }
            if (index % 2 == 0) {
                id++;
            }
        }
        return blocks;
    }

    private static List<String> defragmentBlocks(List<String> blocks) {
        int i = 0;
        int j = blocks.size() - 1;
        while (i < j) {
            while (!blocks.get(i).equals(".") && i < j) {
                i++;
            }
            while (blocks.get(j).equals(".") && i < j) {
                j--;
            }
            if (i >= j) {
                break;
            }
            blocks.set(i, blocks.get(j));
            blocks.set(j, ".");
        }
        return blocks;
    }

    private static List<String> defragmentFiles(List<String> blocks) {
        int jEnd = blocks.size() - 1;
        while (jEnd >= 0) {
            while (jEnd > 0 && blocks.get(jEnd).equals(".")) {
                jEnd--;
            }
            int jStart = jEnd;
            while (jStart >= 0 && blocks.get(jStart).equals(blocks.get(jEnd))) {
                jStart--;
            }
            final int fileSize = jEnd - jStart;
            int iStart = 0;
            while (iStart <= jStart) {
                while (iStart <= jStart && !blocks.get(iStart).equals(".")) {
                    iStart++;
                }
                int iEnd = iStart;
                while (iEnd <= jStart && blocks.get(iEnd).equals(".")) {
                    iEnd++;
                }
                final int space_available = iEnd - iStart;
                if (space_available >= fileSize) {
                    for (int k = iStart; k < iStart + fileSize; k++) {
                        blocks.set(k, blocks.get(jEnd));
                    }
                    for (int k = jStart + 1; k < jEnd + 1; k++) {
                        blocks.set(k, ".");
                    }
                    break;
                }
                iStart = iEnd;
            }
            jEnd = jStart;
        }
        return blocks;
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static BigInteger computeChecksum(List<String> blocks) {
        BigInteger sum = BigInteger.ZERO;
        for (int index = 0; index < blocks.size(); index++) {
            if (isNumeric(blocks.get(index))) {
                sum = sum.add(new BigInteger(blocks.get(index)).multiply(BigInteger.valueOf(index)));
            }
        }
        return sum;
    }

    public static void main(String[] args) throws IOException {
        final String diskMap = readDiskMap(args[0]);
        System.out.println(computeChecksum(defragmentBlocks(buildBlocks(diskMap))));
        System.out.println(computeChecksum(defragmentFiles(buildBlocks(diskMap))));
    }
}
