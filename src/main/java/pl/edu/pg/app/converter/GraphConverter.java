package pl.edu.pg.app.converter;

import org.graphstream.graph.Graph;

public abstract class GraphConverter<T> {
    public abstract T convert(Graph graph);
}