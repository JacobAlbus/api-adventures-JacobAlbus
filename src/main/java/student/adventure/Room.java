package student.adventure;

import java.util.ArrayList;

public class Room {
    private final int[] roomCoordinates;
    private ArrayList<String> availableDoors;
    private ArrayList<String> unavailableDoors;
    private ArrayList<String> availableItems;
    private ArrayList<String> unavailableItems;
    private String primaryDescription;
    private String secondaryDescription;
    private Boolean hasPlayerBeenHere;

    public Room(int[] roomCoordinates, ArrayList<String> availableDoors, ArrayList<String> unavailableDoors,
                ArrayList<String> availableItems, ArrayList<String> unavailableItems, String primaryDescription,
                String secondaryDescription, Boolean hasPlayerBeenHere){
        this.roomCoordinates = roomCoordinates;
        this.availableDoors = availableDoors;
        this.unavailableDoors = unavailableDoors;
        this.availableItems = availableItems;
        this.unavailableItems = unavailableItems;
        this.primaryDescription = primaryDescription;
        this.secondaryDescription = secondaryDescription;
        this.hasPlayerBeenHere = hasPlayerBeenHere;
    }

    public int[] getRoomCoordinates(){
        return roomCoordinates;
    }

    public ArrayList<String> getAvailableDoors(){
        return availableDoors;
    }

    public void addAvailableDoors(String door){
        availableDoors.add(door);
    }

    public ArrayList<String> getUnavailableDoors(){
        return unavailableDoors;
    }

    public void removeUnavailableDoors(String door){
        unavailableDoors.remove(door);
    }

    public ArrayList<String> getAvailableItems(){
        return availableItems;
    }

    public void addAvailableItem(String item){
        availableItems.add(item);
    }

    public ArrayList<String> getUnavailbleItems(){
        return unavailableItems;
    }

    public String getPrimaryDescription(){
        return primaryDescription;
    }

    public void setPrimaryDescription(String description){
        this.primaryDescription = description;
    }

    public String getSecondaryDescription(){
        return secondaryDescription;
    }

    public Boolean getHasPlayerBeenHere(){
        return hasPlayerBeenHere;
    }

    public void setHasPlayerBeenHere(Boolean bool){
        hasPlayerBeenHere = bool;
    }

    /**
     * Prints a formatted message containing the info for each room
     */
    public void printRoomMessage(){
        System.out.println(primaryDescription);

        StringBuilder doors = new StringBuilder();
        for(String direction: availableDoors){
            doors.append(direction);
            doors.append(" ");
        }

        System.out.println("Direction: " + doors.toString());

        StringBuilder items = new StringBuilder();
        for(String item: availableItems){
            items.append(item);
            items.append(" ");
        }

        System.out.println("Items: " + items.toString());
        System.out.print("> ");
    }

}
