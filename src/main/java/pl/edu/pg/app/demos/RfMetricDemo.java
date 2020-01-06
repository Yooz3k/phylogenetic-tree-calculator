package pl.edu.pg.app.demos;

import pl.edu.pg.app.metric.RfMetricEntry;

public class RfMetricDemo {
    private static final String FILENAME = "rf-graph-root.xml";
    private static final String FILENAME2 = "rf-graph-root2.xml";

    public static void main(String[] args) {
        new RfMetricEntry().countRf(FILENAME, FILENAME2);
    }
}
