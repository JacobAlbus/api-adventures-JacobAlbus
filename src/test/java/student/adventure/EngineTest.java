package student.adventure;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EngineTest {
    private GameEngine engine;

    @Before
    public void setUp() throws IOException {
        engine = new GameEngine("src/main/resources/Rooms.json", "bob", 0, false);
    }

    // add tests for 'valid' jsons

    public void testReadInJsonNullFile() throws IOException {
        try {
            new GameEngine("src/test/resources/RoomsNull.json", "bob", 0, false);
        } catch (NullPointerException e) {
            assertEquals("The json file passed is null", e.getMessage());
        }
    }

    @Test
    public void testReadInJsonFileNotFound(){
        try {
            new GameEngine("src/test/resources/R.json", "bob", 0, false);
        } catch (IOException e) {
            assertEquals("The specified file does not exist", e.getMessage());
        }
    }

    @Test
    public void testProcessInputsInvalidInput() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("foo", "bar");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("I couldn't understand that command. Input 'help' to see list of commands\r\n", printedString);
    }

    @Test
    public void testProcessInputsExamine() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("go", "east");
        engine.processInputs("take", "torch");
        engine.processInputs("examine", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().replace("\r\n", "\n");
        assertEquals("This room doesn't look too different than the last\n" +
                "Direction: west north \n" +
                "Items: torch \n" +
                "bob's Inventory: [torch]\n" +
                "This room doesn't look too different than the last\n" +
                "Direction: west north \n", printedString);
    }

    @Test
    public void testProcessInputsTake() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("take", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("It seems like the room doesn't have that item\r\nbob's Inventory: []\r\n", printedString);
    }

    @Test
    public void testProcessInputsDrop() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("drop", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("It seems like you don't have that item\r\n", printedString);
    }

    @Test
    public void testProcessInputsUse() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("use", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("You do not have this item\r\n", printedString);
    }

    @Test
    public void testProcessInputsGo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs( "go", "t");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("You cannot go in that direction\r\n", printedString);
    }

    @Test
    public void testProcessInputsCheck() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("check", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("bob's Inventory: []\r\n", printedString);
    }

    @Test
    public void testProcessInputsHelp() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("help", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
    assertEquals("Input go + 'direction' (east, west, north, south) to through corresponding direction \n"
            + "Input take + 'item' to grab item from room \n"
            + "Input use + 'item' to use item in inventory \n"
            + "Input drop + 'item' to drop item from inventory and leave it in room \n"
            + "Input examine to see room information \n"
            + "Input check to see all items currently in inventory \n"
            + "Input exit or quit to stop playing Adventure\r\n", printedString);
    }

    @Test
    public void testProcessInputsDefault() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("yuppie", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("I couldn't understand that command. " +
                             "Input 'help' to see list of commands\r\n", printedString);
    }

    @Test
    public void testFindPlayerCurrentRoomNotFound() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            String filePath = "src/test/resources/RoomsInvalidTesting.json";
            Player testPlayer = new Player(filePath, "bob");
            GameBoard testBoard;
            try {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Paths.get(filePath));

                testBoard = gson.fromJson(reader, GameBoard.class);
                reader.close();
                // ask elizabeth why we don't want to throw an exception
            } catch (NullPointerException e) {
                throw new NullPointerException("The json file passed is null");
            } catch (IOException e) {
                throw new IOException("The specified file does not exist");
            }

            testPlayer.updatePosition(testBoard.getRoom(0), "east");
            System.out.println(testBoard.findPlayerCurrentRoom(testPlayer).getAvailableDoors());

            System.out.flush();
            System.setOut(old);

            String printedString = baos.toString();
            assertEquals("It seems you've wandered off the path of destiny. " +
                                 "You have been returned to the first room.\r\n" +
                                 "> [east]\r\n", printedString);
    }

    @Test
    public void testPrintOutSquareMap()  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("go", "east");
        engine.processInputs("go", "north");
        engine.processInputs("go", "north");
        System.out.println(engine.createPrintedMap());

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        printedString = printedString.replace("\r", "").replace("\n", "");
        assertEquals("This room doesn't look too different than the lastDirection: west north Items: torch " +
                "There's a door at the north end of the room, but it's locked.Direction: south Items: torch " +
                "You cannot go in that direction0111", printedString);
    }

    @Test
    public void testPrintOutRectangleMap() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("go", "east");
        engine.processInputs("go", "east");
        System.out.println(engine.createPrintedMap());

        System.out.flush();
        System.setOut(old);
        String printedString = baos.toString().replace("\r\n", "");
        assertEquals("This room doesn't look too different than the lastDirection: west north Items: torch " +
                             "You cannot go in that direction11", printedString);
    }

    @Test
    public void testPlayerWins() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs("go", "east");
        engine.processInputs("take", "torch");
        engine.processInputs("go", "west");
        engine.processInputs("use", "torch");
        engine.processInputs("take", "key");
        engine.processInputs("go", "east");
        engine.processInputs("go", "north");
        engine.processInputs("use", "key");
        engine.processInputs("go", "north");
        engine.processInputs("go", "north");
        engine.processInputs("take", "calculator");
        engine.processInputs("go", "south");
        engine.processInputs("go", "east");
        engine.processInputs("go", "east");
        engine.processInputs("take", "lighter");
        engine.processInputs("go", "east");
        engine.processInputs("use", "lighter");
        engine.processInputs("go", "south");
        engine.processInputs("go", "south");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().replace("\r\n", "\n");
    assertEquals("This room doesn't look too different than the last\n" +
            "Direction: west north \n" +
            "Items: torch \n" +
            "bob's Inventory: [torch]\n" +
            "You're in a dark room with one visible door.\n" +
            "Direction: east \n" +
            "After using the torch, you see a sparkle in one of the cracks. After investigating, you see a key\n" +
            "Direction: east \n" +
            "Items: key \n" +
            "bob's Inventory: [key]\n" +
            "This room doesn't look too different than the last\n" +
            "Direction: west north \n" +
            "There's a door at the north end of the room, but it's locked.\n" +
            "Direction: south \n" +
            "Items: torch \n" +
            "The door at the north end opened!\n" +
            "Direction: south north \n" +
            "Items: torch \n" +
            "There are four different doors, pick your path wisely\n" +
            "Direction: north east south west \n" +
            "Doesn't seem to be too much in this room, except a small computational device\n" +
            "Direction: south \n" +
            "Items: calculator \n" +
            "bob's Inventory: [calculator]\n" +
            "There are four different doors, pick your path wisely\n" +
            "Direction: north east south west \n" +
            "There's a locked door with some ancient symbol: 'Requires TI-84' \n" +
            "Direction: west east \n" +
            "There's sun light coming from the cracks in the room, you're getting close to the exit\n" +
            "Direction: west east \n" +
            "Items: lighter \n" +
            "bob's Inventory: [calculator, lighter]\n" +
            "It smells of rotten eggs in this room, and there's a big steel door to the south\n" +
            "Direction: west \n" +
            "WOW, the smell of rotten eggs was actually a gas leak and the lighter caused the room to combust. The south door was blown down\n" +
            "Direction: west south \n" +
            "The exit is right there at the other end...MAKE A RUN FOR IT!!!\n" +
            "Direction: south \n" +
            "You emerge in the dormitory hallway, you look back and see your room number on a plaque next to the door. Maybe it's time for spring cleaning?\n" +
            "Direction: \n" +
            "You emerge in the dormitory hallway, you look back and see your room number on a plaque next to the door. Maybe it's time for spring cleaning?\n" +
            "You win! Play again to venture back into your dorm\n" +
            "Direction: \n", printedString);
    }

}