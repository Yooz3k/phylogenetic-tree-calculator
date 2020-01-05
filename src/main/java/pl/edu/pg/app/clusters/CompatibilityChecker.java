package pl.edu.pg.app.clusters;

import java.util.List;

public class CompatibilityChecker {

    public boolean compatible(List<List<String>> clusters) {
        for (List<String> cluster1 : clusters) {
            for (List<String> cluster2 : clusters.subList(clusters.indexOf(cluster1)+1, clusters.size())) {
                if (!intersectionEmpty(cluster1, cluster2) && !isSubset(cluster1, cluster2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Jeżeli jakikolwiek węzeł ze zbioru cluster1 występuje w zbiorze cluster2 lub odwrotnie, część wspólna nie jest pusta.
     * Metoda zwraca true, jeżeli część wspólna jest pusta. Zwraca false, jeśli nie jest.
     */
    private boolean intersectionEmpty(List<String> cluster1, List<String> cluster2) {
        for (String node1 : cluster1) {
            for (String node2 : cluster2) {
                if (node1.equals(node2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Jeżeli którykolwiek ze zbiorów jest podzbiorem drugiego, metoda zwraca wartość true. W przypadku braku podzbiorów - false.
     */
    private boolean isSubset(List<String> cluster1, List<String> cluster2) {
        return cluster1.containsAll(cluster2) || cluster2.containsAll(cluster1);
    }
}
