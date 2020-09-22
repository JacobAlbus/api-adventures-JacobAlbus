package student.adventure;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Player {
    private int[] position = {1, 1};
    private final List<String> directions = new ArrayList(Arrays.asList("east", "north", "west", "south"));
    private final Map<String, int[]> roomsCoordinatesForItemUse;
    private boolean isTesting = false;
    private ArrayList<String> items = new ArrayList<>();
    private String name;
    private GameBoard board;
    private GameQuestions questions;

    public Player(String boardFilePath, String playerName) throws IOException {
        Map<String, int[]> roomsCoordinatesForItemUse1;
        try {
            Gson gson = new Gson();

            Reader boardReader = Files.newBufferedReader(Paths.get(boardFilePath));
            Reader questionsReader = Files.newBufferedReader(
                    Paths.get("src/main/resources/GameQuestions.json"));

            questions = gson.fromJson(questionsReader, GameQuestions.class);
            board = gson.fromJson(boardReader, GameBoard.class);
            boardReader.close();
            questionsReader.close();
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }

        try{
            roomsCoordinatesForItemUse1 = new HashMap<String, int[]>() {{
                put("torch", board.getRoom(0).getRoomCoordinates());
                put("key", board.getRoom(2).getRoomCoordinates());
                put("calculator", board.getRoom(5).getRoomCoordinates());
                put("lighter", board.getRoom(8).getRoomCoordinates());
            }};
        } catch (IndexOutOfBoundsException e){
            roomsCoordinatesForItemUse1 = new HashMap<String, int[]>() {{
                put("torch", board.getRoom(0).getRoomCoordinates());
            }};
        }

        roomsCoordinatesForItemUse = roomsCoordinatesForItemUse1;
        name = playerName;
    }

    public int[] getPosition(){
        return position;
    }

    public void setPosition(int[] position){
        this.position = position;
    }

    public ArrayList<String> getItems(){
        return items;
    }

    public boolean isTesting() { return isTesting; }

    public void setPlayerIsTesting(boolean isPlayerTesting) { isTesting = isPlayerTesting; }

    public void removeItem(String item) { items.remove(item); }

    /**
     * Removes item from room and adds to player inventory if it is in room and not in player's inventory
     * @param room room player is currently in
     * @param item given item that game checks is in room
     */
    public void takeItem(Room room, String item){
        // TODO make it so that getting and setting rooms variables is handled in room
        if(room.getAvailableItems().contains(item)){
            items.add(item);
            room.getAvailableItems().remove(item);
        } else{
            System.out.println("It seems like the room doesn't have that item");
        }
    }

    /**
     * Removes item from player and puts it in room if player has it and room doesn't
     * @param room player is currently in
     * @param item given item that game checks is in player's inventory and not in room
     */
    public String dropItem(Room room, String item){
        String playerMessage = "";
        // TODO make it so that getting and setting rooms variables is handled in room
        if(items.contains(item) && !room.getAvailableItems().contains(item)){
            items.remove(item);
            room.addAvailableItem(item);
            printInventory();
        } else if(room.getAvailableItems().contains(item)){
            playerMessage = "Can't drop the item, it's already in the room";
            System.out.println(playerMessage);
        } else{
            System.out.println("It seems like you don't have that item");
        }
        return playerMessage;
    }

    /**
     * Uses item if it is in player's inventory
     * @param item item that the player will use
     */
    public String useItem(Room room, String item, boolean useUI){
        String playerMessage = "";
        if(items.contains(item)){
            switch(item){
                case "torch":
                    int[] torchCoords = roomsCoordinatesForItemUse.get("torch");
                    playerMessage = useTorch(room, torchCoords, item);
                    break;
                case "key":
                    int[] keyCoords = roomsCoordinatesForItemUse.get("key");
                    playerMessage = useKey(room, keyCoords, item);
                    break;
                case "calculator":
                    int[] calcCoords = roomsCoordinatesForItemUse.get("calculator");
                    playerMessage = useCalculator(room, calcCoords, item, useUI);
                    break;
                case "lighter":
                    int[] lighterCoords = roomsCoordinatesForItemUse.get("lighter");
                    playerMessage = useLighter(room, lighterCoords, item);
                    break;
                default:
                    playerMessage = "Sorry but that item has no use";
                    System.out.println(playerMessage);
                    break;
            }

        } else {
            playerMessage = "You do not have this item";
            System.out.println(playerMessage);
        }
        return playerMessage;
    }

    /**
     * Updates player position relative to current room location
     * Only updates if given direction is valid for given room
     * @param room Room object player is currently in
     * @param direction given direction user wants player to go
     */
    public int[] updatePosition(Room room, String direction){
        if(directions.contains(direction) && room.getAvailableDoors().contains(direction)){
            switch(direction){
                case "east":
                    position[0] += 1;
                    break;
                case "north":
                    position[1] += 1;
                    break;
                case "west":
                    position[0] -= 1;
                    break;
                case "south":
                    position[1] -= 1;
                    break;
            }
        } else{
            System.out.println("You cannot go in that direction");
        }
        return board.findPlayerCurrentRoom(this).getRoomCoordinates();
    }

    /**
     * Prints out all the items currently in player's inventory
     */
    public void printInventory(){
        System.out.println(name + "'s Inventory: " + items);
    }

    /**
     * Checks if the room item is used in, is a valid room for that item to be used
     * @param room Room object in which useItem() is called in
     * @param coords valid coords for item to be used in
     * @return boolean depending on whether or not room is valid for item use
     */
    private boolean isRoomCorrectForItemUse(Room room, int[] coords){
        boolean isRoomCorrect = false;
        if(Arrays.equals(room.getRoomCoordinates(), coords)){
            isRoomCorrect = true;
        } else {
            System.out.println("It appears that the item has no use here");
        }

        return isRoomCorrect;
    }

    /**
     * Uses torch if in valid room, doesn't otherwise
     * @param room room object where item is being used
     * @param torchCoords coordinates for valid item use room
     * @param item given item (torch) player is trying to use
     * @return message depending on how/where torch is used
     */
    private String useTorch(Room room, int[] torchCoords, String item){
        String playerMessage = "";
        if(isRoomCorrectForItemUse(room, torchCoords)){
            room.setPrimaryDescription(room.getSecondaryDescription());
            room.addAvailableItem(room.getUnavailbleItems().get(0));
            items.remove(item);
            room.printRoomMessage();
        } else{
            playerMessage = "It seems like the torch has no use in this room";
        }
        return playerMessage;
    }

    /**
     * Uses calculator if in valid room, doesn't otherwise
     * @param room room object where item is being used
     * @param calcCoords coordinates for valid item use room
     * @param item given item (calculator) player is trying to use
     * @return message depending on how/where calculator is used
     */
    private String useCalculator(Room room, int[] calcCoords, String item, boolean useUI) {
        String playerMessage = "";
        if(isRoomCorrectForItemUse(room, calcCoords)) {
            isTesting = true;
            if(!useUI && questions.didPlayerAceMathTest(room)) {
                room.setPrimaryDescription(room.getSecondaryDescription());
                room.addAvailableDoors(room.getUnavailableDoors().get(0));
                room.removeUnavailableDoors(room.getUnavailableDoors().get(0));
                items.remove(item);
                room.printRoomMessage();
                isTesting = false;
            } else{
                playerMessage = "It seems like the torch has no use in this room";
            }
        }
        return playerMessage;
    }

    /**
     * Uses key if in valid room, doesn't otherwise
     * @param room room object where item is being used
     * @param keyCoords coordinates for valid item use room
     * @param item given item (key) player is trying to use
     * @return message depending on how/where key is used
     */
    private String useKey(Room room, int[] keyCoords, String item){
        String playerMessage = "";
        if(isRoomCorrectForItemUse(room, keyCoords)){
            room.setPrimaryDescription(room.getSecondaryDescription());
            room.addAvailableDoors(room.getUnavailableDoors().get(0));
            room.removeUnavailableDoors(room.getUnavailableDoors().get(0));
            items.remove(item);
            room.printRoomMessage();
        } else{
            playerMessage = "It seems like the torch has no use in this room";
        }
        return playerMessage;
    }

    /**
     * Uses lighter if in valid room, doesn't otherwise
     * @param room room object where item is being used
     * @param lighterCoords coordinates for valid item use room
     * @param item given item (lighter) player is trying to use
     * @return message depending on how/where lighter is used
     */
    private String useLighter(Room room, int[] lighterCoords, String item){
        String playerMessage = "";
        if(isRoomCorrectForItemUse(room, lighterCoords)){
            // TODO make it so that getting and setting rooms variables is handled in room
            room.setPrimaryDescription(room.getSecondaryDescription());
            room.addAvailableDoors(room.getUnavailableDoors().get(0));
            room.removeUnavailableDoors(room.getUnavailableDoors().get(0));
            items.remove(item);
            room.printRoomMessage();
        } else{
            playerMessage = "It seems like the torch has no use in this room";
        }
        return playerMessage;
    }


}