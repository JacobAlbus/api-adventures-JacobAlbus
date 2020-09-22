package student.adventure;

import com.google.gson.Gson;
import student.server.AdventureState;
import student.server.GameStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class GameEngine {
    private boolean isPlayerInWinRoom = false;
    private int gameID;
    private String playerMessage;
    private int gameScore = 1000000;
    private boolean useUI;
    private GameBoard board;
    private Player player;
    private Room room;
    private GameStatus status;
    private GameQuestions questions;
    private final List<String> actions = new ArrayList<>(Arrays.asList("use", "take", "drop", "go", "check",
                                                                       "examine", "help", "map", "exit", "quit"));
    private final Scanner userInput = new Scanner(System.in);

    /**
     * Initializes Player and GameBoard object
     */
    public GameEngine(String filePath, String name, int gameID, boolean useUI) throws IOException {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Reader questionsReader = Files.newBufferedReader(
                    Paths.get("src/main/resources/GameQuestions.json"));

            questions = gson.fromJson(questionsReader, GameQuestions.class);
            board = gson.fromJson(reader, GameBoard.class);
            reader.close();
            // TODO ask elizabeth why we don't want to throw an exception
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }

        this.gameID = gameID;
        this.useUI = useUI;
        player = new Player(filePath, name);
        room = board.findPlayerCurrentRoom(player);
        updateGameStatus();
    }

    public GameStatus getStatus(){
        return status;
    }

    /**
     * Contains main loop for game: explore a mysterious dungeon and solve puzzles to escape!
     */
    public void playGame(){
        room.printRoomMessage();

        while(!isPlayerInWinRoom){
            printInputPrompt();
            List<String>  inputs = filterInputs();
            String  action = inputs.get(0);
            String  noun = inputs.get(1);

            if(action.equals("exit") || action.equals("quit")){
                break;
            }

            processInputs(action, noun);
        }
    }

    /**
     * Prompts user for input and filters it to match the form 'action' + 'noun'
     * @return an arrayList containing an 'action' command (use, go, examine, take, check etc.)
     *         and a 'noun' (usually an item or direction)
     */
    public ArrayList<String> filterInputs(){
        ArrayList<String> filteredInputs = new ArrayList<>();
        String[] playerInputs = userInput.nextLine().toLowerCase().split("\\s+");
        int inputWordIndex = 0;

        String action = playerInputs[inputWordIndex];
        inputWordIndex++;
        if(action.equals("") && playerInputs.length > 1){
            action = playerInputs[inputWordIndex];
            inputWordIndex++;
        }

        StringBuilder noun = new StringBuilder();
        for(int index = inputWordIndex; index < playerInputs.length; index++){
            noun.append(playerInputs[index]);
            if(index != playerInputs.length - 1){
                noun.append(" ");
            }
        }

        Collections.addAll(filteredInputs, action, noun.toString());

        return filteredInputs;
    }

    /**
     * Given valid inputs, executes method corresponding to some action
     * @param action command for the player (use, go, examine, take, check etc.)
     * @param noun object or thing the player interacts with (usually an item or direction)
     */
    public void processInputs(String action, String noun){
            // TODO ask elizabeth about magic strings
            switch(action) {
                case "examine":
                    deductGameScore();
                    room.printRoomMessage();
                    break;
                case "take":
                    deductGameScore();
                    player.takeItem(room, noun);
                    player.printInventory();
                    break;
                case "drop":
                    deductGameScore();
                    playerMessage = player.dropItem(room, noun);
                    break;
                case "use":
                    deductGameScore();
                    playerMessage = player.useItem(room, noun, useUI);
                    break;
                case "go":
                    deductGameScore();
                    int[] updatedRoomCoords = player.updatePosition(room, noun);
                    checkPlayerInWinRoom(updatedRoomCoords);

                    if(!Arrays.equals(room.getRoomCoordinates(), updatedRoomCoords) && !isPlayerInWinRoom) {
                        room = board.findPlayerCurrentRoom(player);
                        room.printRoomMessage();
                    }

                    break;
                case "answer":
                    if(player.isTesting()){
                        playerMessage = questions.askMathQuestionUI(room, player, noun);
                    } else {
                        System.out.println("I couldn't understand that command. Input 'help' to see list of commands");
                    }
                    break;
                case "check":
                    deductGameScore();
                    player.printInventory();
                    break;
                case "map":
                    deductGameScore();
                    System.out.println(createPrintedMap());
                    break;
                case "help":
                    deductGameScore();
                    printHelpCommands();
                    break;
                default:
                    System.out.println("I couldn't understand that command. Input 'help' to see list of commands");
            }
            //TODO is this breaking encapsulation?
            room = board.findPlayerCurrentRoom(player);
            updateGameStatus();
    }

    /**
     * Prints out a map of all rooms player has been in
     */
    public String createPrintedMap(){
        char[][] mapOfRoomsVisited = createMapArray();

        // Converts 2d String array into a single String
        // ask elizabeth about rows then columns comment
        StringBuilder printedMap = new StringBuilder();
        for(int columnIndex = mapOfRoomsVisited[0].length - 1; columnIndex >= 0; columnIndex--){
            for(int rowIndex = 0; rowIndex < mapOfRoomsVisited.length; rowIndex++){
                printedMap.append(mapOfRoomsVisited[rowIndex][columnIndex]);
            }
            printedMap.append("\r\n");
        }

        return printedMap.toString();
    }

    /**
     * Prints a list of commands recognized by student.adventure.GameEngine
     */
    public void printHelpCommands(){
        System.out.println("Input go + 'direction' (east, west, north, south) to through corresponding direction \n" +
                "Input take + 'item' to grab item from room \n" +
                "Input use + 'item' to use item in inventory \n" +
                "Input drop + 'item' to drop item from inventory and leave it in room \n" +
                "Input examine to see room information \n" +
                "Input check to see all items currently in inventory \n" +
                "Input exit or quit to stop playing Adventure");
    }

    /**
     * Checks if player's updated Room is win room
     * @param updatedRoomCoords coords of player's room after attempting to "go" between rooms
     */
    private void checkPlayerInWinRoom(int[] updatedRoomCoords){
        int winRoomIndex = 10;
        int[] winRoom = board.getRoom(winRoomIndex).getRoomCoordinates();
        if(Arrays.equals(updatedRoomCoords, winRoom)) {
            room = board.findPlayerCurrentRoom(player);
            room.setPrimaryDescription(room.getSecondaryDescription());
            room.printRoomMessage();
            isPlayerInWinRoom = true;
        }
    }

    /**
     * Updates contents of GameStatus to match with player's current room
     */
    private void updateGameStatus(){
        boolean error = false;
        int id = gameID;
        String message = room.getPrimaryDescription();
        String imageUrl = room.getImageUrl();
        String videoUrl = room.getVideoUrl();
        AdventureState state = new AdventureState(createPrintedMap(), gameScore, "", playerMessage);
        HashMap<String, List<String>> commandOptions = new HashMap();

        if(player.isTesting()){
            int index = questions.getCurrentQuestionIndex();
            Question currentQuestion = questions.getGameQuestions(index);

            ArrayList<String> playerAnswers = currentQuestion.getPlayerAnswers();
            commandOptions.put("answer", playerAnswers);
            state = new AdventureState(createPrintedMap(), gameScore, currentQuestion.getQuestion(), playerMessage);
        } else {
            commandOptions.put("go", room.getAvailableDoors());
            if(room.getAvailableItems().size() != 0){
                commandOptions.put("take", room.getAvailableItems());
            }
            if(player.getItems().size() != 0){
                commandOptions.put("use", player.getItems());
                commandOptions.put("drop", player.getItems());
            }
        }

        status = new GameStatus(error, id, message, imageUrl, videoUrl, state, commandOptions);
    }

    private void deductGameScore(){
        int scoreDeduction = 10000;
        gameScore -= scoreDeduction;
    }

    /**
     * Gets dimensions of map based on which rooms player has been to
     * @return int array containing vertical and horizontal map dimensions
     */
    private int[] findMapDimensions(){
        int mapSizeX = 0;
        int mapSizeY = 0;

        // Get dimensions of map size based on which rooms player has been to
        for(int roomIndex = 0; roomIndex < board.getBoardSize(); roomIndex++) {
            Room room = board.getRoom(roomIndex);

            if(room.hasPlayerBeenHere()) {
                int[] roomCoordinates = room.getRoomCoordinates();
                if(roomCoordinates[0] > mapSizeX) {
                    mapSizeX = roomCoordinates[0];
                }
                if(roomCoordinates[1] > mapSizeY) {
                    mapSizeY = roomCoordinates[1];
                }
            }
        }

        return new int[] {mapSizeX, mapSizeY};
    }

    /**
     * Creates a map array based on the rooms player has been to
     * @return 2d String array containing a map of all the rooms player has been to
     */
    private char[][] createMapArray(){
        int[] mapDimensions = findMapDimensions();
        char[][] mapArray = new char[mapDimensions[0]][mapDimensions[1]];
        char ROOM_MARKER = '1';
        char NO_ROOM_MARKER = '0';

        for(char[] mapRow : mapArray){
            Arrays.fill(mapRow, NO_ROOM_MARKER);
        }

        for(int roomIndex = 0; roomIndex < board.getBoardSize(); roomIndex++) {
            Room room = board.getRoom(roomIndex);

            if(room.hasPlayerBeenHere()) {
                int x = room.getRoomCoordinates()[0] - 1;
                int y = room.getRoomCoordinates()[1] - 1;
                mapArray[x][y] = ROOM_MARKER;
            }
        }

        return mapArray;
    }

    private void printInputPrompt(){
        System.out.print("> ");
    }
}