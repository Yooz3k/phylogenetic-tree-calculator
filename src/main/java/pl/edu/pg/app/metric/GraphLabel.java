package pl.edu.pg.app.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GraphLabel {
    LABEL("label"),
    LEAF("leaf"),
    ROOT("root"),
    PARTITION("partition"),
    BIT_PARTITION("bit-partition"),
    VISITED("VISITED"),;

    private String text;
}
