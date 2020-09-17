package student.adventure;

import java.io.*;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EngineTest {
    GameEngine engine;
    Player player;
    GameBoard board;

    @Before
    public void setUp() throws IOException {
        engine = new GameEngine("src/main/resources/Rooms.json", "bob");
        player = engine.player;
        board = engine.board;
    }

    @Test
    public void testReadInJsonNullFile() throws IOException {
        try {
            new GameEngine("src/main/resources/RoomsNull.json", "bob");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "The json file passed is null");
        }
    }

    @Test
    public void testReadInJsonFileNotFound(){
        try {
            new GameEngine("src/main/resources/R.json", "bob");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "The specified file does not exist");
        }
    }

    @Test
    public void testProcessInputsInvalidInput(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "foo", "bar");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("I couldn't understand that command. Input 'help' to see list of commands\r\n", printedString);
    }

    @Test
    public void testProcessInputsExamine(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "examine", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You're in a dark room with one visible door." +
                "\r\nDirection: east \r\nItems:  \r\n", printedString);
    }

    @Test
    public void testProcessInputsTake(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "take", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("It seems like the room doesn't have that item\r\n", printedString);
    }

    @Test
    public void testProcessInputsDrop(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "drop", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("It seems like you don't have that item\r\n", printedString);
    }

    @Test
    public void testProcessInputsUse(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "use", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You do not have this item\r\n", printedString);
    }

    @Test
    public void testProcessInputsGo(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "go", "t");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You cannot go in that direction\r\n", printedString);
    }

    @Test
    public void testProcessInputsCheck(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "check", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("bob's Inventory: []\r\n", printedString);
    }

    @Test
    public void testProcessInputsHelp(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "help", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(Pattern.quote("+"))[0];
        assertEquals("Input go ", printedString);
    }

    @Test
    public void testProcessInputsDefault(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        engine.processInputs(board.getRoom(0), "yuppie", "foo");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("I couldn't understand that command. " +
                             "Input 'help' to see list of commands\r\n", printedString);
    }

    @Test
    public void testFindPlayerCurrentRoomNotFound() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            GameEngine testEngine = new GameEngine("src/main/resources/RoomsInvalidTesting.json",
                                                      "bob");
            Player testPlayer = testEngine.player;
            GameBoard testBoard = testEngine.board;

            testPlayer.updatePosition(testBoard.getRoom(0), "east");
            System.out.println(testBoard.findPlayerCurrentRoom(testPlayer));

            System.out.flush();
            System.setOut(old);

            String printedString = baos.toString().split("> ")[0];
            assertEquals("It seems you've wandered off the path of destiny. " +
                                 "You have been returned to the first room.\r\n", printedString);
    }

    @Test
    public void testPrintOutSquareMap(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.updatePosition(board.findPlayerCurrentRoom(player), "east");
        player.updatePosition(board.findPlayerCurrentRoom(player), "north");
        player.updatePosition(board.findPlayerCurrentRoom(player), "north");
        engine.printOutMap();

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[1];
        printedString = printedString.replace("\r", "").replace("\n", "");
        assertEquals(" 0111", printedString);
    }

    @Test
    public void testPrintOutRectangleMap(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.updatePosition(board.findPlayerCurrentRoom(player), "east");
        player.updatePosition(board.findPlayerCurrentRoom(player), "east");
        engine.printOutMap();

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[1];
        printedString = printedString.replace("\r", "").replace("\n", "");
        assertEquals(" 11", printedString);
    }

    @Test
    public void testPlayerWins(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.updatePosition(board.getRoom(0), "east");
        player.takeItem(board.getRoom(1), "torch");
        player.updatePosition(board.getRoom(1), "west");
        player.useItem(board.getRoom(0), "torch");
        player.takeItem(board.getRoom(0), "key");
        player.updatePosition(board.getRoom(0), "east");
        player.updatePosition(board.getRoom(1), "north");
        player.useItem(board.getRoom(2), "key");
        player.updatePosition(board.getRoom(2), "north");
        player.updatePosition(board.getRoom(3), "north");
        player.takeItem(board.getRoom(6), "calculator");
        player.updatePosition(board.getRoom(6), "south");
        player.updatePosition(board.getRoom(3), "east");
        board.getRoom(5).addAvailableDoors("east");
        player.updatePosition(board.getRoom(5), "east");
        player.takeItem(board.getRoom(7), "lighter");
        player.updatePosition(board.getRoom(7), "east");
        player.useItem(board.getRoom(8), "lighter");
        player.updatePosition(board.getRoom(8), "south");
        player.updatePosition(board.getRoom(9), "south");
        engine.processInputs(board.findPlayerCurrentRoom(player), "examine", "");

        engine.gameLoop();
        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[9];
        assertEquals(" You win! Play again to venture back into your dorm\r\n", printedString);

    }

}