package com.aabo.testing.suite;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestSuite{

    private LinkedList lista;

    public enum commons{
        ANYTHING,
        NULL,
        ANYTHING_NOT_NULL,
        BLANK
    }

    public TestSuite(){
        lista = new LinkedList();
    }

    public TestSuite parseObject(String key, Object val) throws ValidationException{
        getInstance(key, val, lista);
        return this;
    }

    private void getInstance(String key, Object val, LinkedList lista) throws ValidationException {
        if (val instanceof Map<?,?>){
            //noinspection unchecked
            lista.append(new Node<>(key, iterateMap((Map<String, Object>) val), Node.Type.MAP));
        }else if (val instanceof Number){
            lista.append(new Node<>(key, (Number) val, Node.Type.NATIVE));
        }else if (val instanceof String){
            lista.append(new Node<>(key, (String) val, Node.Type.NATIVE));
        }else if (val instanceof Boolean){
            lista.append(new Node<>(key, (Boolean) val, Node.Type.NATIVE));
        }else if (val instanceof List){
            //noinspection unchecked
            lista.append(new Node<>(iterateList((List<Object>) val), Node.Type.LIST));
        }else if(val instanceof commons){
            lista.append(new Node<>(key, (commons) val, Node.Type.COMMONS));
        }else{
            throw new ValidationException("EL formato de "+val+" no es soportado");
        }
    }

    public TestSuite withString(String key, String val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.NATIVE));
        return this;
    }

    public TestSuite withString(String key, commons val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.COMMONS));
        return this;
    }

    public TestSuite withBoolean(String key, boolean val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.NATIVE));
        return this;
    }

    public TestSuite withBoolean(String key, commons val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.COMMONS));
        return this;
    }

    public TestSuite withMap(String key, Map<String, Object> val) throws ValidationException {
        lista.append(new Node<>(key, iterateMap(val), Node.Type.MAP));
        return this;
    }

    private LinkedList iterateList(List<Object> mapa){

        LinkedList total = new LinkedList();

        mapa.forEach((v) -> {
            try {
                if (v instanceof Map<?,?>){
                    //noinspection unchecked
                    total.append(new Node<>(iterateMap((Map<String, Object>) v), Node.Type.MAP));
                }else if (v instanceof Number){
                    total.append(new Node<>((Number)v, Node.Type.NATIVE));
                }else if (v instanceof String){
                    total.append(new Node<>((String)v, Node.Type.NATIVE));
                }else if (v instanceof Boolean){
                    lista.append(new Node<>((Boolean) v, Node.Type.NATIVE));
                }else if (v instanceof List){
                    //noinspection unchecked
                    total.append(new Node<>(iterateList((List<Object>) v), Node.Type.LIST));
                }else if(v instanceof commons){
                    total.append(new Node<>((commons)v, Node.Type.COMMONS));
                }else{
                    throw new ValidationException("EL formato de "+v+" no es soportado");
                }

            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        return total;

    }

    private LinkedList iterateMap(Map<String, Object> mapa){

        LinkedList total = new LinkedList();

        mapa.forEach((k,v) -> {
            try {
                getInstance(k, v, total);
            } catch (ValidationException e) {
                e.printStackTrace();
                assert(false);
            }
        });
        return total;
    }

    public TestSuite withMap(String key, commons val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.COMMONS));
        return this;
    }

    public TestSuite withNumber(String key, Number val) throws ValidationException{
        lista.append(new Node<>(key, val, Node.Type.NATIVE));
        return this;
    }

    public TestSuite withNumber(String key, commons val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.COMMONS));
        return this;
    }

    public TestSuite withList(String key, List<Object> val) throws ValidationException{
        lista.append(new Node<>(key, iterateList(val), Node.Type.LIST));
        return this;
    }

    public TestSuite withList(String key, commons val) throws ValidationException {
        lista.append(new Node<>(key, val, Node.Type.COMMONS));
        return this;
    }

    public TestSuite withNull(String key) throws ValidationException{
        lista.append(new Node<>(key, commons.NULL, Node.Type.COMMONS));
        return this;
    }

    public LinkedList getStructure() {
        return lista;
    }

    public boolean validateResponse(String json) throws IOException, ValidationException {

        StringConverter converter = new StringConverter();
        LinkedList object = converter.parser(json).getStructure();

        while (object.hasNext()){
            Node val = object.getNext();
        }

        return true;
    }

}
