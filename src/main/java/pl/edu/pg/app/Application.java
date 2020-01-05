package pl.edu.pg.app;

import pl.edu.pg.app.clusters.ClustersFamilyToGraphConverter;

import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        System.out.println("***PHYLOGENETIC ROOTED TREE CALCULATOR***\n");

        if (args.length == 0) {
            System.out.println("Nie wybrano żadnej akcji!");
            return;
        }

        chooseOption(Arrays.asList(args));
    }

    private static void chooseOption(List<String> args) {
        String option = args.get(0).toLowerCase();
        //Pozostałe elementy jako kolejne case'y
        switch (option) {
            case "help":
                System.out.println(getHelp());
                break;
            case "-clusterstograph":
                ClustersFamilyToGraphConverter clusters = new ClustersFamilyToGraphConverter();
                //Drugim elementem listy argumentów powinien być plik ze zbiorami wierzchołków
                String clustersFilename = args.get(1);
                clusters.convert(clustersFilename);
                break;
            default:
                System.out.println("Nie rozpoznano polecenia!");
                break;
        }
    }

    private static String getHelp() {
        return "Dostępne akcje:\n"
                + "-clusters: zamiana \"rodziny zgodnych klastrów\" do postaci drzewa\n"
                //Tutaj proponuję dopisywać info o kolejnych funkcjonalnościach
                + "...inne opcje...\n";
    }
}
