package pl.edu.pg.app.compatibility;

public enum CompatibilityError
{
    SUCCESS {
        @Override public String toString() {
            return "SUCCESS: Action executed successfully.";
        } },

    ERROR_TREES_INCOMPATIBLE {
        @Override public String toString() {
            return "ERROR_TREES_INCOMPATIBLE: Trees are incompatible.";
        } }
}
