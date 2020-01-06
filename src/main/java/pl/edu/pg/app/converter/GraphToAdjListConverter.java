package pl.edu.pg.app.converter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import pl.edu.pg.app.struct.AdjacencyList;

public class GraphToAdjListConverter implements GraphConverter<AdjacencyList<String>> {

    @Override
    public AdjacencyList<String> convert(Graph graph) {
        AdjacencyList<String> adjList = new AdjacencyList<>();

        for (Edge edge : graph.getEachEdge()) {
            String node0 = edge.getNode0().getId();
            String node1 = edge.getNode1().getId();
            adjList.AddNode( node0 );
            adjList.AddNode( node1 );
            adjList.AddEdge( node0, node1, edge.isDirected() );
        }

        return adjList;
    }
}
