package pl.edu.pg.app.consensus;

public enum ConsensusError
{
    SUCCESS {
        @Override public String toString() {
            return "SUCCESS: Action executed successfully.";
        } },

    ERROR_LEAF_SET_MISMATCH {
        @Override public String toString() {
            return "ERROR_LEAF_SET_MISMATCH: All trees must define the same leaf set.";
        } },

    ERROR_EMPTY_SET {
        @Override public String toString() {
            return "ERROR_EMPTY_SET: At least one tree must be provided.";
        } },

    ERROR_INVALID_THRESHOLD {
        @Override public String toString() {
            return "ERROR_INVALID_THRESHOLD: Threshold must be value between 0.5 and 1.0.";
        } }
}
