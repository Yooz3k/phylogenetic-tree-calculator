package pl.edu.pg.app.demos;

import pl.edu.pg.app.cutting.CuttingOffLeafs;

import java.util.Arrays;

public class CuttingTreeDemo {

    private static final String FILENAME = "rf-root-1.xml";

    public static void main(String[] args) {
        new CuttingOffLeafs().cut(FILENAME, Arrays.asList("B", "G"));
    }
}
