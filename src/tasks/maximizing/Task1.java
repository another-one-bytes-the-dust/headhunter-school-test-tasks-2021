package tasks.maximizing;

import java.util.*;

public class Task1 {
    public static void main(String[] args) {
        System.out.println(new Solver().solve());
    }
}

class Solver {
    private final int[] arr;
    private final int numOfAccounts;
    private final int numOfManagers;
    private int maxAccountResource;

    public Solver() {
        Scanner scanner = new Scanner(System.in);
        numOfAccounts = scanner.nextInt();
        numOfManagers = scanner.nextInt();

        arr = new int[numOfAccounts];
        maxAccountResource = Integer.MIN_VALUE;

        for (int i = 0; i < numOfAccounts; i++) {
            int val = scanner.nextInt();
            arr[i] = val;

            if (val > maxAccountResource)
                maxAccountResource = val;
        }
    }

    public int solve() {
        return dichotomy(1, maxAccountResource);
    }

    public int dichotomy(int lower, int upper) {
        int mid, f1, f2;

        while (upper != lower)
        {
            mid = lower + (upper - lower) / 2;
            f1 = resourceFunction(mid);
            f2 = resourceFunction(mid + 1);

            if (f2 > f1)
                 lower = mid + 1;
            else upper = mid;
        }

        return resourceFunction(upper) == 0 ? 0 : lower;
    }

    private int resourceFunction(int x) {
        int amount = 0;

        for (int i : arr) amount += i / x;

        return amount >= numOfManagers ? x : 0;
    }
}