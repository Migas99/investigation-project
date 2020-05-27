package Exceptions;

public class NodeException extends Throwable {

    public NodeException(){
        System.err.println("Node not found!\n");
    }

    public NodeException(String msg){
        System.err.println(msg);
    }
}
