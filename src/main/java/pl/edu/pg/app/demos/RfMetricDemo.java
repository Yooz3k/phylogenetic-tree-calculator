package pl.edu.pg.app.demos;

import pl.edu.pg.app.metric.RfMetricEntry;

public class RfMetricDemo {
    private static final String FILENAME = "rf-noroot-1.xml";
    private static final String FILENAME2 = "rf-noroot-1-2.xml";

    public static void main(String[] args) {
        new RfMetricEntry().countRf(FILENAME, FILENAME2);
    }
}
