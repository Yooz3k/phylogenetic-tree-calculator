package pl.edu.pg.app;

import pl.edu.pg.app.divisions.DivisionsFamilyToTreeConverter;
import pl.edu.pg.app.divisions.TreeToDivisionsFamilyConverter;
import pl.edu.pg.app.compatibility.CompatibilityFinder;
import pl.edu.pg.app.consensus.ConsensusFinder;
import pl.edu.pg.app.cutting.CuttingOffLeafs;
import pl.edu.pg.app.metric.RfMetricEntry;
import pl.edu.pg.app.view.TreeViewer;

import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        System.out.println("***PHYLOGENETIC UNROOTED TREE CALCULATOR***\n");

        if (args.length == 0) {
            System.out.println("Nie wybrano żadnej akcji!");
            return;
        }

        chooseOption(Arrays.asList(args));
    }

    private static void chooseOption(List<String> args) {
        String option = args.get(0).toLowerCase();
        switch (option) {
            case "help":
                System.out.println(getHelp());
                break;
            case "-show":
                TreeViewer treeViewer = new TreeViewer();
                treeViewer.view(args.get(1));
                break;
            case "-divisionstotree":
                DivisionsFamilyToTreeConverter toTreeConverter = new DivisionsFamilyToTreeConverter();
                toTreeConverter.convert(args.get(1));
                break;
            case "-treetodivisions":
                TreeToDivisionsFamilyConverter toDivisionsConverter = new TreeToDivisionsFamilyConverter();
                toDivisionsConverter.convert(args.get(1));
                break;
            case "-consensus":
                ConsensusFinder.Execute(args.subList(1, args.size()));
                break;
            case "-compatibility":
                CompatibilityFinder.Execute(args.subList(1, args.size()));
                break;
            case "-rf":
                new RfMetricEntry().countRf(args.get(1), args.get(2));
                break;
            case "-cut":
                new CuttingOffLeafs().cut(args.get(1), args.subList(2, args.size()));
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
                + "   -threshold <x>                Tolerancja przy wyznaczaniu drzewa konsensusu [domyślnie: 0.5]\n"
                + "   -strict                       Wyznaczanie drzewa pełnego konsensusu\n"
                + "-divisionsToTree <file>          Zamiana \"rodziny zgodnych rozbić\" do postaci drzewa\n"
                + "-treeToDivisions <file>          Zamiana drzewa do postaci \"rodziny zgodnych rozbić\"\n"
                + "-compatibility <file>...         Wyznaczanie wspólnego rozszerzenia drzew\n"
                + "-rf <file1> <file2>              Wyznaczenie odległości topologicznej RF między parą drzew\n"
                + "-cut <file> <leaf>...            Obcięcie drzewa do zadanego podzbioru liści\n";
    }
}
