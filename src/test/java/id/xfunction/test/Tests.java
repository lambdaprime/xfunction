package id.xfunction.test;

import id.xfunction.Unchecked;
import id.xfunction.XUtils;

public class Tests {

    int m() throws Exception {
        return 0;
    }

    public void testSafe() {
        Unchecked.runUnchecked(this::m);
    }

    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new Tests().testSafe();
        XUtils.infiniteRandomStream(10)
            .limit(5)
            .forEach(System.out::println);
    }
}
