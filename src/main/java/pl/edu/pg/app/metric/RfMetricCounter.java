package pl.edu.pg.app.metric;

import org.graphstream.graph.Graph;

import java.util.HashSet;
import java.util.Set;

public class RfMetricCounter {

    public void count(Graph g1, Graph g2) {
        final GraphAnalyzerResult g1Result = new GraphAnalyzer(g1).analyzeAndSetAttributes();
        final GraphAnalyzerResult g2Result = new GraphAnalyzer(g2).analyzeAndSetAttributes();

        final BiPartitionerResult biPartResult1 = new BiPartitioner(g1Result.getRoot(), g1Result.getLeafs()).getNodesInPostOrderAndSetBipartitions(g1);
        final BiPartitionerResult biPartResult2 = new BiPartitioner(g2Result.getRoot(), g2Result.getLeafs()).getNodesInPostOrderAndSetBipartitions(g2);

        System.out.println(biPartResult1.toString());
        System.out.println(biPartResult2.toString());

        double distance = calculateRfDistance(biPartResult1.getPartitions(), biPartResult2.getPartitions());
        System.out.println("RF distance: " + distance);
    }

    private double calculateRfDistance(Set<String> partitions1, Set<String> partitions2) {
        Set<String> p1 = new HashSet<>(partitions1);
        Set<String> p2 = new HashSet<>(partitions2);

        p1.removeAll(partitions2);
        p2.removeAll(partitions1);

        return (p1.size() + p2.size()) / 2.0;

    }
}
