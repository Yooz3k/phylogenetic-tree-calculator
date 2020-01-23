package pl.edu.pg.app.compatibility;

import pl.edu.pg.app.struct.Cluster;

import java.util.List;

public class UnrootedCompatibilityFinder extends CompatibilityFinder
{
    @Override
    protected Cluster FindCompatibleTree( List<Cluster> nodes, List<List<Cluster>> treeClusters )
    {
        return null;
    }
}
