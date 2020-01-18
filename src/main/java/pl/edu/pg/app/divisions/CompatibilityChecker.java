package pl.edu.pg.app.divisions;

import java.util.List;

public class CompatibilityChecker {

    public boolean compatible(List<List<String>> divisions) {
        for (List<String> division1 : divisions) {
            for (List<String> division2 : divisions.subList(divisions.indexOf(division1)+1, divisions.size())) {
                if (!intersectionEmpty(division1, division2) && !isSubset(division1, division2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Jeżeli jakikolwiek węzeł ze zbioru division1 występuje w zbiorze division2 lub odwrotnie, część wspólna nie jest pusta.
     * Metoda zwraca true, jeżeli część wspólna jest pusta. Zwraca false, jeśli nie jest.
     */
    private boolean intersectionEmpty(List<String> division1, List<String> division2) {
        for (String node1 : division1) {
            for (String node2 : division2) {
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
    private boolean isSubset(List<String> division1, List<String> division2) {
        return division1.containsAll(division2) || division2.containsAll(division1);
    }
}
