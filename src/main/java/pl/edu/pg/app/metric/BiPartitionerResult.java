package pl.edu.pg.app.metric;

import lombok.Value;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.Set;

@Value
public class BiPartitionerResult {
    private Set<String> partitions;
    private List<Node> nodesInPostOrder;
}
