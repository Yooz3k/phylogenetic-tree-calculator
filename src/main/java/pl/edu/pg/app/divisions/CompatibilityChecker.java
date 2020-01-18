package pl.edu.pg.app.divisions;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CompatibilityChecker {

    public boolean compatible(List<Pair<List<String>, List<String>>> divisions) {
        // Na początku sprawdzana jest poprawność każdego rozbicia
        for (Pair<List<String>, List<String>> division : divisions) {
            if (!intersectionEmpty(division.getLeft(), division.getRight())) {
                return false;
            }
        }

        // Następnie sprawdzany jest warunek, według którego dla dowolnych dwóch rozbić {A,B} i {C,D} spełniony jest warunek,
        // że dokładnie jest zbiór spośród AxC, AxD, BxC i BxD jest pusty.
        for (Pair<List<String>, List<String>> division1 : divisions) {
            for (Pair<List<String>, List<String>> division2 : divisions.subList(divisions.indexOf(division1) + 1, divisions.size())) {
                int emptyIntersections = 0;

                if (intersectionEmpty(division1.getLeft(), division2.getLeft())) {
                    emptyIntersections++;
                }
                if (intersectionEmpty(division1.getLeft(), division2.getRight())) {
                    emptyIntersections++;
                }
                if (intersectionEmpty(division1.getRight(), division2.getLeft())) {
                    emptyIntersections++;
                }
                if (intersectionEmpty(division1.getRight(), division2.getRight())) {
                    emptyIntersections++;
                }

                if (emptyIntersections != 1) {
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
}
