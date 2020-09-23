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
    private boolean didPlayerWin;

    public AdventureState(String map, int gameScore, String currentQuestion, String playerMessage, boolean didPlayerWin){
        this.map = map;
        this.gameScore = gameScore;
        this.currentQuestion = currentQuestion;
        this.playerMessage = playerMessage;
        this.didPlayerWin = didPlayerWin;
    }

    // used by AdventureServer
    public String getMap(){
        return map;
    }

    public int getGameScore(){
        return gameScore;
    }

    // used by AdventureServer
    public String getCurrentQuestion(){
        return currentQuestion;
    }

    // used by AdventureServer
    public String getPlayerMessage() {
        return playerMessage;
    }

    public boolean getDidPlayerWin() { return didPlayerWin; }
}
