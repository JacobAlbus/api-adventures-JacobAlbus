package student.adventure;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RoomsTest {
    private Player player;
    private GameBoard board;

    @Before
    public void setUp() throws IOException {
        String filePath = "src/main/resources/Rooms.json";
        player = new Player(filePath, "bob");
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Reader questionsReader = Files.newBufferedReader(
                    Paths.get("src/main/resources/GameQuestions.json"));

            board = gson.fromJson(reader, GameBoard.class);
            reader.close();
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }
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
