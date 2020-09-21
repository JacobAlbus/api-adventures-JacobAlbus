package student.adventure;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTests {
    private Player player;
    private GameBoard board;

    @Before
    public void setUp() throws IOException {
        String filePath = "src/main/resources/Rooms.json";
        player = new Player(filePath, "bob");
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

    @Test
    public void testPlayerUpdatePositionEast(){
        player.updatePosition(board.getRoom(0),"east");

        int[] position = player.getPosition();
        int[] roomCoordinates = board.getRoom(1).getRoomCoordinates();

        assertArrayEquals(roomCoordinates, position);
    }

    @Test
    public void testPlayerUpdatePositionWest(){
        player.updatePosition(board.getRoom(0),"east");
        player.updatePosition(board.getRoom(1),"west");

        int[] position = player.getPosition();
        int[] roomCoordinates = board.getRoom(0).getRoomCoordinates();

        assertArrayEquals(roomCoordinates, position);
    }

    @Test
    public void testPlayerUpdatePositionNorth(){
        player.updatePosition(board.getRoom(0),"east");
        player.updatePosition(board.getRoom(1),"north");

        int[] position = player.getPosition();
        int[] roomCoordinates = board.getRoom(2).getRoomCoordinates();

        assertArrayEquals(roomCoordinates, position);
    }

    @Test
    public void testPlayerUpdatePositionSouth(){
        player.updatePosition(board.getRoom(0),"east");
        player.updatePosition(board.getRoom(1),"north");
        player.updatePosition(board.getRoom(2),"south");

        int[] position = player.getPosition();
        int[] roomCoordinates = board.getRoom(1).getRoomCoordinates();

        assertArrayEquals(roomCoordinates, position);
    }

    @Test
    public void testPlayerUpdatePositionInvalidDirection(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.updatePosition(board.getRoom(0), "weast");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You cannot go in that direction\r\n", printedString);
    }

    @Test
    public void testPlayerUpdatePositionNoInput(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.updatePosition(board.getRoom(0), "weast");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You cannot go in that direction\r\n", printedString);
    }

    @Test
    public void testCheckInventory(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.takeItem(board.getRoom(1), "torch");
        player.printInventory();

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("bob's Inventory: [torch]\r\n", printedString);
    }

    @Test
    public void testPlayerTakeItem(){
        player.takeItem(board.getRoom(1), "torch");
        ArrayList<String> items = player.getItems();
        Assert.assertTrue(items.contains("torch"));
        assertEquals(1, items.size());
    }

    @Test
    // Code from here: https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
    public void testPlayerTakeItemNotFoundInRoom(){
        // figure out what this does and write proper variable names
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        player.takeItem(board.getRoom(1), "borch");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("It seems like the room doesn't have that item\r\n", printedString);
    }

    @Test
    public void testPlayerDropsItem(){
        player.updatePosition(board.getRoom(0), "east");
        player.takeItem(board.getRoom(1), "torch");
        player.dropItem(board.getRoom(1), "torch");

        List<String> itemsInRoom = board.getRoom(1).getAvailableItems();
        List<String> playerItems = player.getItems();

        Assert.assertTrue(itemsInRoom.contains("torch") && itemsInRoom.size() == 1);
        Assert.assertFalse(playerItems.contains("torch") && playerItems.size() == 1);
    }

    @Test
    public void testPlayerDropsItemNotFoundInInventory(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintStream old = System.out;

        System.setOut(ps);

        player.dropItem(board.getRoom(0), "torch");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("It seems like you don't have that item\r\n", printedString);
    }

    @Test
    public void testPlayerDropsItemAlreadyInRoom(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintStream old = System.out;

        System.setOut(ps);
        player.takeItem(board.getRoom(2), "torch");
        player.dropItem(board.getRoom(1), "torch");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("Can't drop the item, it's already in the room\r\n", printedString);
    }

    @Test
    public void testPlayerUsesItemNotFoundInInventory(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintStream old = System.out;

        System.setOut(ps);
        player.useItem(board.getRoom(0), "torch", false);

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("You do not have this item\r\n", printedString);
    }

    @Test
    public void testPlayerUsesItemInvalidRoom(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintStream old = System.out;
        System.setOut(ps);

        player.takeItem(board.getRoom(1), "torch");
        player.useItem(board.getRoom(1), "torch", false);

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("It appears that the item has no use here\r\n", printedString);
    }

    @Test
    public void testPlayerUsesItemWithNoUse(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintStream old = System.out;
        System.setOut(ps);

        player.takeItem(board.getRoom(4), "knife");
        player.useItem(board.getRoom(4), "knife", false);

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("Sorry but that item has no use\r\n", printedString);
    }

    @Test
    public void testPlayerUsesTorch(){
        player.takeItem(board.getRoom(1), "torch");
        player.useItem(board.getRoom(0), "torch", false);

        String description = board.getRoom(0).getPrimaryDescription();
        assertEquals("After using the torch, you see a sparkle in one of the cracks. " +
                "After investigating, you see a key", description);
    }

    @Test
    public void testPlayerUsesKey(){
        player.takeItem(board.getRoom(1), "torch");
        player.useItem(board.getRoom(0), "torch", false);
        player.takeItem(board.getRoom(0), "key");
        player.useItem(board.getRoom(2), "key", false);

        String description = board.getRoom(2).getPrimaryDescription();
        assertEquals("The door at the north end opened!", description);
    }

    @Test
    public void testPlayerUsesLighter(){
        player.takeItem(board.getRoom(7), "lighter");
        player.useItem(board.getRoom(8), "lighter", false);

        String description = board.getRoom(8).getPrimaryDescription();
        assertEquals("WOW, the smell of rotten eggs was actually a gas leak " +
                "and the lighter caused the room to combust. The south door was blown down", description);
    }

    @Test
    public void testPlayerUsesCalculator(){
        // Next two lines are from: https://bugsdb.com/_en/debug/09bdbc2d248d31d6785ba772ea8689cb
        ByteArrayInputStream in = new ByteArrayInputStream("64".getBytes());
        System.setIn(in);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        try{
            player.takeItem(board.getRoom(6), "calculator");
            player.useItem(board.getRoom(5), "calculator", false);
        } catch (NoSuchElementException e){
            in = new ByteArrayInputStream("64".getBytes());
            System.setIn(in);
        }


        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("You have begun the eternal math test, pick your answers wisely!\r\n" +
                "What is the product of the squares of four and two\r\n" +
                "> Correct\r\n" +
                "Give me pi to the first 3 digits\r\n" +
                "> ", printedString);
    }

    @Test
    public void testPlayerMathTest(){
        // Next two lines are from: https://bugsdb.com/_en/debug/09bdbc2d248d31d6785ba772ea8689cb
        ByteArrayInputStream in = new ByteArrayInputStream("64".getBytes());
        System.setIn(in);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        //player.askMathQuestion("What's 4 times 16", "64");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("What's 4 times 16\r\n> Correct\r\n", printedString);
    }
}