package student.server;

import student.adventure.GameEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GameAdventureService implements AdventureService{
    private Map<Integer, GameEngine> allGames = new HashMap<>();
    private int currentGameID = 0;

    @Override
    public void reset() {
        allGames = new HashMap<>();
        currentGameID = 0;
    }

    @Override
    public int newGame() throws AdventureException, IOException {
        currentGameID++;
        GameEngine newGame = new GameEngine("src/main/resources/Rooms.json", "bob");
        allGames.put(currentGameID, newGame);

        return currentGameID;
    }

    @Override
    public GameStatus getGame(int id) {
        try{
            GameEngine engine = allGames.get(id);
            return engine.getStatus();
        } catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public boolean destroyGame(int id) {
        try{
            allGames.remove(id);
            return true;
        } catch(NullPointerException e){
            return false;
        }
    }

    @Override
    public void executeCommand(int id, Command command) {
        String action = command.getCommandName();
        String noun = command.getCommandValue();
        allGames.get(id).processInputs(action, noun);
    }

    @Override
    public SortedMap<String, Integer> fetchLeaderboard() {
        return null;
    }
}
