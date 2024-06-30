import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cache.*;
public class Main {
    public static void main(String[] args) {
        int cacheSize = 1024;
        int blockSize = 4;
        int[] kValues = { 1, 2, 4, 8, 16 };
        int numOperations = 100000;

        List<Double> amats = new ArrayList<>();
        for (int k : kValues) {
            Cache cache = new Cache(cacheSize, blockSize, k);
            Random random = new Random();

            for (int i = 0; i < numOperations; i++) {
                int key = random.nextInt(10000);
                cache.getData(key);
            }
            System.out.println("K = " + k);
            System.out.println(cache.getMetrics());
            System.out.println();

            amats.add(cache.getMetrics().getAMAT() * 1e9);
        }
        System.out.println("K values: " + kValues);
        System.out.println("AMAT values (ns):"+ amats);
    }
}
