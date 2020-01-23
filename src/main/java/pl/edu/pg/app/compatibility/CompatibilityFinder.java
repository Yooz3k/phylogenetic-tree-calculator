package pl.edu.pg.app.compatibility;

import pl.edu.pg.app.converter.ClusterToGraphConverter;
import pl.edu.pg.app.converter.GraphToClusterConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;
import pl.edu.pg.app.struct.Cluster;

import java.util.*;
import java.util.stream.Collectors;

public class CompatibilityFinder
{
    protected Random m_Random = new Random();
    protected List<String> m_TreeFilenames;

    public static void Execute( List<String> arguments )
    {
        CompatibilityFinder compatibilityFinder = CreateCompatibilityFinder( arguments );

        GraphToClusterConverter graphConverter = new GraphToClusterConverter();
        List<Cluster> trees = compatibilityFinder.m_TreeFilenames.parallelStream()
                .map( GraphLoader::load )
                .map( graphConverter::convert )
                .collect( Collectors.toList() );

        List<List<Cluster>> treeClusters = trees.stream()
                .map( Cluster::GetAllClusters )
                .collect( Collectors.toList() );

        Cluster compatibleTree = compatibilityFinder.FindCompatibleTree( trees, treeClusters );

        if( compatibleTree != null )
        {
            System.out.println( "Compatible tree: " + compatibleTree );

            // Display compatible tree
            new ClusterToGraphConverter().Convert( compatibleTree ).display();
        }
        else
        {
            System.out.println( "Trees incompatible" );
        }
    }

    private static CompatibilityFinder CreateCompatibilityFinder( List<String> arguments )
    {
        boolean unrooted = false;

        // Process arguments and return unparsed list
        boolean allArgumentsProcessed = false;

        ListIterator<String> argumentIterator = arguments.listIterator();
        while( !allArgumentsProcessed && argumentIterator.hasNext() )
        {
            switch( argumentIterator.next() )
            {
                case "-unrooted":
                    unrooted = true;
                    break;

                default:
                    // Move iterator back - argument was not processed
                    argumentIterator.previous();
                    allArgumentsProcessed = true;
                    break;
            }
        }

        // Create the finder instance
        CompatibilityFinder compatibilityFinder;

        if( unrooted )
        {
            // Handle unrooted trees
            compatibilityFinder = new UnrootedCompatibilityFinder();
        }
        else
        {
            // Handle rooted trees
            compatibilityFinder = new CompatibilityFinder();
        }

        // Update config
        compatibilityFinder.m_TreeFilenames = arguments.subList( argumentIterator.nextIndex(), arguments.size() );

        return compatibilityFinder;
    }

    protected Cluster FindCompatibleTree( List<Cluster> nodes, List<List<Cluster>> treeClusters )
    {
        // Implementation of BuildST from
        // https://arxiv.org/pdf/1510.07758.pdf

        // Cover simple cases - single-species trees and two-species trees
        List<String> uniqueTerminals = nodes.stream()
                .flatMap( Cluster::GetTerminalsStream )
                .distinct()
                .collect( Collectors.toList() );

        if( uniqueTerminals.size() == 1 )
        {
            return new Cluster( m_Random.nextInt(), uniqueTerminals.get( 0 ) );
        }

        if( uniqueTerminals.size() == 2 )
        {
            return new Cluster( m_Random.nextInt(),
                    new Cluster( m_Random.nextInt(), uniqueTerminals.get( 0 ) ),
                    new Cluster( m_Random.nextInt(), uniqueTerminals.get( 1 ) ) );
        }

        List<Cluster> newNodes = new ArrayList<>();

        for( List<Cluster> tree : treeClusters )
        {
            List<Cluster> intersection = IntersectClusters( nodes, tree );

            if( intersection.size() == 1 )
            {
                // Remove singletons by replacing them with their children
                newNodes.addAll( intersection.get( 0 ).GetClusters() );
            }
            else
            {
                // Leave unchanged
                newNodes.addAll( intersection );
            }
        }

        // Get connected components of the new node set
        List<List<Cluster>> connectedComponents = GetConnectedComponents( newNodes );

        if( connectedComponents.size() == 1 )
        {
            // If newNodes subset is still fully connected, the tree is incompatible
            return null;
        }

        Cluster root = new Cluster( m_Random.nextInt() );

        for( List<Cluster> component : connectedComponents )
        {
            // Call recursively for each connected component
            Cluster subtreeCompatibilityTree = FindCompatibleTree( component, treeClusters );

            if( subtreeCompatibilityTree != null )
            {
                root.Add( subtreeCompatibilityTree );
            }
            else
            {
                // If any of subtrees is incompatible, the tree is incompatible
                return null;
            }
        }

        return root;
    }

    protected List<List<Cluster>> GetConnectedComponents( List<Cluster> nodes )
    {
        // Create the graph where nodes are connected if both lead to one or more common terminals
        AdjacencyList<Cluster> relatedClusters = new AdjacencyList<>();

        nodes.forEach( relatedClusters::AddNode );

        for( Cluster node0 : nodes )
        {
            for( Cluster node1 : nodes )
            {
                if( node0 == node1 )
                {
                    continue;
                }

                if( AnyIntersects( node0.GetTerminals(), node1.GetTerminals() ) )
                {
                    relatedClusters.AddEdge( node0, node1 );
                }
            }
        }

        // Extract components from the created graph
        List<List<Cluster>> connectedComponents = new ArrayList<>();

        while( !relatedClusters.Empty() )
        {
            List<Cluster> clusters = relatedClusters.GetNodes();
            List<Cluster> visitedClusters = relatedClusters.CollectInOrder( clusters.get( 0 ) );

            // Include source cluster in the visited set
            assert( visitedClusters.contains( clusters.get( 0 ) ) );

            connectedComponents.add( visitedClusters );

            visitedClusters.forEach( relatedClusters::RemoveNode );
        }

        return connectedComponents;
    }

    protected List<Cluster> IntersectClusters( List<Cluster> a, List<Cluster> b )
    {
        return a.stream()
                .filter( aCluster -> b.stream().anyMatch( bCluster -> aCluster.GetId() == bCluster.GetId() ) )
                .collect( Collectors.toList() );
    }

    protected <T> boolean AnyIntersects( List<T> a, List<T> b )
    {
        return a.stream().anyMatch( b::contains );
    }
}
