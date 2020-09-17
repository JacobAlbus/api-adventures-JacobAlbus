package student.adventure;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class GameEngine {
    // Made protected instead of static for testing purposes
    protected GameBoard board;
    protected Player player;
    private static Scanner gameMaster = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        // Scanner prompt for name moved to main method for testing purposes
        System.out.println("Hello player! What would you like to name your adventurer?");
        printInputPrompt();
        String name = gameMaster.nextLine();
        System.out.println(".\n" + ".\n" + ".\n.");

        GameEngine engine = new GameEngine("src/main/resources/Rooms.json", name);
        engine.gameLoop();
    }

    /**
     * Initializes Player and GameBoard object
     */
    public GameEngine(String filePath, String name) throws IOException {
        player = new Player(name);

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));

            board = gson.fromJson(reader, GameBoard.class);
            reader.close();
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }
    }

    /**
     * Contains main loop for game: prompts player for input then filters and
     * processes input to figure out what player wants to do
     */
    public void gameLoop(){
        int[] winRoom = board.getRoom(10).getRoomCoordinates();
        
        Room room = board.findPlayerCurrentRoom(player);
        room.printRoomMessage();
        while(true){
            room = board.findPlayerCurrentRoom(player);
            // Message for when player enters the final/winners Room
            if(Arrays.equals(room.getRoomCoordinates(), winRoom)){
                System.out.println("You win! Play again to venture back into your dorm");
                break;
            }

            ArrayList<String> inputs = filterInputs();
            String action = inputs.get(0);
            String noun = inputs.get(1);

            if(action.equals("exit") || action.equals("quit")){
                break;
            }

            processInputs(room, action, noun);
        }
    }

    /**
     * Prompts user for input and filters it to match the form 'action' + 'noun'
     * @return an arrayList containing an 'action' command (use, go, examine, take, check etc.)
     *         and a 'noun' (usually an item or direction)
     */
    public ArrayList<String> filterInputs(){
        ArrayList<String> actions = new ArrayList<>(Arrays.asList("use", "take", "drop", "go", "check",
                                                                  "examine", "help", "map", "exit", "quit"));
        ArrayList<String> processedInputs = new ArrayList<>();

        String[] playerInputs = gameMaster.nextLine().toLowerCase().split(" ");

        // Sees how many different words user inputted,
        // tracks first and second if more than one, only tracks first otherwise
        String action = playerInputs[0];
        String noun;
        if(playerInputs.length > 1){
            noun = playerInputs[1];
        } else{
            noun = "foo";
        }

        if(actions.contains(action)){
            Collections.addAll(processedInputs, action, noun);
        } else{
            // adds filler to reset input loop
            Collections.addAll(processedInputs, "foo", "bar");
        }

        return processedInputs;
    }

    /**
     * Contains a switch tree that calls certain methods depending on param 'action' from user input
     * @param room the Room object player is currently in
     * @param action command for the player (use, go, examine, take, check etc.)
     * @param noun object or thing the player interacts with (usually an item or direction)
     */
    public void processInputs(Room room, String action, String noun){
        switch(action){
            case "examine":
                room.printRoomMessage();
                break;
            case "take":
                player.takeItem(room, noun);
                break;
            case "drop":
                player.dropItem(room, noun);
                break;
            case "use":
                player.useItem(room, noun);
                break;
            case "go":
                Room updatedRoom = player.updatePosition(room, noun);
                if(!Arrays.equals(room.getRoomCoordinates(), updatedRoom.getRoomCoordinates())){
                    updatedRoom.printRoomMessage();
                }
                break;
            case "check":
                player.checkInventory();
                break;
            case "map":
                printOutMap();
                break;
            case "help":
                printHelpCommands();
                break;
            default:
                System.out.println("I couldn't understand that command. Input 'help' to see list of commands");
                printInputPrompt();
        }
    }

    /**
     * Prints out a map of all rooms player has been in
     */
    public void printOutMap(){
        String[][] mapArray = createMapArray();

        // Converts 2d String array into a single String
        StringBuilder mapString = new StringBuilder();
        for(int columnIndex = mapArray[0].length - 1; columnIndex >= 0; columnIndex--){
            for(int rowIndex = 0; rowIndex < mapArray.length; rowIndex++){
                mapString.append(mapArray[rowIndex][columnIndex]);
            }
            mapString.append("\n");
        }

        System.out.println(mapString);
        printInputPrompt();
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
        printInputPrompt();
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

            if(room.getHasPlayerBeenHere()) {
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
    private String[][] createMapArray(){
        int[] mapDimensions = findMapDimensions();
        String[][] mapArray = new String[mapDimensions[0]][mapDimensions[1]];

        // Initialize mapArray with '0' denoting no room
        for(String[] mapRow : mapArray){
            Arrays.fill(mapRow, "0");
        }

        // populate map with rooms with '1' denoting a room
        for(int roomIndex = 0; roomIndex < board.getBoardSize(); roomIndex++) {
            Room room = board.getRoom(roomIndex);

            if(room.getHasPlayerBeenHere()) {
                int x = room.getRoomCoordinates()[0] - 1;
                int y = room.getRoomCoordinates()[1] - 1;
                mapArray[x][y] = "1";
            }
        }

        return mapArray;
    }

    private static void printInputPrompt(){
        System.out.print("> ");
    }
}