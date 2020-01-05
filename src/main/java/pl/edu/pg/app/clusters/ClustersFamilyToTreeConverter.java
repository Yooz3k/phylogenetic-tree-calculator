package pl.edu.pg.app.clusters;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClustersFamilyToTreeConverter {

    int edgeIndex = 0;

    public void convert(String clustersFilename) {
        List<List<String>> clusters = getClusters(clustersFilename);
        if (clusters == null) {
            return;
        }

        if (!checkCompatibility(clusters)) {
            return;
        }

        Graph graph = new DefaultGraph("g");

        //Sortujemy klastry wg ich liczebności rosnąco
        //Usuwamy też ewentualne puste klastry - są to błędne dane
        clusters = clusters.stream()
                .filter(c -> !c.isEmpty())
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());

        //Klastry jednoelementowe to liście - dodajemy je jako węzły do drzewa
        clusters.stream()
                .filter(cluster -> cluster.size() == 1)
                .forEach(cluster -> graph.addNode(cluster.get(0)));

        Map<String, List<String>> childrenPerParents = new HashMap<>();

        //Pozostają nam klastry o więcej niż jednym elemencie
        clusters.stream()
                .filter(cluster -> cluster.size() > 1).forEach(cluster -> {
            //Dla każdego takiego klastra tworzymy nowy wierzchołek
            String nodeName = getNewNodeName(cluster);
            graph.addNode(nodeName);

            //Dla każdego elementu klastra zapisujemy do mapy krawędź między nim i nowo utworzonym wierzchołkiem
            childrenPerParents.put(nodeName, cluster);
        });

        LinkedHashMap<String, List<String>> childrenPerParentSortedBySizeDesc =
                childrenPerParents.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, List<String>> mapWithFilteredChildren = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> childrenPerParent : childrenPerParentSortedBySizeDesc.entrySet()) {
            List<String> currentChildren = new ArrayList<>(childrenPerParent.getValue());
            for (Map.Entry<String, List<String>> childrenPerAnotherParent : childrenPerParentSortedBySizeDesc.entrySet()) {
                if (!childrenPerParent.equals(childrenPerAnotherParent) && currentChildren.containsAll(childrenPerAnotherParent.getValue())) {
                    currentChildren.removeAll(childrenPerAnotherParent.getValue());
                    currentChildren.add(childrenPerAnotherParent.getKey());
                }
            }
            mapWithFilteredChildren.put(childrenPerParent.getKey(), currentChildren);
        }

        //Teraz możemy dodać krawędzi do drzewa na podstawie mapy mapWithFilteredChildren
        mapWithFilteredChildren.entrySet().forEach(entry -> {
            entry.getValue().forEach(value -> {
                graph.addEdge(getEdgeName(), entry.getKey(), value, true);
            });
        });

        graph.getNodeSet().forEach(node -> node.addAttribute("label", node.getId()));
        graph.display();

        System.out.println(clusters);
    }


    private List<List<String>> getClusters(String clustersFilename) {
        String fileContent = readFile(clustersFilename);
        if (fileContent == null) {
            return null;
        }

        List<String> separateClusters = Arrays.asList(fileContent.split(";"));
        List<List<String>> clusters = new ArrayList<>();
        separateClusters.forEach(cluster -> {
            List<String> nodes = Arrays.asList(cluster.split(","));
            clusters.add(nodes);
        });

        return clusters;
    }

    private String readFile(String clustersFilename) {
        Path path;
        try {
            URL resourceUrl = getClass().getClassLoader()
                    .getResource(clustersFilename);
            if (resourceUrl == null) {
                System.err.println("Nie znaleziono pliku o nazwie " + clustersFilename);
                return null;
            }
            path = Paths.get(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Błąd odczytu pliku o nazwie " + clustersFilename);
            e.printStackTrace();
            return null;
        }

        String clusters;
        try (Stream<String> lines = Files.lines(path)) {
            clusters = lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku!");
            e.printStackTrace();
            return null;
        }
        return clusters;
    }

    private boolean checkCompatibility(List<List<String>> clusters) {
        CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
        if (!compatibilityChecker.compatible(clusters)) {
            System.out.println("Wczytana rodzina klastrów nie jest zgodna!");
            return false;
        } else {
            System.out.println("Wczytana rodzina klastrów jest zgodna!");
            return true;
        }
    }

    private String getNewNodeName(List<String> clusters) {
        return String.join("&", clusters);
    }

    private String getEdgeName() {
        return String.valueOf(edgeIndex++);
    }
}
