package pl.edu.pg.app.converter;

import org.graphstream.graph.Graph;

public interface GraphConverter<T>
{
    T convert(Graph graph);
}