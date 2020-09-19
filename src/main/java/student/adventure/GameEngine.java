package student.adventure;

import com.google.gson.Gson;
import student.server.AdventureState;
import student.server.GameStatus;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameEngine {
    private GameBoard board;
    private Player player;
    private Room room;
    private GameStatus status;
    private final List<String> actions = new ArrayList<>(Arrays.asList("use", "take", "drop", "go", "check",
                                                                       "examine", "help", "map", "exit", "quit"));
    private final Scanner userInput = new Scanner(System.in);

    /**
     * Initializes Player and GameBoard object
     */
    public GameEngine(String filePath, String name) throws IOException {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));

            board = gson.fromJson(reader, GameBoard.class);
            reader.close();
            // ask elizabeth why we don't want to throw an exception
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }

        player = new Player(filePath, name);
        room = board.findPlayerCurrentRoom(player);
    }

    /**
     * Contains main loop for game: explore a mysterious dungeon and solve puzzles to escape!
     */
    public void playGame(){
        room.printRoomMessage();

        while(true){
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

        if(actions.contains(action)){
            Collections.addAll(filteredInputs, action, noun.toString());
        } else{
            return null;
        }

        return filteredInputs;
    }

    /**
     * Given valid inputs, executes method corresponding to some action
     * @param action command for the player (use, go, examine, take, check etc.)
     * @param noun object or thing the player interacts with (usually an item or direction)
     */
    public void processInputs(String action, String noun){
        // is this breaking encapsulation?
        room = board.findPlayerCurrentRoom(player);
        updateGameStatus();
        // ask elizabeth about magic strings
        switch(action){
            case "examine":
                room.printRoomMessage();
                break;
            case "take":
                player.takeItem(room, noun);
                player.printInventory();
                break;
            case "drop":
                player.dropItem(room, noun);
                break;
            case "use":
                player.useItem(room, noun);
                break;
            case "go":
                int winRoomIndex = 10;
                int[] winRoom = board.getRoom(winRoomIndex).getRoomCoordinates();
                Room updatedRoom = player.updatePosition(room, noun);

                if(!Arrays.equals(room.getRoomCoordinates(), updatedRoom.getRoomCoordinates())){
                    System.out.println(Arrays.toString(updatedRoom.getRoomCoordinates()));
                }
                // should this be own method?
                if(Arrays.equals(updatedRoom.getRoomCoordinates(), winRoom)){
                    System.out.println("You win! Play again to venture back into your dorm");
                    System.exit(0);
                }
                break;
            case "check":
                player.printInventory();
                break;
            case "map":
                printOutMap();
                break;
            case "help":
                printHelpCommands();
                break;
            default:
                System.out.println("I couldn't understand that command. Input 'help' to see list of commands");
        }
    }

    /**
     * Prints out a map of all rooms player has been in
     */
    public void printOutMap(){
        char[][] mapOfRoomsVisited = createMapArray();

        // Converts 2d String array into a single String
        // ask elizabeth about rows then columns comment
        StringBuilder printedMap = new StringBuilder();
        for(int columnIndex = mapOfRoomsVisited[0].length - 1; columnIndex >= 0; columnIndex--){
            for(int rowIndex = 0; rowIndex < mapOfRoomsVisited.length; rowIndex++){
                printedMap.append(mapOfRoomsVisited[rowIndex][columnIndex]);
            }
            printedMap.append("\n");
        }

        System.out.println(printedMap);
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

    public GameStatus getStatus(){
        return status;
    }

    /**
     * Updates contents of GameStatus to match with player's current room
     */
    private void updateGameStatus(){
        boolean error = false;
        int id = 1;
        String message = room.getPrimaryDescription();
        String imageUrl = room.getImageUrl();
        String videoUrl = room.getVideoUrl();
        AdventureState state = new AdventureState();
        HashMap<String, List<String>> commandOptions = new HashMap<String, List<String>>() {{
            put("go", room.getAvailableDoors());
            put("take", room.getAvailableItems());
            put("use", player.getItems());
            put("drop", player.getItems());
            put("examine", null);
            put("check", null);
            put("help", null);
            put("map", null);
        }};

        status = new GameStatus(error, id, message, imageUrl, videoUrl, state, commandOptions);
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