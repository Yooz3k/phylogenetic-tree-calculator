package pl.edu.pg.app.demos;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToClusterConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.Cluster;

import java.net.URISyntaxException;

public class ClusterDemo extends FileSourceDemo
{
    private static final String FILENAME = "simple_tree.xml";

    public static void main( String[] args ) throws URISyntaxException
    {
        String filepath = getFilePath( FILENAME );

        Graph g = GraphLoader.load( filepath );

        GraphConverter<Cluster> converter = new GraphToClusterConverter();
        Cluster cluster = converter.convert( g );

        // Print converted graph
        System.out.println( "Tree:\n" + cluster.toString() );

        // Tests
        Test_GetAllClusters( cluster );
        Test_GetTerminals( cluster );
        Test_Equals();

        // Display loaded graph
        g.display();
    }

    private static void Test_GetAllClusters( Cluster tree )
    {
        System.out.println( "\nTEST: GetAllClusters" );
        tree.GetAllClusters().forEach( System.out::println );
    }

    private static void Test_GetTerminals( Cluster tree )
    {
        System.out.println( "\nTEST: GetTerminals" );
        tree.GetTerminals().forEach( System.out::println );
    }

    private static void Test_Equals()
    {
        Cluster a = new Cluster( 0 );
        a.GetClusters().add( new Cluster( 1, "A" ) );
        a.GetClusters().add( new Cluster( 2, "B" ) );
        Cluster b = new Cluster( 3 );
        b.GetClusters().add( a );
        b.GetClusters().add( new Cluster( 4, "C" ) );

        Cluster c = new Cluster( 5 );
        c.GetClusters().add( new Cluster( 6, "B" ) );
        c.GetClusters().add( new Cluster( 7, "A" ) );
        Cluster d = new Cluster( 8 );
        d.GetClusters().add( new Cluster( 9, "C" ) );
        d.GetClusters().add( c );

        System.out.println( "\nTEST: Equals" );
        System.out.println( d.equals( b ) );

        if( d != b )
        {
            System.out.println( "b: " + b + " (terminals: " + b.GetTerminals() + ")" );
            System.out.println( "d: " + d + " (terminals: " + d.GetTerminals() + ")" );
        }
    }
}
