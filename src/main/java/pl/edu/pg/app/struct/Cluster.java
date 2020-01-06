package pl.edu.pg.app.struct;

import java.util.ArrayList;
import java.util.List;
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

    public void Add( Cluster cluster )
    {
        // Store reference to the parent cluster
        cluster.m_Parent = this;

        m_Clusters.add( cluster );
        m_IsTerminal = false;
    }

    public void Insert( Cluster cluster )
    {
        // Find the most suitable place for new cluster
        List<String> clusterTerminals = cluster.GetTerminals();

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
    }

    public void Remove( Cluster cluster )
    {
        List<String> clusterTerminals = cluster.GetTerminals();

        if( GetTerminals().equals( clusterTerminals ) )
        {
            assert (m_Parent != null);

            // Move all child clusters to the parent
            m_Parent.m_Clusters.addAll( m_Clusters );
            m_Parent.m_Clusters.remove( this );
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

        // Cluster not found?
        assert( false );
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

    public boolean HasTerminal( int id )
    {
        if( !m_IsTerminal )
        {
            return m_Clusters.stream()
                    .anyMatch( cluster -> cluster.HasTerminal( id ) );
        }
        return id == m_Id;
    }

    public List<String> GetTerminals()
    {
        if( !m_IsTerminal )
        {
            return m_Clusters.stream()
                    .flatMap( Cluster::GetTerminalsStream )
                    .sorted()
                    .collect( Collectors.toList() );
        }
        return List.of( m_Label );
    }

    public Stream<String> GetTerminalsStream()
    {
        if( !m_IsTerminal )
        {
            return m_Clusters.stream()
                    .flatMap( Cluster::GetTerminalsStream );
        }
        return Stream.of( m_Label );
    }

    public boolean Contains( Cluster cluster )
    {
        return this.equals( cluster ) ||
                m_Clusters.stream()
                        .anyMatch( child -> child.Contains( cluster ) );
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
        else
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
