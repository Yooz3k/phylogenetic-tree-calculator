package pl.edu.pg.app.consensus;

import com.google.common.collect.Lists;
import pl.edu.pg.app.struct.Cluster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UnrootedConsensusFinder extends ConsensusFinder
{
    @Override
    protected Cluster FindConsensus( List<Cluster> trees )
    {
        // 1. Fuckery step - generate rooted trees from unrooted tree set with root in each node
        List<List<Cluster>> rootedTrees = trees.stream()
                .map( this::ShuffleTree )
                .collect( Collectors.toList() );

        List<List<Cluster>> setsToCheck = Lists.cartesianProduct( rootedTrees );

        // 2. Compute consensus tree for each of the tree combinations
        List<Cluster> consensusTrees = new ArrayList<>();

        for( List<Cluster> set : setsToCheck )
        {
            consensusTrees.add( super.FindConsensus( set ) );
        }

        // 3. Select largest consensus tree
        return consensusTrees.stream()
                .max( Comparator.comparingInt( cluster -> cluster.GetAllClusters().size() ) )
                .orElseThrow();
    }

    private List<Cluster> ShuffleTree( Cluster tree )
    {
        return tree.GetAllClusters().stream()
                .map( tree::GetRootedAt )
                .collect( Collectors.toList() );
    }
}
