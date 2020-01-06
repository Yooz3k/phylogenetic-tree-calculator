package pl.edu.pg.app.struct;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdjacencyList<T extends Comparable<T>>
{
    private Map<T, Set<T>> m_NodeEdges = new TreeMap<>();

    public void AddNode( T node )
    {
        // Insert new entry only if it does not exist
        if( !m_NodeEdges.containsKey( node ) )
        {
            m_NodeEdges.put( node, new TreeSet<>() );
        }
    }

    public void RemoveNode( T node )
    {
        m_NodeEdges.remove( node );
        m_NodeEdges.forEach( ( $, edges ) -> edges.remove( node ) );
    }

    public List<T> GetNodes()
    {
        return List.copyOf( m_NodeEdges.keySet() );
    }

    public boolean Empty()
    {
        return m_NodeEdges.isEmpty();
    }

    public void AddEdge( T a, T b )
    {
        // Insert new undirected edge
        m_NodeEdges.get( a ).add( b );
        m_NodeEdges.get( b ).add( a );
    }

    public void AddEdge( T a, T b, boolean directed )
    {
        // Insert new (optionally) directed edge
        m_NodeEdges.get( a ).add( b );

        if( !directed )
        {
            m_NodeEdges.get( b ).add( a );
        }
    }

    public boolean HasEdge( T a, T b )
    {
        // Check if edge between a and b exists
        return m_NodeEdges.get( a ).contains( b );
    }

    public int GetDegree( T node )
    {
        // Get degree of the node
        return m_NodeEdges.get( node ).size();
    }

    public List<T> GetNodeEdges( T node )
    {
        return List.copyOf( m_NodeEdges.get( node ) );
    }

    public List<T> CollectInOrder( T node )
    {
        Stack<T> unvisitedNodes = new Stack<>();
        unvisitedNodes.add( node );

        Set<T> visitedNodes = new TreeSet<>();

        while( !unvisitedNodes.isEmpty() )
        {
            T next = unvisitedNodes.pop();

            // Don't use 'contains', compare references (ids)
            if( visitedNodes.stream()
                    .anyMatch( visitedNode -> visitedNode.compareTo( next ) == 0 ) )
            {
                continue;
            }

            // Process nodes adjacent to next
            unvisitedNodes.addAll( m_NodeEdges.get( next ) );

            // Mark this node as visited
            visitedNodes.add( next );
        }

        return List.copyOf( visitedNodes );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for( Map.Entry<T, Set<T>> node : m_NodeEdges.entrySet() )
        {
            String adjacentNodes = node.getValue().stream()
                    .map(String::valueOf)
                    .collect( Collectors.joining(", " ) );

            sb.append( node.getKey() )
                    .append(": ")
                    .append(adjacentNodes)
                    .append("\n");
        }

        return sb.toString();
    }
}
