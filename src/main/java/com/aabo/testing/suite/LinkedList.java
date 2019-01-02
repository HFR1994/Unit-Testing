package com.aabo.testing.suite;

import javax.xml.bind.ValidationException;

public class LinkedList {

    private Node parent;
    private Node last;
    private Node current;
    private int size;
    private boolean start;

    LinkedList() {
        parent = null;
        size = 0;
        start = true;
    }

    void append(Node nodo) throws ValidationException {
        if (parent == null) {
            parent = nodo;
            nodo.setNext(null);
            last = nodo;
        } else {
            nodo.setPrevious(last);
            last.setNext(nodo);
            last = nodo;
        }
        size++;
    }

    Node getElement(String key){
        Node current = parent;
        while(current != null){
            if(current.getIdentifier().equals(key)){
                return current;
            }
            current = current.getNext();
        }
        throw new NullPointerException("\nERROR: No key \"" + key + "\" found on response\n");
    }

    Node getElement(Integer pos){
        int i=0;
        Node current = parent;
        while (current != null){
            if(pos == i){
                return current;
            }
            current = current.getNext();
            i++;
        }
        throw new NullPointerException("\nERROR: No position " + pos + " found on response\n");
    }

    public int getSize(){
        return size;
    }

    public boolean hasNext(){
        if(start) {
            current = parent;
            return current != null;
        }

        if(current != null) {
            return current.getNext() != null;
        }else{
            return false;
        }
    }

    public Node getNext(){

        if(start) {
            current = parent;
            start = false;
            if(current != null){
                return current;
            }
            else{
                throw new NullPointerException("ERROR: End of list");
            }
        }

        if(current != null){
            current = current.getNext();
        }else{
            throw new NullPointerException("ERROR: End of list");
        }

        return current;
    }

    public void add(Node nodo) throws ValidationException{
        if(parent == null){
            parent = nodo;
            nodo.setNext(null);
            last = nodo;
        }else{
            parent.setPrevious(nodo);
            nodo.setNext(parent);
            parent = nodo;
        }
        size++;
    }

}
