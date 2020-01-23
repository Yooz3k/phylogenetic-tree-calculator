package pl.edu.pg.app.consensus;

import pl.edu.pg.app.converter.ClusterToGraphConverter;
import pl.edu.pg.app.converter.GraphToClusterConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.Cluster;

import java.util.*;
import java.util.stream.Collectors;

public class ConsensusFinder
{
    protected float m_Threshold = 0.5f;
    protected List<String> m_TreeFilenames;

    public static void Execute( List<String> arguments )
    {
        ConsensusFinder consensusFinder = CreateConsensusFinder( arguments );

        GraphToClusterConverter graphConverter = new GraphToClusterConverter();

        List<Cluster> trees = consensusFinder.m_TreeFilenames.parallelStream()
                .map( GraphLoader::load )
                .map( graphConverter::convert )
                .collect( Collectors.toList() );

        Cluster consensusTree = consensusFinder.FindConsensus( trees );

        System.out.println( "Consensus tree: " + consensusTree );

        // Display consensus tree
        new ClusterToGraphConverter().Convert( consensusTree ).display();
    }

    private static ConsensusFinder CreateConsensusFinder( List<String> arguments )
    {
        float threshold = 0.5f;
        boolean unrooted = false;

        // Process arguments and return unparsed list
        boolean allArgumentsProcessed = false;

        ListIterator<String> argumentIterator = arguments.listIterator();
        while( !allArgumentsProcessed && argumentIterator.hasNext() )
        {
            switch( argumentIterator.next() )
            {
                case "-threshold":
                    threshold = Float.parseFloat( argumentIterator.next() );
                    break;

                case "-strict":
                    threshold = 1.0f;
                    break;

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
        ConsensusFinder consensusFinder;

        if( unrooted )
        {
            // Handle unrooted trees
            consensusFinder = new UnrootedConsensusFinder();
        }
        else
        {
            // Handle rooted trees
            consensusFinder = new ConsensusFinder();
        }

        // Update config
        consensusFinder.m_Threshold = threshold;
        consensusFinder.m_TreeFilenames = arguments.subList( argumentIterator.nextIndex(), arguments.size() );

        return consensusFinder;
    }

    protected Cluster FindConsensus( List<Cluster> trees )
    {
        Map<Cluster, Integer> counts = CountClusters( trees );
        Map<Cluster, Float> shares = ComputeShares( counts, trees.size() );

        // TODO: Create copy?
        Cluster consensusTree = trees.get( 0 );

        // Reconstruct the tree using only clusters with share above the threshold
        shares.entrySet().stream()
                .sorted( ( a, b ) -> Float.compare( a.getValue(), b.getValue() ) )
                .forEach( ( entry ) ->
        {
            Cluster cluster = entry.getKey();
            float share = entry.getValue();

            if( share < m_Threshold )
            {
                // Remove the node if share is below threshold
                consensusTree.Remove( cluster );
            }
            else if( !consensusTree.Contains( cluster ) )
            {
                // Insert new node if share is above threshold and it is not present in the tree
                consensusTree.Insert( cluster );
            }
        } );

        return consensusTree;
    }

    protected Map<Cluster, Integer> CountClusters( List<Cluster> trees )
    {
        List<Cluster> candidateTrees = trees.subList( 1, trees.size() );
        List<Cluster> candidateClusters = trees.get( 0 ).GetAllClusters();

        Map<Cluster, Integer> counts = new HashMap<>();

        // Initialize with ones
        candidateClusters.forEach( cluster -> counts.put( cluster, 1 ) );

        for( Cluster tree : candidateTrees )
        {
            List<Cluster> treeClusters = tree.GetAllClusters();

            // Iterate over all clusters in current tree
            for( Cluster candidate : candidateClusters )
            {
                if( treeClusters.contains( candidate ) )
                {
                    // Tree contains candidate cluster
                    counts.replace( candidate, 1 + counts.get( candidate ) );
                }
            }

            // Look for new clusters
            for( Cluster treeCluster : treeClusters )
            {
                if( !candidateClusters.contains( treeCluster ) )
                {
                    candidateClusters.add( treeCluster );
                    counts.put( treeCluster, 1 );
                }
            }
        }

        return counts;
    }

    protected Map<Cluster, Float> ComputeShares( Map<Cluster, Integer> counts, int numTrees )
    {
        Map<Cluster, Float> shares = new HashMap<>();

        // Compute share of each cluster
        counts.forEach( ( cluster, count ) ->
                shares.put( cluster, ( float ) count / numTrees ) );

        return shares;
    }
}
