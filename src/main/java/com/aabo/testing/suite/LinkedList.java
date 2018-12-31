package com.aabo.testing.suite;

import javax.xml.bind.ValidationException;

@SuppressWarnings("ALL")
public class LinkedList {

    private Node parent;
    private Node last;
    private Node current;
    private int size;
    private boolean end;

    LinkedList() {
        parent = null;
        size = 0;
        end = false;
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
            current = parent.getNext();
        }
        throw new NullPointerException("No existe ese valor");
    }

    Node getElement(Integer pos){
        int i=0;
        Node current = parent;
        while (current != null){
            if(pos == i){
                return current;
            }
            current = parent.getNext();
            i++;
        }
        throw new NullPointerException("No existe ese valor");
    }

    public int getSize(){
        return size;
    }

    public boolean hasNext(){
        if(current == null) {
            return current.getNext() != null;
        }else{
            return false;
        }
    }

    public Node getNext(){
        if(current != null){
            current = current.getNext();
        }else{
            current = parent;
        }
        if(current == null){
            throw new NullPointerException("End of list");
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
