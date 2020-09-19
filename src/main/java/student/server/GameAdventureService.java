package student.server;

import student.adventure.GameEngine;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GameAdventureService implements AdventureService{
    private final static String DATABASE_URL = "jdbc:sqlite:src/main/resources/adventure.db";
    private final Connection dbConnection;
    private Map<Integer, GameEngine> allGames;
    private int currentGameID = 0;

    public GameAdventureService() throws SQLException {
        dbConnection = DriverManager.getConnection(DATABASE_URL);
        currentGameID = 0;
        allGames = new HashMap<>();
    }

    @Override
    public void reset() {
        allGames = new HashMap<>();
        currentGameID = 0;
    }

    /**
     * Creates a new game instance then adds it to a HashMap for later use
     * @return int indicating ID for created game
     * @throws IOException thrown if specified json does not exist
     */
    @Override
    public int newGame() throws IOException {
        currentGameID++;
        GameEngine newGame = new GameEngine("src/main/resources/Rooms.json", "bob", currentGameID);
        allGames.put(currentGameID, newGame);
        System.out.println(currentGameID);
        System.out.println(allGames.size());
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
    public SortedMap<String, Integer> fetchLeaderboard() throws SQLException {
        Statement stmt = dbConnection.createStatement();
        ResultSet results;
        if (stmt.execute("SELECT * FROM leaderboard_albus2")) {
            results = stmt.getResultSet();
            System.out.println(results.first());
        } else {
            return null;
        }
        return null;
    }
}
