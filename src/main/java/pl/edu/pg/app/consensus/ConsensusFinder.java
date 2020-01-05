package pl.edu.pg.app.consensus;

import pl.edu.pg.app.converter.GraphToClusterConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.Cluster;

import java.util.*;
import java.util.stream.Collectors;

public class ConsensusFinder
{
    public static void Execute( List<String> treeFilenames )
    {
        GraphToClusterConverter graphConverter = new GraphToClusterConverter();

        List<Cluster> trees = treeFilenames.parallelStream()
                .map( GraphLoader::load )
                .map( graphConverter::convert )
                .collect( Collectors.toList() );

        ConsensusFinder consensusFinder = new ConsensusFinder();

        Cluster consensusTree = consensusFinder.FindMajorityConsensus( trees );

        System.out.println( "Consensus tree: " + consensusTree );
    }

    public Cluster FindStrictConsensus( List<Cluster> trees )
    {
        return FindConsensus( trees, 1.0f );
    }

    public Cluster FindMajorityConsensus( List<Cluster> trees )
    {
        return FindConsensus( trees, 0.5f );
    }

    public Cluster FindConsensus( List<Cluster> trees, float threshold )
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

            if( share < threshold )
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

    private Map<Cluster, Integer> CountClusters( List<Cluster> trees )
    {
        List<Cluster> candidateTrees = trees.subList( 1, trees.size() );
        List<Cluster> candidateClusters = trees.get( 0 ).GetAllClusters();

        Map<Cluster, Integer> counts = new HashMap<>();

        // Initialize with ones
        candidateClusters.forEach( cluster -> counts.put( cluster, 1 ) );

        for( Cluster tree : candidateTrees )
        {
            List<Cluster> treeClusters = tree.GetAllClusters();

            // Iterate over all clades in current tree
            for( Cluster candidate : candidateClusters )
            {
                if( treeClusters.contains( candidate ) )
                {
                    // Tree contains candidate clade
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

    private Map<Cluster, Float> ComputeShares( Map<Cluster, Integer> counts, int numTrees )
    {
        Map<Cluster, Float> shares = new HashMap<>();

        // Compute share of each clade
        counts.forEach( ( cluster, count ) ->
                shares.put( cluster, ( float ) count / numTrees ) );

        return shares;
    }
}
