package student.server;

import student.adventure.GameEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GameAdventureService implements AdventureService{
    Map<Integer, GameEngine> allGames = new HashMap<>();
    int currentGameID = 0;

    @Override
    public void reset() {

    }

    @Override
    public int newGame() throws AdventureException, IOException {
//        GameEngine newGame = new GameEngine();
//        allGames.put(currentGameID, newGame);

        return currentGameID;
    }

    @Override
    public GameStatus getGame(int id) {
        return null;
    }

    @Override
    public boolean destroyGame(int id) {
        return false;
    }

    @Override
    public void executeCommand(int id, Command command) {

    }

    @Override
    public SortedMap<String, Integer> fetchLeaderboard() {
        return null;
    }
}
