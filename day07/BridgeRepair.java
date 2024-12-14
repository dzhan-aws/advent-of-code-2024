import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class BridgeRepair {
    private static class Equation {
        private final BigInteger leftSide;
        private final BigInteger[] rightSide;

        public Equation(BigInteger leftSide, BigInteger[] rightSide) {
            this.leftSide = leftSide;
            this.rightSide = rightSide;
        }

        public String toString() {
            return leftSide + " " + Arrays.toString(rightSide);
        }

        public boolean isValid() {
            if (rightSide.length == 0) {
                return leftSide.equals(BigInteger.ZERO);
            }

            final BigInteger[] newRightSide = Arrays.copyOfRange(this.rightSide, 0, this.rightSide.length - 1);
            final BigInteger sumLeftSide = this.leftSide.subtract(this.rightSide[this.rightSide.length - 1]);
            final Equation sumEquation = new Equation(sumLeftSide, newRightSide);
            if (sumEquation.isValid()) {
                return true;
            }
            if (this.leftSide.remainder(this.rightSide[this.rightSide.length - 1]).equals(BigInteger.ZERO)) {
                final Equation multiEquation = new Equation(this.leftSide.divide(this.rightSide[this.rightSide.length - 1]), newRightSide);
                if (multiEquation.isValid()) {
                    return true;
                }
            }
            if (this.leftSide.compareTo(BigInteger.ZERO) < 0) {
                return false;
            }
            final String lefSideString = this.leftSide.toString();
            final String lastRightSideString = this.rightSide[this.rightSide.length - 1].toString();
            if (lefSideString.endsWith(lastRightSideString)) {
                final String newLeftSideString = lefSideString.substring(0, lefSideString.length() - lastRightSideString.length());
                final Equation concatEquation = new Equation(new BigInteger(newLeftSideString), newRightSide);
                return concatEquation.isValid();
            }
            return false;
        }
    }

    private static Equation equationFromString(String equation) {
        final String[] equationParts = equation.split(":");
        final BigInteger leftSide = new BigInteger(equationParts[0]);
        final String[] operands = equationParts[1].strip().split(" ");
        final BigInteger[] rightSide = new BigInteger[operands.length];
        for (int i = 0; i < rightSide.length; i++) {
            rightSide[i] = new BigInteger(operands[i]);
        }
        return new Equation(leftSide, rightSide);
    }

    public static void main(String[] args) throws IOException {
        final BufferedReader br = new BufferedReader(new FileReader(args[0]));
        final BigInteger result = br.lines()
                .map(BridgeRepair::equationFromString)
                .filter(Equation::isValid)
                .map(equation -> new BigInteger(String.valueOf(equation.leftSide)))
                .reduce(new BigInteger("0"), BigInteger::add);
        System.out.println(result);
    }
}
