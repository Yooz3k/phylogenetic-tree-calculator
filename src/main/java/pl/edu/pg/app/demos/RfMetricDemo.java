package pl.edu.pg.app.demos;

import pl.edu.pg.app.metric.RfMetricEntry;

public class RfMetricDemo {
    private static final String FILENAME = "rf-root-1.xml";
    private static final String FILENAME2 = "rf-root-1-2.xml";

    public static void main(String[] args) {
        new RfMetricEntry().countRf(FILENAME, FILENAME2);
    }
}
