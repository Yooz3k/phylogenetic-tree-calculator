package pl.edu.pg.app;

import pl.edu.pg.app.clusters.ClustersFamilyToGraphConverter;

public class Application {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Nie wybrano żadnej akcji!");
            return;
        }

        String firstArg = args[0].toLowerCase();
        //Pozostałe elementy jako kolejne case'y
        switch (firstArg) {
            case "help":
                System.out.println(getHelp());
                break;
            case "-clusters":
                ClustersFamilyToGraphConverter clusters = new ClustersFamilyToGraphConverter();
                clusters.execute();
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
