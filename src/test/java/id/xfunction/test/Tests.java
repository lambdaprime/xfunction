package id.xfunction.test;

import id.xfunction.Unchecked;

public class Tests {

    int m() throws Exception {
        return 0;
    }

    public void testSafe() {
        Unchecked.run(this::m);
    }

    public static void main(String[] args) {
        new Tests().testSafe();
    }
}
