package pl.edu.pg.app.demos;

import pl.edu.pg.app.cutting.CuttingOffLeafs;

import java.util.Arrays;

public class CuttingTreeDemo {

    private static final String FILENAME = "rf-graph-root.xml";

    public static void main(String[] args) {
        new CuttingOffLeafs().cut(FILENAME, Arrays.asList("B", "G"));
    }
}
