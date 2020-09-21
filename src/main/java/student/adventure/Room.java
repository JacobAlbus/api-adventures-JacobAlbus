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
    private String imageUrl;
    private String videoUrl;
    private boolean hasPlayerBeenHere;

    public Room(int[] roomCoordinates,
                ArrayList<String> availableDoors,
                ArrayList<String> unavailableDoors,
                ArrayList<String> availableItems,
                ArrayList<String> unavailableItems,
                String primaryDescription,
                String secondaryDescription,
                String imageUrl,
                String videoUrl,
                boolean hasPlayerBeenHere){
        this.roomCoordinates = roomCoordinates;
        this.availableDoors = availableDoors;
        this.unavailableDoors = unavailableDoors;
        this.availableItems = availableItems;
        this.unavailableItems = unavailableItems;
        this.primaryDescription = primaryDescription;
        this.secondaryDescription = secondaryDescription;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
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

    public void removeItem(String item){
        availableItems.remove(item);
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

    public void setSecondaryDescription(String description){
        this.secondaryDescription = description;
    }

    public String getImageUrl() { return imageUrl; }

    public String getVideoUrl() { return videoUrl; }

    public Boolean hasPlayerBeenHere(){
        return hasPlayerBeenHere;
    }

    public void setHasPlayerBeenHere(boolean hasPlayerBeenHere){
        this.hasPlayerBeenHere = hasPlayerBeenHere;
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
        String fillerSpace = " ";
        for(String item: availableItems){
            items.append(item);
            items.append(fillerSpace);
        }

        if(!items.toString().equals(fillerSpace) && availableItems.size() != 0){
            System.out.println("Items: " + items.toString());
        }

    }

}
