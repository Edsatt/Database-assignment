package edu.uob;

public enum ValueType {
    STRING,
    DOUBLE,
    BOOLEAN,
    NULL;

    public ValueType getValue(String value){
        switch(value.toLowerCase()){
            case "null" -> {
                return NULL;
            }
            case "true", "false" -> {
                return BOOLEAN;
            }
        }
        if(value.startsWith("'") && value.endsWith("'")) return STRING;
        return DOUBLE;
    }
}
