package pl.edu.pg.app.converter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import pl.edu.pg.app.struct.AdjacencyList;

public class GraphToAdjListConverter extends GraphConverter<AdjacencyList> {

    @Override
    public AdjacencyList convert(Graph graph) {
        AdjacencyList adjList = new AdjacencyList();

        for (Edge edge : graph.getEachEdge()) {
            adjList.add(edge.getNode0().getIndex(), edge.getNode1().getIndex());
        }

        return adjList;
    }
}
