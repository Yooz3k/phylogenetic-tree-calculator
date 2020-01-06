package pl.edu.pg.app.metric;

import org.graphstream.graph.Graph;

import java.net.URISyntaxException;

public class RfMetricEntry {

    public void countRf(String filename1, String filename2) {
        try {
            Graph g = GraphUtils.loadGraph(filename1);
            Graph g2 = GraphUtils.loadGraph(filename2);

            g.display();
            g2.display();

            new RfMetricCounter().count(g, g2);
        } catch (URISyntaxException e) {
            System.out.println("Wrong filepath. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
