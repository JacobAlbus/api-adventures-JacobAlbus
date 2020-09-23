package student.server;

import student.adventure.GameEngine;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class GameAdventureService implements AdventureService{
    private final static String DATABASE_URL = "jdbc:sqlite:src/main/resources/adventure.db";
    private final Connection dbConnection = DriverManager.getConnection(DATABASE_URL);
    private Map<Integer, GameEngine> allGames;
    private int currentGameID = 0;

    public GameAdventureService() throws SQLException {
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
    public int newGame() throws IOException, SQLException {
        currentGameID++;
        GameEngine newGame = new GameEngine("src/main/resources/Rooms.json",
                                             "bob",
                                                    currentGameID,
                                              true);
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
    public void executeCommand(int id, Command command) throws SQLException {
        String action = command.getCommandName();
        String noun = command.getCommandValue();
        allGames.get(id).processInputs(action, noun);

        GameStatus updatedStatus = getGame(id);
        boolean didPlayerWin = updatedStatus.getState().getDidPlayerWin();

        if(didPlayerWin){
            String playerName = command.getPlayerName();
            int playerScore = updatedStatus.getState().getGameScore();
            addPlayerToLeaderboard(playerName, playerScore);
        }
    }

    /**
     * Adds player and their score to the leaderboard if they win
     * @param playerName String name of player
     * @param playerScore Int score of their game
     * @throws SQLException Required for Statement object
     */
    private void addPlayerToLeaderboard(String playerName, int playerScore) throws SQLException {
        Statement stmt = dbConnection.createStatement();
        String add = String.format("INSERT INTO leaderboard_albus2 VALUES (10, '%s', %d)", playerName, playerScore);
        stmt.execute(add);
    }



    @Override
    public SortedMap<String, Integer> fetchLeaderboard() throws SQLException {
        Statement stmt = dbConnection.createStatement();
        SortedMap<String, Integer> leaderboard = new TreeMap();

        if(stmt.execute("SELECT name, score FROM leaderboard_albus2")) {
            ResultSet results = stmt.getResultSet();
            while (results.next()) {
                String name = results.getString("name");
                int score = results.getInt("score");
                leaderboard.put(name, score);
            }
        } else {
            return null;
        }
        return leaderboard;
    }
}
