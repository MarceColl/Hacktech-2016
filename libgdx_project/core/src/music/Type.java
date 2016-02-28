/**
 * Created by dorien on 10/11/14.
 */

package music;

public enum Type {
    Chord, Note;

    public static Type parse(String s) {
        for (Type type : values()) {
            if (type.name().equals(s)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event type [" + s + "].");
    }
}

