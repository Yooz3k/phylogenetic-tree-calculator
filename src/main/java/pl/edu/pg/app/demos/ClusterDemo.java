package pl.edu.pg.app.demos;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToClusterConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.Cluster;

import java.net.URISyntaxException;

public class ClusterDemo extends FileSourceDemo
{
    private static final String FILENAME = "((((n0,n1),n4),n6),(n9,n10)).xml";

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
        Test_Insert();
        Test_Remove();

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

        if( !d.equals( b ) )
        {
            System.out.println( "b: " + b + " (terminals: " + b.GetTerminals() + ")" );
            System.out.println( "d: " + d + " (terminals: " + d.GetTerminals() + ")" );
        }
    }

    private static void Test_Insert()
    {
        Cluster a = new Cluster( 0,
                new Cluster( 1,
                        new Cluster( 11, "A" ),
                        new Cluster( 12, "C" ) ),
                new Cluster( 2, "B" ) );
        Cluster b = new Cluster( 3,
                new Cluster( 4, "A" ),
                new Cluster( 5, "D" ) );
        a.Insert( b );

        Cluster c = new Cluster( 6,
                new Cluster( 13,
                    new Cluster( 7,
                            new Cluster( 8, "A" ),
                            new Cluster( 9, "D" ) ),
                    new Cluster( 14, "C" ) ),
                new Cluster( 10, "B" ) );

        System.out.println( "\nTEST: Insert" );
        System.out.println( c.equals( a ) );

        if( !c.equals( a ) )
        {
            System.out.println( "a: " + a + " (terminals: " + a.GetTerminals() + ")" );
            System.out.println( "c: " + c + " (terminals: " + c.GetTerminals() + ")" );
        }
    }

    private static void Test_Remove()
    {
        Cluster a = new Cluster( 0,
                new Cluster( 1,
                        new Cluster( 11, "A" ),
                        new Cluster( 12, "C" ),
                        new Cluster( 15, "D" ) ),
                new Cluster( 2, "B" ) );

        Cluster c = new Cluster( 6,
                new Cluster( 13,
                        new Cluster( 7,
                                new Cluster( 8, "A" ),
                                new Cluster( 9, "D" ) ),
                        new Cluster( 14, "C" ) ),
                new Cluster( 10, "B" ) );
        Cluster b = new Cluster( 3,
                new Cluster( 4, "A" ),
                new Cluster( 5, "D" ) );
        c.Remove( b );

        System.out.println( "\nTEST: Remove" );
        System.out.println( c.equals( a ) );

        if( !c.equals( a ) )
        {
            System.out.println( "a: " + a + " (terminals: " + a.GetTerminals() + ")" );
            System.out.println( "c: " + c + " (terminals: " + c.GetTerminals() + ")" );
        }
    }
}
