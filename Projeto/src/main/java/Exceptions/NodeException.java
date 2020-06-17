package Exceptions;

public class NodeException extends Throwable {

    private String element;

    public NodeException(String element){
        this.element = element;
    }

    public void getError(){
        System.err.println("Node '" + this.element + "' not found!");
    }
}
