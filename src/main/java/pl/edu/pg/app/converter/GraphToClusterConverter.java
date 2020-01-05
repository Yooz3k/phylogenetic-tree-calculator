package pl.edu.pg.app.converter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.struct.Cluster;

import java.util.List;
import java.util.stream.Collectors;

public class GraphToClusterConverter implements GraphConverter<Cluster>
{
    @Override
    public Cluster convert( Graph graph )
    {
        // Find root, the only node with 2-degree
        List<Node> rootCandidates = graph.getNodeSet().stream()
                .filter( candidate -> candidate.getDegree() == 2 )
                .collect( Collectors.toList() );

        assert (rootCandidates.size() == 1);

        return ProcessSubtree( null, rootCandidates.get( 0 ) );
    }

    private Cluster ProcessSubtree( Node parent, Node node )
    {
        // Check if this is terminal node
        if( node.getDegree() == 1 )
        {
            return new Cluster( node.hashCode(), node.getId() );
        }

        // Internal node
        Cluster cluster = new Cluster( node.hashCode() );

        for( Edge edge : node.getEachEdge() )
        {
            if( edge.getNode0() == parent ||
                    edge.getNode1() == parent )
            {
                // Don't process parent nodes
                continue;
            }

            Node child = edge.getNode0();
            if( child == node )
            {
                child = edge.getNode1();
            }

            cluster.Add( ProcessSubtree( node, child ) );
        }

        return cluster;
    }
}
