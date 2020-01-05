package pl.edu.pg.app.struct;

import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Setter
public class AdjacencyList {

    public TreeMap<String, TreeSet<String>> adjacentNodesPerNode = new TreeMap<>();

    public void add(String edge1, String edge2, boolean directed) {
        TreeSet<String> adjacentToEdge1 = adjacentNodesPerNode.get(edge1);
        TreeSet<String> adjacentToEdge2 = adjacentNodesPerNode.get(edge2);

        if (adjacentToEdge1 == null) {
            adjacentToEdge1 = new TreeSet<>();
        }
        adjacentToEdge1.add(edge2);
        adjacentNodesPerNode.put(edge1, adjacentToEdge1);

        if (adjacentToEdge2 == null) {
            adjacentToEdge2 = new TreeSet<>();
        }

        if (!directed) {
            adjacentToEdge2.add(edge1);
        }

        adjacentNodesPerNode.put(edge2, adjacentToEdge2);
    }

    public void put(String key, TreeSet<String> value) {
        adjacentNodesPerNode.put(key, value);
    }

    public Set<String> get(String key) {
        return adjacentNodesPerNode.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, TreeSet<String>> node : adjacentNodesPerNode.entrySet()) {
            String adjacentNodes = node.getValue().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            sb.append(node.getKey())
                    .append(": ")
                    .append(adjacentNodes)
                    .append("\n");
        }

        return sb.toString();
    }
}
