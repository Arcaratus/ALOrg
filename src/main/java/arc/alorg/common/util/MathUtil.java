package arc.alorg.common.util;

public class MathUtil {
    // Returns a quick and scuffed (-1) ^ n
    public static int powNegOne(int n) {
        return n % 2 == 0 ? 1 : -1;
    }

    public static int coneFunc(int n) {
        return (int) (Math.ceil(n / 2F) * powNegOne(n + 1));
    }
}
