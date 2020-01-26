package pl.edu.pg.app.consensus;

import com.google.common.collect.Lists;
import pl.edu.pg.app.converter.UnrootedAlgorithm;
import pl.edu.pg.app.struct.Cluster;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UnrootedConsensusFinder extends ConsensusFinder
    implements UnrootedAlgorithm
{
    @Override
    protected Cluster FindConsensus( List<Cluster> trees )
    {
        // Generate rooted trees from unrooted tree set with root in each node
        List<List<Cluster>> rootedTrees = trees.stream()
                .map( this::ShuffleTree )
                .collect( Collectors.toList() );

        // Compute consensus tree for each of the tree combinations and select largest consensus tree
        return Lists.cartesianProduct( rootedTrees ).stream()
                .map( super::FindConsensus )
                .max( Comparator.comparingInt( cluster -> cluster.GetAllClusters().size() ) )
                .orElseThrow()
                .RemoveSimpleNodes()
                .Unroot()
                .ResetIndex();
    }
}
