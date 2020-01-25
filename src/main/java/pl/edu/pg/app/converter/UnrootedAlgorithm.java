package pl.edu.pg.app.converter;

import pl.edu.pg.app.struct.Cluster;

import java.util.List;
import java.util.stream.Collectors;

public interface UnrootedAlgorithm
{
    default List<Cluster> ShuffleTree( Cluster tree )
    {
        return tree.GetAllClusters().stream()
                .filter( cluster -> !cluster.IsTerminal() )
                .map( tree::GetRootedAt )
                .collect( Collectors.toList() );
    }
}
