package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Tokeniser {
    private final String[] specialCharacters = {"(",")",",",";","=",">","<","!","+","-","."};
    private final ArrayList<String> tokenStrings = new ArrayList<>();
    private final ArrayList<Token> tokens;

    public Tokeniser(String query){
        this.tokens = new ArrayList<>();
        setup(query);
    }

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
        storeTokens();
        //for (Token token : tokens) printToken(token);
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

    public void storeTokens() {
        for (String tokenString : tokenStrings) tokens.add(new Token(tokenString));
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
}
