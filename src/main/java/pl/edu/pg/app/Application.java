package pl.edu.pg.app;

import pl.edu.pg.app.clusters.ClustersFamilyToTreeConverter;
import pl.edu.pg.app.clusters.TreeToClustersFamilyConverter;
import pl.edu.pg.app.consensus.ConsensusFinder;
import pl.edu.pg.app.metric.RfMetricEntry;
import pl.edu.pg.app.view.TreeViewer;

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
            case "-show":
                TreeViewer treeViewer = new TreeViewer();
                treeViewer.view(args.get(1));
                break;
            case "-clusterstotree":
                ClustersFamilyToTreeConverter toTreeConverter = new ClustersFamilyToTreeConverter();
                toTreeConverter.convert(args.get(1));
                break;
            case "-treetoclusters":
                TreeToClustersFamilyConverter toClustersConverter = new TreeToClustersFamilyConverter();
                toClustersConverter.convert(args.get(1));
                break;
            case "-consensus":
                ConsensusFinder.Execute(args.subList(1, args.size()));
                break;
            case "-rf":
                new RfMetricEntry().countRf(args.get(1), args.get(2));
                break;
            default:
                System.out.println("Nie rozpoznano polecenia!");
                break;
        }
    }

    private static String getHelp() {
        return "Dostępne akcje:\n"
                + "-show <file>                     Wyświetlenie drzewa\n"
                + "-consensus [options] <file>...   Wyznaczanie drzewa konsensusu dla zadanego zbioru drzew\n"
                + "   Options:\n"
                + "   -threshold <x>                Tolerancja przy wyznaczaniu drzewa konsensusus [domyślnie: 0.5]\n"
                + "   -strict                       Wyznaczanie drzewa pełnego konsensusu\n"
                + "-clustersToTree <file>           Zamiana \"rodziny zgodnych klastrów\" do postaci drzewa\n"
                + "-treeToClusters <file>           Zamiana drzewa do postaci \"rodziny zgodnych klastrów\"\n"
                + "-rf <file1> <file2>              Wyznaczenie odległości topologicznej RF między parą drzew\n"
                + "-cut <file> <leaf>...            Obcięcie drzewa do zadanego podzbioru liści\n"
                //Tutaj proponuję dopisywać info o kolejnych funkcjonalnościach
                + "...inne opcje...\n";
    }
}
