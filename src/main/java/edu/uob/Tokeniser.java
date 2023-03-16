package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Tokeniser {
    private String[] specialCharacters = {"(",")",",",";","=",">","<","!","+","-"};
    private ArrayList<String> tokenStrings = new ArrayList<>();
    private ArrayList<Token> tokens = new ArrayList<>();

    public void setup(String query){
        query = query.trim();
        String[] fragments = query.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) tokenStrings.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokenStrings = tokenise(fragments[i]);
                tokenStrings.addAll(Arrays.asList(nextBatchOfTokenStrings));
            }
        }
        tokenCheck();
        for (String token : tokenStrings) storeToken(token);
        //for (Token token : tokens) printToken(token);
        parse();
    }

    public String[] tokenise(String input){
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split(" ");
    }

    public void tokenCheck(){
        for(int i=0; i<tokenStrings.size()-1; i++){
            switch (tokenStrings.get(i)) {
                case ">" -> handleGreaterThan(i);
                case "<" -> handleLessThan(i);
                case "=" -> handleEqual(i);
                case "!" -> handleNotEqual(i);
            }
        }
    }

    public void handleGreaterThan(int index){
        if(Objects.equals(tokenStrings.get(index+1), "=")){
            tokenStrings.set(index, ">=");
            tokenStrings.remove(index+1);
        }
    }

    public void handleLessThan(int index){
        if(Objects.equals(tokenStrings.get(index+1), "=")){
            tokenStrings.set(index, "<=");
            tokenStrings.remove(index+1);
        }
    }

    public void handleEqual(int index){
        if(Objects.equals(tokenStrings.get(index+1), "=")){
            tokenStrings.set(index, "==");
            tokenStrings.remove(index+1);
        }
    }

    public void handleNotEqual(int index){
        if(Objects.equals(tokenStrings.get(index+1), "=")){
            tokenStrings.set(index, "!=");
            tokenStrings.remove(index+1);
        }
    }

    public void storeToken(String tokenString){
        tokens.add(new Token(tokenString));
    }

    public void printToken(Token token){
        System.out.println("Value = " +token.getValue() +" Type = " +token.getType());
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public Token getToken(int index){
        return tokens.get(index);
    }

    public int getNumTokens(){
        return tokens.size();
    }

    public void parse(){
        Parser parser = new Parser(tokens);
    }
}
