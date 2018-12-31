package com.aabo.testing.suite;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Stack;

public class StringConverter {

    public enum Type{
        ARRAY,
        MAP,
    }

    private TestSuite object;
    private Boolean begining;
    private Stack<String> keys;
    private Stack<TestMap<String, Object>> maps;
    private Stack<TestList<Object>> arrays;
    private Stack<Type> scope;

    public StringConverter() {
        this.object = new TestSuite();
        this.begining = true;
        this.keys = new Stack<>();
        this.maps = new Stack<>();
        this.arrays = new Stack<>();
        this.scope = new Stack<>();
    }

    public TestSuite parser(String JSON) throws IOException, ValidationException {

        JsonFactory factory = new JsonFactory();

        JsonParser parser  = factory.createParser(JSON);
        JsonToken jsonToken = parser.nextToken();

        while(!parser.isClosed()){

            System.out.println(jsonToken);

            switch(jsonToken){
                case FIELD_NAME:
                    keys.push(parser.getCurrentName());
                    break;
                case START_OBJECT:
                    if(begining){
                        begining = false;
                    }else{
                        scope.add(Type.MAP);
                        maps.push(new TestMap<>());
                    }
                    break;
                case END_ARRAY:
                    TestList<Object> currentList = arrays.pop();
                    scope.pop();
                    if(scope.isEmpty()){
                        //noinspection unchecked
                        object.withList(keys.pop(), currentList);
                    }else{
                        switch(scope.peek()) {
                            case MAP:
                                maps.peek().put(keys.pop(), currentList);
                                break;
                            case ARRAY:
                                arrays.peek().put(currentList);
                                break;
                        }
                    }
                    break;
                case END_OBJECT:
                    if(keys.empty()){
                        break;
                    }
                    TestMap<String, Object> currentMap = maps.pop();
                    scope.pop();
                    if(scope.isEmpty()){
                        //noinspection unchecked
                        object.withMap(keys.pop(), currentMap);
                    }else{
                        switch(scope.peek()) {
                            case MAP:
                                maps.peek().put(keys.pop(), currentMap);
                                break;
                            case ARRAY:
                                arrays.peek().put(currentMap);
                                break;
                        }
                    }
                    break;
                case START_ARRAY:
                    scope.add(Type.ARRAY);
                    arrays.push(new TestList<>());
                    break;
                default:
                    returnValue(parser);
            }

            jsonToken = parser.nextToken();
        }
        return object;
    }

    private void returnValue(JsonParser parser) throws ValidationException, IOException {
        JsonToken jsonToken = parser.currentToken();
        switch (jsonToken){
            case VALUE_NULL:
                if(scope.isEmpty()){
                    object.withNull(keys.pop());
                }else{
                    switch(scope.peek()) {
                        case MAP:
                            maps.peek().put(keys.pop(), null);
                            break;
                        case ARRAY:
                            arrays.peek().put(null);
                            break;
                    }
                }
                break;
            case VALUE_NUMBER_FLOAT:
                if(scope.isEmpty()){
                    object.withNumber(keys.pop(), parser.getDoubleValue());
                }else{
                    switch(scope.peek()) {
                        case MAP:
                            maps.peek().put(keys.pop(), parser.getDoubleValue());
                            break;
                        case ARRAY:
                            arrays.peek().put(parser.getDoubleValue());
                            break;
                    }
                }
                break;
            case VALUE_FALSE:
                if(scope.isEmpty()){
                    object.withBoolean(keys.pop(), false);
                }else{
                    switch(scope.peek()) {
                        case MAP:
                            maps.peek().put(keys.pop(), false);
                            break;
                        case ARRAY:
                            arrays.peek().put(false);
                            break;
                    }
                }
                break;
            case VALUE_NUMBER_INT:
                if(scope.isEmpty()){
                    object.withNumber(keys.pop(), parser.getIntValue());
                }else{
                    switch(scope.peek()) {
                        case MAP:
                            maps.peek().put(keys.pop(), parser.getIntValue());
                            break;
                        case ARRAY:
                            arrays.peek().put(parser.getIntValue());
                            break;
                    }
                }
                break;
            case VALUE_TRUE:
                if(scope.isEmpty()){
                    object.withBoolean(keys.pop(), true);
                }else{
                    switch(scope.peek()) {
                        case MAP:
                            maps.peek().put(keys.pop(), true);
                            break;
                        case ARRAY:
                            arrays.peek().put(true);
                            break;
                    }
                }
                break;
            case VALUE_STRING:
                String value = parser.getValueAsString();
                if(value.isEmpty()){
                    if(scope.isEmpty()){
                        object.withString(keys.pop(), TestSuite.commons.BLANK);
                    }else{
                        switch(scope.peek()) {
                            case MAP:
                                maps.peek().put(keys.pop(), TestSuite.commons.BLANK);
                                break;
                            case ARRAY:
                                arrays.peek().put(TestSuite.commons.BLANK);
                                break;
                        }
                    }
                }else{
                    if(scope.isEmpty()){
                        object.withString(keys.pop(), value);
                    }else{
                        switch(scope.peek()) {
                            case MAP:
                                maps.peek().put(keys.pop(), value);
                                break;
                            case ARRAY:
                                arrays.peek().put(value);
                                break;
                        }
                    }
                }
                break;
            default:
                throw new ValidationException("EL formato de "+jsonToken+" no es soportado");
        }
    }

}
