package com.aabo.testing.suite;

import javax.xml.bind.ValidationException;

public class Node<T> {

    public enum Type{
        LIST,
        MAP,
        NATIVE,
        COMMONS
    }

    private String identifier;
    private T payload;
    private Type type;
    private Node next;
    private Node previous;


    Node(String identifier, T payload, Type type){
        next = null;
        this.previous = null;
        this.identifier = identifier;
        this.payload = payload;
        this.type = type;
    }

    Node(T payload, Type type){
        next = null;
        this.previous = null;
        this.identifier = null;
        this.payload = payload;
        this.type = type;
    }

    public void setIdentifier(String identifier){
        this.identifier=identifier;
    }

    public void setPayload(T payload){
        this.payload=payload;
    }

    public String getIdentifier() {return identifier;}

    public T getPayload() {return payload;}

    void setNext(Node node) throws ValidationException {
        if(node == this){
            throw new ValidationException("El nodo no puede apuntarse a si mismo");
        }else{
            this.next = node;
        }
    }

    void setPrevious(Node node) throws ValidationException {
        if(node == this){
            throw new ValidationException("El nodo no puede apuntarse a si mismo");
        }else{
            this.previous = node;
        }
    }

    boolean hasNext() {
        return next != null;
    }

    Node getNext() {
        return next;
    }
}
