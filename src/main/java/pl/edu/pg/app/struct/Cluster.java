package pl.edu.pg.app.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cluster implements Comparable<Cluster>
{
    private Cluster m_Parent = null;
    private List<Cluster> m_Clusters = new ArrayList<>();
    private boolean m_IsTerminal = false;
    private int m_Id;
    private String m_Label;

    public Cluster( int id )
    {
        m_Id = id;
    }

    public Cluster( int id, String label )
    {
        this( id );
        m_Label = label;
        m_IsTerminal = true;
    }

    public Cluster( int id, Cluster... clusters )
    {
        this( id );

        for( Cluster cluster : clusters )
        {
            Add( cluster );
        }
    }

    public static Cluster CopyOf( Cluster cluster )
    {
        Cluster newCluster = new Cluster( cluster.m_Id );
        newCluster.m_Label = cluster.m_Label;
        newCluster.m_IsTerminal = cluster.m_IsTerminal;

        for( Cluster child : cluster.m_Clusters )
        {
            Cluster childCopy = CopyOf( child );
            childCopy.m_Parent = newCluster;

            newCluster.m_Clusters.add( childCopy );
        }

        return newCluster;
    }

    public Cluster ResetIndex()
    {
        // Generate new node indexes
        ResetIndex( new Random() );
        return this;
    }

    private void ResetIndex( Random random )
    {
        m_Id = random.nextInt();

        for( Cluster child : m_Clusters )
        {
            child.ResetIndex( random );
        }
    }

    public void Add( Cluster cluster )
    {
        // Store reference to the parent cluster
        cluster.m_Parent = this;

        m_Clusters.add( cluster );
        m_IsTerminal = false;
    }

    public void Insert( Cluster cluster )
    {
        // Cache for following filter calls
        List<String> currentTreeTerminals = GetTerminals();

        // Find the most suitable place for new cluster
        // Remove leaves which are not present in current tree
        List<String> clusterTerminals = cluster.GetTerminalsStream()
                .filter( currentTreeTerminals::contains )
                .collect( Collectors.toList() );

        for( Cluster child : m_Clusters )
        {
            // Child contains all terminals the new cluster references
            // Insert there (the child may have other terminals)
            if( child.GetTerminals().containsAll( clusterTerminals ) )
            {
                child.Insert( cluster );
                return;
            }
        }

        // We didn't found any suitable child to insert the cluster
        // So we are suitable :D

        // Remove all clusters referenced by new one
        m_Clusters = m_Clusters.stream()
                .filter( child -> !clusterTerminals.containsAll( child.GetTerminals() ) )
                .collect( Collectors.toList() );

        m_Clusters.add( cluster );

        // Node is not terminal anymore (if it was)
        m_IsTerminal = false;
    }

    public void Remove( Cluster cluster )
    {
        List<String> clusterTerminals = cluster.GetTerminals();

        if( GetTerminals().equals( clusterTerminals ) )
        {
            if( m_Parent != null )
            {
                // Move all child clusters to the parent
                m_Parent.m_Clusters.addAll( m_Clusters );
                m_Parent.m_Clusters.remove( this );
            }
        }
        else
        {
            // Find cluster which contains removed cluster
            for( Cluster child : m_Clusters )
            {
                if( child.GetTerminals().containsAll( clusterTerminals ) )
                {
                    child.Remove( cluster );
                    return;
                }
            }
        }
    }

    public int GetId()
    {
        return m_Id;
    }

    public boolean IsTerminal()
    {
        return m_IsTerminal;
    }

    public String GetLabel()
    {
        assert( m_IsTerminal );
        return m_Label;
    }

    public List<Cluster> GetClusters()
    {
        return m_Clusters;
    }

    public List<Cluster> GetAllClusters()
    {
        List<Cluster> allClusters = new ArrayList<>();
        allClusters.add( this );
        allClusters.addAll( m_Clusters.stream()
                .flatMap( cluster -> cluster.GetAllClusters().stream() )
                .collect( Collectors.toList() ) );
        return allClusters;
    }

    public Cluster GetRootedAt( Cluster node )
    {
        if( node.m_Id == m_Id )
        {
            // Look at me...
            // I'm the root now
            Cluster newRoot = new Cluster( m_Id );
            newRoot.m_Label = m_Label;
            newRoot.m_IsTerminal = m_IsTerminal;
            newRoot.m_Clusters.addAll( m_Clusters );

            Cluster previous = newRoot;
            Cluster previousParent = node;
            Cluster parent = m_Parent;
            while( parent != null )
            {
                Cluster newParent = new Cluster( parent.m_Id );
                newParent.m_Clusters = new ArrayList<>( parent.m_Clusters );
                newParent.m_Clusters.remove( previousParent );
                newParent.m_Parent = previous;

                previous.m_Clusters.add( newParent );

                previous = newParent;
                previousParent = parent;
                parent = parent.m_Parent;
            }

            newRoot.RemoveSimpleNodes();

            return newRoot;
        }

        // Early quit for terminal nodes
        if( m_IsTerminal )
        {
            return null;
        }

        for( Cluster cluster : m_Clusters )
        {
            Cluster newRootedTree = cluster.GetRootedAt( node );

            if( newRootedTree != null )
            {
                return newRootedTree;
            }
        }

        return null;
    }

    public List<String> GetTerminals()
    {
        return GetTerminalsStream()
                .collect( Collectors.toList() );
    }

    public Stream<String> GetTerminalsStream()
    {
        var terminals = m_Clusters.stream()
                .flatMap( Cluster::GetTerminalsStream )
                .collect( Collectors.toCollection( ArrayList::new ) );

        if( m_IsTerminal )
        {
            terminals.add( m_Label );
        }

        return terminals.stream()
                .sorted();
    }

    public boolean Contains( Cluster cluster )
    {
        return this.equals( cluster ) ||
                m_Clusters.stream()
                        .anyMatch( child -> child.Contains( cluster ) );
    }

    public Cluster RemoveSimpleNodes()
    {
        if( m_Clusters.size() == 1 )
        {
            Cluster next = m_Clusters.get( 0 );

            if( next.m_IsTerminal && !m_IsTerminal )
            {
                m_Label = next.m_Label;
                m_IsTerminal = true;
            }

            m_Clusters = next.m_Clusters;

            RemoveSimpleNodes();
        }
        else
        {
            for( Cluster cluster : m_Clusters )
            {
                cluster.RemoveSimpleNodes();
            }
        }
        return this;
    }

    public Cluster Unroot()
    {
        if( m_Clusters.size() == 2 )
        {
            Cluster childA = m_Clusters.get( 0 );
            Cluster childB = m_Clusters.get( 1 );

            if( !childA.m_IsTerminal )
            {
                m_Clusters.remove( childA );
                m_Clusters.addAll( childA.m_Clusters );
            }
            else if( !childB.m_IsTerminal )
            {
                m_Clusters.remove( childB );
                m_Clusters.addAll( childB.m_Clusters );
            }
        }
        return this;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            // The same reference
            return true;
        }

        if( null == obj )
        {
            // Null reference
            return false;
        }

        if( this.getClass() != obj.getClass() )
        {
            // Different classes
            return false;
        }

        return this.GetTerminals().equals( (( Cluster ) obj).GetTerminals() );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if( m_IsTerminal )
        {
            sb.append( m_Label );
        }

        if( !m_Clusters.isEmpty() )
        {
            sb.append( '(' );
            sb.append( m_Clusters.stream()
                    .map( Cluster::toString )
                    .collect( Collectors.joining( "," ) ) );
            sb.append( ')' );
        }

        return sb.toString();
    }

    @Override
    public int compareTo( Cluster cluster )
    {
        if( this == cluster )
        {
            // The same reference
            return 0;
        }

        if( null == cluster )
        {
            // Null reference
            return 1;
        }

        return m_Id - cluster.m_Id;
    }
}
