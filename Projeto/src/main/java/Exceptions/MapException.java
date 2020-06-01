package Exceptions;

public class MapException extends Throwable {

    public MapException(String element) {
        System.err.println("\nElement not mapped found!\nElement: " + element + "\n.");
    }
}
