package pl.edu.pg.app.demos;

import pl.edu.pg.app.cutting.CuttingOffLeafs;

import java.util.Arrays;
import java.util.HashSet;

public class CuttingTreeDemo {

    private static final String FILENAME = "rf-graph-root.xml";

    public static void main(String[] args) {
        new CuttingOffLeafs().cut(FILENAME, new HashSet<>(Arrays.asList("F", "G", "D", "B")));
    }
}
