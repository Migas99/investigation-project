package Exceptions;

public class NodeException extends Throwable {

    public NodeException(){
        System.err.println("Node not found!");
    }

    public NodeException(String msg){
        System.err.println(msg);
    }
}
