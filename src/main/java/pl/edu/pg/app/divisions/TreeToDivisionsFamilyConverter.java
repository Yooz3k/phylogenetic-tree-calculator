package pl.edu.pg.app.divisions;

import org.apache.commons.lang3.tuple.Pair;
import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeToDivisionsFamilyConverter {

    private AdjacencyList<String> tree;
    private List<String> leaves;
    private List<String> checkedNodes = new ArrayList<>();

    public void convert(String treeFilename) {
        tree = loadTreeFromFile(treeFilename);

        List<Pair<List<String>, List<String>>> divisions = new ArrayList<>();

        //Zbieramy liście
        leaves = tree.GetNodes().stream()
                .filter(node -> tree.GetDegree(node) == 1)
                .collect(Collectors.toList());

        List<Pair<String, String>> checkedPairs = new ArrayList<>();

        tree.GetNodes().forEach(node -> {
            if (!leaves.contains(node)) {
                List<String> edges = tree.GetNodeEdges(node);
                for (String edge : edges) {
                    if (!leaves.contains(edge)) {
                        // Symulujemy usunięcie krawędzi z grafu
                        String node1 = node;
                        String node2 = edge;
                        if (!checkedPairs.contains(Pair.of(node1, node2)) && !checkedPairs.contains(Pair.of(node2, node1))) {
                            //Unikamy ponownego sprawdzenia usunięcie tej samej krawędzi, sprawdzając obydwa wierzchołki łączone przez krawędź
                            checkedPairs.add(Pair.of(node, edge));

                            //checkedNodes zawiera informacje o wierzchołkach sprawdzonych w rekurencyjnej metodzie getLeaves
                            checkedNodes.clear();
                            List<String> node1Adjacents = new ArrayList<>(tree.GetNodeEdges(node1));
                            //W ten sposób symulowane jest wycięcie krawędzi - usuwamy sąsiedni wierzchołek
                            node1Adjacents.remove(node2);
                            //Rekurencyjnie zbierane są wszystkie liście
                            List<String> node1Leaves = new ArrayList<>(getLeaves(node1, node1Adjacents));
                            //Sortowanie elementów zbiorów zgodnie z kolejnością alfabetyczną
                            node1Leaves.sort(Comparator.naturalOrder());

                            //Te same operacje wykonujemy dla drugiego wierzchołka badanej krawędzi
                            checkedNodes.clear();
                            List<String> node2Adjacents = new ArrayList<>(tree.GetNodeEdges(node2));
                            node2Adjacents.remove(node1);
                            List<String> node2Leaves = new ArrayList<>(getLeaves(node2, node2Adjacents));
                            node2Leaves.sort(Comparator.naturalOrder());

                            //Dodajemy wynik sprawdzenia krawędzi do rodziny rozbić
                            divisions.add(Pair.of(node1Leaves, node2Leaves));
                        }
                    }
                }
            }
        });

        System.out.println(divisions);
    }

    private Set<String> getLeaves(String checkedNode, List<String> adjacentNodes) {
        Set<String> currentLeaves = new HashSet<>(4);
        adjacentNodes.forEach(adjNode -> {
            if (leaves.contains(adjNode)) {
                currentLeaves.add(adjNode);
            } else {
                //Rekurencyjnie "zbieramy" wszystkie liście dla kolejnych węzłów, które liśćmi nie są.
                checkedNodes.add(checkedNode);
                List<String> edgesToCheck = new ArrayList<>(tree.GetNodeEdges(adjNode));
                edgesToCheck.removeAll(checkedNodes);
                currentLeaves.addAll(getLeaves(adjNode, edgesToCheck));
            }
        });
        return currentLeaves;
    }

    private AdjacencyList<String> loadTreeFromFile(String filename) {
        String path = GraphLoader.getFilePath(filename);
        Graph graph = GraphLoader.load(path);
        //graph.display();

        GraphToAdjListConverter converter = new GraphToAdjListConverter();
        return converter.convert(graph);
    }
}
