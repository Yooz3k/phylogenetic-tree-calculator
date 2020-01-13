package pl.edu.pg.app.metric;

import org.graphstream.graph.Graph;

import java.util.HashSet;
import java.util.Set;

public class RfMetricCounter {

    public void count(Graph g1, Graph g2) {
        final GraphAnalyzerResult g1Result = new GraphAnalyzer(g1).analyzeAndSetAttributes();
        final GraphAnalyzerResult g2Result = new GraphAnalyzer(g2).analyzeAndSetAttributes();

        final BiPartitionerResult biPartResult1 = new BiPartitioner(g1Result.getRoot(), g1Result.getLeafs()).getNodesInPostOrderAndSetBiPartitions(g1);
        final BiPartitionerResult biPartResult2 = new BiPartitioner(g2Result.getRoot(), g2Result.getLeafs()).getNodesInPostOrderAndSetBiPartitions(g2);

        if (checkIfSameNumberOfLeaves(biPartResult1.getPartitions(), biPartResult2.getPartitions())) {
            System.out.println("Drzewa są różnej wielkości!");
        }

        System.out.println(biPartResult1.toString());
        System.out.println(biPartResult2.toString());

        double distance = calculateRfDistance(biPartResult1.getPartitions(), biPartResult2.getPartitions());
        System.out.println("RF distance: " + distance);
    }

    private boolean checkIfSameNumberOfLeaves(Set<String> partitions, Set<String> partitions2) {
        int len1 = partitions.stream().findFirst().get().length();
        int len2 = partitions2.stream().findFirst().get().length();
        return (len1 - len2) != 0;
    }

    private double calculateRfDistance(Set<String> partitions1, Set<String> partitions2) {
        Set<String> p1 = new HashSet<>(partitions1);
        Set<String> p2 = new HashSet<>(partitions2);

        p1.removeAll(partitions2);
        p2.removeAll(partitions1);

        return (p1.size() + p2.size());

    }
}
