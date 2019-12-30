package pl.edu.pg.app;

import java.util.stream.Stream;

public class Application {

    public static void main(String[] args) {
        if (args.length > 0) {
            Stream.of(args)
                    .forEach(System.out::println);
        } else {
            System.out.println("Hello world!");
        }
    }
}
