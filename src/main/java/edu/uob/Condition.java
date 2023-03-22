package edu.uob;

public class Condition {

    Table table1;
    Table table2;
    String boolFunc;

    Table output;

    public Condition(Table table1, Table table2, String boolfunc){
        this.table1 = table1;
        this.table2 = table2;
        this.boolFunc = boolfunc;
    }


    public void boolSwitch(){
        switch(this.boolFunc.toLowerCase()){
            case "and" -> andCondition();
            case "or" -> orCondition();
        }
    }

    private void andCondition() {

    }

    private void orCondition() {
    }


}
