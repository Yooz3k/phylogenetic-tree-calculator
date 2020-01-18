package pl.edu.pg.app.divisions;

import org.apache.commons.lang3.tuple.Pair;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DivisionsFamilyToTreeConverter {

    int edgeIndex = 0;

    public void convert(String divisionsFilename) {
        List<Pair<List<String>, List<String>>> divisions = getDivisions(divisionsFilename);
        if (divisions == null) {
            return;
        }

        if (!checkCompatibility(divisions)) {
            return;
        }

        Graph graph = new DefaultGraph("g");

        List<List<String>> flattenedPairs = new ArrayList<>();
        for (Pair<List<String>, List<String>> d : divisions) {
            flattenedPairs.add(d.getLeft());
            flattenedPairs.add(d.getRight());
        }

        //Sortujemy rozbicia wg ich liczebności rosnąco
        flattenedPairs = flattenedPairs.stream()
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());

        //Dodajemy wszystkie liście jako wierzchołki
        flattenedPairs.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .forEach(graph::addNode);

        int numLeaves = graph.getNodeCount();

        LinkedHashMap<String, List<String>> childrenPerParents = new LinkedHashMap<>();

        flattenedPairs = flattenedPairs.stream()
                .filter(p -> p.size() <= numLeaves/2)
                .collect(Collectors.toList());

        flattenedPairs.forEach(pair -> {
            List<String> addedElement = getAddedElement(childrenPerParents, pair);
            String nodeName = getNewNodeName(addedElement);
            childrenPerParents.put(nodeName, addedElement);
        });

        childrenPerParents.keySet().forEach(graph::addNode);

        LinkedHashMap<String, List<String>> childrenPerParentSortedBySizeDesc = sortBySizeDescending(childrenPerParents);

        Map<String, List<String>> mapWithFilteredChildren = filterChildren(childrenPerParentSortedBySizeDesc);

        //Teraz możemy dodać krawędzi do drzewa na podstawie mapy mapWithFilteredChildren
        mapWithFilteredChildren.entrySet().forEach(entry -> {
            entry.getValue().forEach(value -> {
                graph.addEdge(getEdgeName(), entry.getKey(), value, false);
            });
        });

        int maxPairSize = flattenedPairs.stream()
                .map(List::size)
                .max(Comparator.comparingInt(p -> p))
                .get();

        List<String> biggestNodesToBeConnected = new ArrayList<>();
        childrenPerParents.keySet().forEach(cpp -> {
            if (cpp.split("&").length == maxPairSize) {
                biggestNodesToBeConnected.add(cpp);
            }
        });

        for (String node1 : biggestNodesToBeConnected) {
            for (String node2 : biggestNodesToBeConnected.subList(biggestNodesToBeConnected.indexOf(node1) + 1, biggestNodesToBeConnected.size())) {
                graph.addEdge(getEdgeName(), node1, node2, false);
            }
        }

        graph.getNodeSet().forEach(node -> node.addAttribute("label", node.getId()));

        graph.display();

        System.out.println(divisions);
    }

    private List<String> getAddedElement(Map<String, List<String>> childrenPerParents, final List<String> pair) {
        List<String> addedElements = new ArrayList<>(pair);

        List<String> reverseOrderedKeys = new ArrayList<>(childrenPerParents.keySet());
        Collections.reverse(reverseOrderedKeys);
        for (String key : reverseOrderedKeys) {
            List<String> value = childrenPerParents.get(key);
            List<String> decomposedValue = value.stream()
                    .map(v -> Arrays.asList(v.split("&")))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (pair.containsAll(decomposedValue)) {
                int index = addedElements.indexOf(decomposedValue.get(0));
                if (index >= 0) {
                    addedElements.removeAll(decomposedValue);
                    addedElements.add(index, key);
                }
            }
        }

        return addedElements;
    }


    private List<Pair<List<String>, List<String>>> getDivisions(String divisionsFilename) {
        String fileContent = readFile(divisionsFilename);
        if (fileContent == null) {
            return null;
        }

        fileContent = fileContent.replaceAll("\\{", "");

        List<Pair<List<String>, List<String>>> pairsOfDivisions = new ArrayList<>();

        List<String> separateDivisionPairs = Arrays.asList(fileContent.split("}"));
        separateDivisionPairs.forEach(stringPair -> {
            List<String> dividedPair = Arrays.asList(stringPair.split(";"));
            List<String> left = Arrays.asList(dividedPair.get(0).split(","));
            List<String> right = Arrays.asList(dividedPair.get(1).split(","));
            pairsOfDivisions.add(Pair.of(left, right));
        });

        return pairsOfDivisions;
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

    private boolean checkCompatibility(List<Pair<List<String>, List<String>>> divisions) {
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
