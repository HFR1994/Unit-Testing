package com.aabo.testing.suite;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Type getType() {
        return type;
    }

    private String getTypeString() {
        return type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object equals) {
        if(equals instanceof Node<?>) {
            if (type == Type.MAP) {
                if (((Node) equals).getType() == Type.MAP) {
                    return validateMap((LinkedList) ((Node<T>) equals).getPayload(), (LinkedList) payload);
                } else {
                    System.err.println("\nERROR: Incorrect value type response for key \"" + ((Node) equals).getIdentifier() + "\" found " + ((Node) equals).getTypeString() + " expected Map\n");
                }
            } else if (type == Type.LIST) {
                if (((Node) equals).getType() == Type.LIST) {
                    return validateList(identifier,((LinkedList) ((Node<T>) equals).getPayload()), ((LinkedList) payload));
                } else {
                    System.err.println("\nERROR: Incorrect value type response for key \"" + ((Node) equals).getIdentifier() + "\" found " + ((Node) equals).getTypeString() + " expected List\n");
                }
            } else {
                Node<T> current = ((Node<T>) equals);
                return this.hashCode() == current.hashCode();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, payload, type);
    }

    private boolean validateList(String key, LinkedList compare, LinkedList current) {
        Object v;
        int i=0;
        while (current.hasNext()) {
            try {
                Node entry = current.getNext();
                v = entry.getPayload();
                if (v instanceof Map<?, ?>) {
                    Node next = compare.getElement(i);
                    if(next.getType() == Type.MAP){
                        if(!validateMap((LinkedList) next.getPayload(), ((LinkedList) v))){
                            return false;
                        }
                    }else{
                        throw new ValidationException("\nERROR: Incorrect value type for key \"" + key + "\" found " + next.getTypeString() + " expected Map");
                    }
                } else if (v instanceof List) {
                    Node next = compare.getElement(i);
                    if(next.getType() == Type.LIST){
                        if(!validateList(key, (LinkedList) next.getPayload(), ((LinkedList) v))){
                            return false;
                        }
                    }else{
                        throw new ValidationException("\nERROR: Incorrect value type for key \"" + key + "\" found " + next.getTypeString() + " expected List");
                    }
                } else if (v instanceof TestSuite.commons) {
                    TestSuite.commons valor = (TestSuite.commons) v;
                    Object actual = compare.getElement(key).getPayload();
                    switch (valor) {
                        case NULL:
                            if (actual != null)
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position "+i+" expected null got " + actual.toString() + "\n");
                            break;
                        case BLANK:
                            if (!((String) compare.getElement(key).getPayload()).isEmpty())
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position "+i+" expected empty got " + actual.toString() + "\n");
                            break;
                        case ANYTHING_NOT_NULL:
                            if (actual == null)
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position "+i+" expected anything except null got null\n");
                            break;
                    }
                } else if (v instanceof String) {
                    if (!compare.getElement(key).equals(payload)) {
                        Node value = compare.getElement(key);
                        if(value.getPayload() instanceof LinkedList) {
                            throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position " + i + " expected " + value.getTypeString() + " got \"" + v.toString() + "\"\n");
                        }else{
                            throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position " + i + " expected " + compare.getElement(key).getPayload().toString() + " got \"" + v.toString() + "\"\n");
                        }
                    }
                } else {
                    if (compare.getElement(key) != payload) {
                        Node value = compare.getElement(key);
                        if (value.getPayload() instanceof LinkedList) {
                            throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position " + i + " expected " + value.getTypeString() + " got \"" + v.toString() + "\"\n");
                        } else {
                            throw new ValidationException("\nERROR: Incorrect value at key \"" + key + "\" in position " + i + " expected " + compare.getElement(key).getPayload().toString() + " got \"" + v.toString() + "\"\n");
                        }
                    }
                }
            } catch(ValidationException e) {
                System.err.println(e.getMessage());
                return false;
            } catch (NullPointerException e){
                System.err.println("\nERROR: No position "+i+" found on key " + key + " in response");
                return false;
            } catch(Exception e) {
                System.err.println("\nERROR: Incorrect value type response for key " + key + " in position " + i);
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean validateMap(LinkedList compare, LinkedList current) {
        String k="";
        Object v;
        while (current.hasNext()) {
            try {
                Node<?> entry = current.getNext();
                k = entry.getIdentifier();
                v = entry.getPayload();
                if (entry.getType() == Type.MAP) {
                    Node next = compare.getElement(k);
                    if(next.getType() == Type.MAP){
                        if(!validateMap((LinkedList) next.getPayload(), ((LinkedList) v))){
                            return false;
                        }
                    }else{
                        throw new ValidationException("\nERROR: Incorrect value type for key \"" + k + "\" found " + next.getTypeString() + " expected Map");
                    }
                } else if (entry.getType() == Type.LIST) {
                    Node next = compare.getElement(k);
                    if(next.getType() == Type.LIST){
                        if(!validateList(k, (LinkedList) next.getPayload(), ((LinkedList) v))){
                            return false;
                        }
                    }else{
                        throw new ValidationException("\nERROR: Incorrect value type for key \"" + k + "\" found " + next.getTypeString() + " expected List");
                    }
                } else if (v instanceof TestSuite.commons) {
                    TestSuite.commons valor = (TestSuite.commons) v;
                    Object actual = compare.getElement(k).getPayload();
                    switch (valor) {
                        case NULL:
                            if (actual != null)
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + k + "\" expected null got " + actual.toString());
                            break;
                        case BLANK:
                            if (!((String) compare.getElement(k).getPayload()).isEmpty())
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + k + "\" expected empty got " + actual.toString());
                            break;
                        case ANYTHING_NOT_NULL:
                            if (actual == null)
                                throw new ValidationException("\nERROR: Incorrect value at key \"" + k + "\" expected anything except null got null");
                            break;
                    }
                } else if (v instanceof String) {
                    if (!compare.getElement(k).equals(payload))
                        throw new ValidationException("\nERROR: Incorrect value at key \"" + k + "\" expected "+compare.getElement(k).getPayload().toString()+" got " + v.toString());
                } else {
                    if (compare.getElement(k) != payload)
                        throw new ValidationException("\nERROR: Incorrect value at key \"" + k + "\" expected "+compare.getElement(k).getPayload().toString()+" got " + v.toString());
                }
            } catch(ValidationException e) {
                System.err.println(e.getMessage()+"\n");
                return false;
            } catch (NullPointerException e){
                System.err.println("\nERROR: No key \"" + k + "\" found on validation\n");
                return false;
            } catch(Exception e) {
                System.err.println("\nERROR: Incorrect value type for key \"" + k + "\"\n");
                return false;
            }
        }
        return true;
    }
}
