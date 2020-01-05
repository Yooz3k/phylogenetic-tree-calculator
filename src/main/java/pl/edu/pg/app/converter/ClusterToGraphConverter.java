package pl.edu.pg.app.converter;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import pl.edu.pg.app.struct.Cluster;

public class ClusterToGraphConverter
{
    public Graph Convert( Cluster tree )
    {
        Graph graph = new DefaultGraph( "g" );

        ProcessSubtree( graph, tree );

        return graph;
    }

    private String ProcessSubtree( Graph g, Cluster subtree )
    {
        String subtreeId = String.valueOf( subtree.GetId() );
        g.addNode( subtreeId );

        for( Cluster cluster : subtree.GetClusters() )
        {
            String clusterId = ProcessSubtree( g, cluster );
            g.addEdge( String.format( "%s--%s", subtreeId, clusterId ),
                    subtreeId, clusterId );
        }

        if( subtree.IsTerminal() )
        {
            g.getNode( subtreeId ).addAttribute( "label", subtree.GetLabel() );
        }

        return subtreeId;
    }
}
