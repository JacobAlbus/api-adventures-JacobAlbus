package student.adventure;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RoomsTest {
    private GameEngine engine;
    private Player player;
    private GameBoard board;

    @Before
    public void setUp() throws IOException {
        engine = new GameEngine("src/main/resources/Rooms.json", "bob");
        player = engine.player;
        board = engine.board;
    }
    @Test
    public void testPrintRoomMessage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        board.getRoom(0).printRoomMessage();

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString().split(">")[0];
        assertEquals("You're in a dark room with one visible door." +
                "\r\nDirection: east \r\nItems:  \r\n", printedString);
    }

    @Test
    public void testFindPlayerCurrentRoom(){
        Room room = board.findPlayerCurrentRoom(player);

        assertArrayEquals(room.getRoomCoordinates(), player.getPosition());
    }

}
