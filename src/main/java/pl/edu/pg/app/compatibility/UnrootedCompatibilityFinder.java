package pl.edu.pg.app.compatibility;

import com.google.common.collect.Lists;
import pl.edu.pg.app.converter.UnrootedAlgorithm;
import pl.edu.pg.app.struct.Cluster;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UnrootedCompatibilityFinder extends CompatibilityFinder
    implements UnrootedAlgorithm
{
    @Override
    protected Cluster FindCompatibleTree( List<Cluster> trees )
    {
        // Generate rooted trees from unrooted tree set with root in each node
        List<List<Cluster>> rootedTrees = trees.stream()
                .map( this::ShuffleTree )
                .collect( Collectors.toList() );

        // Compute compatible tree for each of the tree combinations and select smallest tree
        Cluster compatibilityTree = Lists.cartesianProduct( rootedTrees ).stream()
                .map( super::FindCompatibleTree )
                .filter( Objects::nonNull )
                .min( Comparator.comparingInt( cluster -> cluster.GetAllClusters().size() ) )
                .orElse( null );

        if( compatibilityTree != null ) compatibilityTree
                .RemoveSimpleNodes()
                .Unroot()
                .ResetIndex();

        return compatibilityTree;
    }
}
