package Exceptions;

public class MapException extends Throwable {

    public MapException(){
        System.err.println("Element not mapped found!");
    }
}
