package pl.edu.pg.app.divisions;

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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DivisionsFamilyToTreeConverter {

    int edgeIndex = 0;

    public void convert(String divisionsFilename) {
        List<List<String>> divisions = getDivisions(divisionsFilename);
        if (divisions == null) {
            return;
        }

        if (!checkCompatibility(divisions)) {
            return;
        }

        Graph graph = new DefaultGraph("g");

        //Sortujemy klastry wg ich liczebności rosnąco
        //Usuwamy też ewentualne puste klastry - są to błędne dane
        divisions = divisions.stream()
                .filter(c -> !c.isEmpty())
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());

        //Dodajemy wszystkie liście jako wierzchołki
        divisions.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .forEach(graph::addNode);

        Map<String, List<String>> childrenPerParents = new HashMap<>();

        //Pozostają nam klastry o więcej niż jednym elemencie
        divisions.stream()
                .filter(division -> division.size() > 1).forEach(division -> {
            //Dla każdego takiego klastra tworzymy nowy wierzchołek
            String nodeName = getNewNodeName(division);
            graph.addNode(nodeName);

            //Dla każdego elementu klastra zapisujemy do mapy krawędź między nim i nowo utworzonym wierzchołkiem
            childrenPerParents.put(nodeName, division);
        });

        LinkedHashMap<String, List<String>> childrenPerParentSortedBySizeDesc = sortBySizeDescending(childrenPerParents);

        Map<String, List<String>> mapWithFilteredChildren = filterChildren(childrenPerParentSortedBySizeDesc);

        //Teraz możemy dodać krawędzi do drzewa na podstawie mapy mapWithFilteredChildren
        mapWithFilteredChildren.entrySet().forEach(entry -> {
            entry.getValue().forEach(value -> {
                graph.addEdge(getEdgeName(), entry.getKey(), value, true);
            });
        });
        graph.getNodeSet().forEach(node -> node.addAttribute("label", node.getId()));

        graph.display();

        System.out.println(divisions);
    }


    private List<List<String>> getDivisions(String divisionsFilename) {
        String fileContent = readFile(divisionsFilename);
        if (fileContent == null) {
            return null;
        }

        List<String> separateDivisions = Arrays.asList(fileContent.split(";"));
        List<List<String>> divisions = new ArrayList<>();
        separateDivisions.forEach(division -> {
            List<String> nodes = Arrays.asList(division.split(","));
            divisions.add(nodes);
        });

        return divisions;
    }

    private String readFile(String divisionsFilename) {
        Path path;
        try {
            URL resourceUrl = getClass().getClassLoader()
                    .getResource(divisionsFilename);
            if (resourceUrl == null) {
                System.err.println("Nie znaleziono pliku o nazwie " + divisionsFilename);
                return null;
            }
            path = Paths.get(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Błąd odczytu pliku o nazwie " + divisionsFilename);
            e.printStackTrace();
            return null;
        }

        String divisions;
        try (Stream<String> lines = Files.lines(path)) {
            divisions = lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku!");
            e.printStackTrace();
            return null;
        }
        return divisions;
    }

    private boolean checkCompatibility(List<List<String>> divisions) {
        CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
        if (!compatibilityChecker.compatible(divisions)) {
            System.out.println("Wczytana rodzina rozbić nie jest zgodna!");
            return false;
        } else {
            System.out.println("Wczytana rodzina rozbić jest zgodna!");
            return true;
        }
    }

    private String getNewNodeName(List<String> divisions) {
        return String.join("&", divisions);
    }

    private String getEdgeName() {
        return String.valueOf(edgeIndex++);
    }

    private LinkedHashMap<String, List<String>> sortBySizeDescending(Map<String, List<String>> childrenPerParents) {
        return childrenPerParents.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<String, List<String>> filterChildren(Map<String, List<String>> childrenPerParentSortedBySizeDesc) {
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
        return mapWithFilteredChildren;
    }
}
