package pl.edu.pg.app.clusters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClustersFamilyToTreeConverter {

    public void convert(String clustersFilename) {
        List<List<String>> clusters = getClusters(clustersFilename);
        if (clusters == null) {
            return;
        }

        CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
        if (!compatibilityChecker.compatible(clusters)) {
            System.out.println("Wczytana rodzina klastrów nie jest zgodna!");
            return;
        } else {
            System.out.println("Wczytana rodzina klastrów jest zgodna!");
        }

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
}
