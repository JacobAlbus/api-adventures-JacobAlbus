package student.adventure;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class GameQuestionsTest {
    private Player player;
    private GameBoard board;
    private GameQuestions questions;

    @Before
    public void setUp() throws IOException {
        String filePath = "src/main/resources/Rooms.json";
        player = new Player(filePath, "bob");
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Reader questionsReader = Files.newBufferedReader(
                    Paths.get("src/main/resources/GameQuestions.json"));

            questions = gson.fromJson(questionsReader, GameQuestions.class);
            board = gson.fromJson(reader, GameBoard.class);
            reader.close();
        } catch (NullPointerException e) {
            throw new NullPointerException("The json file passed is null");
        } catch (IOException e) {
            throw new IOException("The specified file does not exist");
        }
    }

    @Test
    public void testMathTestScanner(){
        // Next two lines are from: https://bugsdb.com/_en/debug/09bdbc2d248d31d6785ba772ea8689cb
        ByteArrayInputStream in = new ByteArrayInputStream("64".getBytes());
        System.setIn(in);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        questions.askMathQuestionScanner("What's 4 times 16", "64");

        System.out.flush();
        System.setOut(old);

        String printedString = baos.toString();
        assertEquals("What's 4 times 16\r\n> Correct!\r\n", printedString);
    }

    @Test
    public void testMathTestUIPlayerNotTesting(){
        player.setPlayerIsTesting(false);
        String message = questions.askMathQuestionUI(board.getRoom(5), player, "64");
        assertEquals("Wrong!", message);
    }

    @Test
    public void testMathTestUISingleQuestion(){
        player.setPlayerIsTesting(true);
        String message = questions.askMathQuestionUI(board.getRoom(5), player, "64");
        assertEquals("Correct!", message);
    }

    @Test
    public void testMathTestUIPassedTest(){
        player.setPlayerIsTesting(true);
        questions.askMathQuestionUI(board.getRoom(5), player, "64");
        questions.askMathQuestionUI(board.getRoom(5), player, "3.14");
        questions.askMathQuestionUI(board.getRoom(5), player, "i");
        questions.askMathQuestionUI(board.getRoom(5), player, "216");
        String message = questions.askMathQuestionUI(board.getRoom(5), player, "1");

        assertEquals("Congratualtion, you passed the test!", message);
    }

    @Test
    public void testMathTestUIFailedTest(){
        player.setPlayerIsTesting(true);
        questions.askMathQuestionUI(board.getRoom(5), player, "63");
        questions.askMathQuestionUI(board.getRoom(5), player, "3.2");
        questions.askMathQuestionUI(board.getRoom(5), player, "e");
        questions.askMathQuestionUI(board.getRoom(5), player, "625");
        String message = questions.askMathQuestionUI(board.getRoom(5), player, "0");

        assertEquals("You failed, try again", message);
    }
}
