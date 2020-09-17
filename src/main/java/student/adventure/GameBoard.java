package student.adventure;

import java.util.Arrays;
import java.util.List;

public class GameBoard {
    protected List<Room> rooms;

    public GameBoard(List<Room> rooms){
        this.rooms = rooms;
    }

    public int getBoardSize(){
        return rooms.size();
    }

    public Room getRoom(int i){
        return rooms.get(i);
    }

    /**
     * Finds room the player is currently in
     * @return the Room object player is currently in
     */
    public Room findPlayerCurrentRoom(Player player){
        for(Room room : rooms){

            if(Arrays.equals(player.getPosition(), room.getRoomCoordinates())){
                room.setHasPlayerBeenHere(true);
                return room;
            }
        }
        // Returns player to first room if current room isn't found in GameBoard
        System.out.println("It seems you've wandered off the path of destiny. " +
                "You have been returned to the first room.");
        System.out.print("> ");
        player.setPosition(rooms.get(0).getRoomCoordinates());
        return rooms.get(0);
    }
}
