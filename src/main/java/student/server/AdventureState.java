package student.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A class to represent values in a game state.
 *
 * Note: these fields should be JSON-serializable values, like Strings, ints, floats, doubles, etc.
 * Please don't nest objects, as the frontend won't know how to display them.
 *
 * Good example:
 * private String shoppingList;
 *
 * Bad example:
 * private ShoppingList shoppingList;
 */
@JsonSerialize
public class AdventureState {
    private String map;
    private int gameScore;
    private String currentQuestion;
    private String playerMessage;

    public AdventureState(String map, int gameScore, String currentQuestion, String playerMessage){
        this.map = map;
        this.gameScore = gameScore;
        this.currentQuestion = currentQuestion;
        this.playerMessage = playerMessage;
    }

    public String getMap(){
        return map;
    }

    public int getGameScore(){
        return gameScore;
    }

    public String getCurrentQuestion(){
        return currentQuestion;
    }

    public String getPlayerMessage() {
        return playerMessage;
    }
}
