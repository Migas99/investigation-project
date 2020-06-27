package Application.Exceptions;

public class MapException extends Throwable {

    private String element;

    public MapException(String element) {
        this.element = element;
    }

    public void getError(){
        System.err.println("\nElement not mapped found!\nElement: " + this.element + "\n.");
    }
}
