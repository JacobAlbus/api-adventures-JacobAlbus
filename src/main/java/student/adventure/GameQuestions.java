package student.adventure;

import java.util.List;
import java.util.Scanner;

public class GameQuestions {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private boolean isTestOccuring = false;
    private int correctAnswerCount = 0;

    public GameQuestions(List<Question> gameQuestions){
        this.questions = gameQuestions;
    }

    public Question getGameQuestions(int i) {
        return questions.get(i);
    }

    public int getListSize(){
        return questions.size();
    }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }

    public boolean isTestOccuring(){ return isTestOccuring; }

    public void setTestOccuring(boolean isTestOccuring) { this.isTestOccuring = isTestOccuring; }

    /**
     * Simulates a math test with basic problems; returns boolean depending on whether or not player passes test
     * @return boolean representing whether or not player passed test
     */
    public boolean didPlayerAceMathTest(Room room){
        isTestOccuring = true;
        int numCorrect = 0;
        int passingGrade = 4;

        System.out.println("You have begun the eternal math test, pick your answers wisely!");

        for(Question question : questions){
            numCorrect += this.askMathQuestionScanner(question.getQuestion(), question.getCorrectAnswer());
        }
        isTestOccuring = false;
        room.setPrimaryDescription(room.getPrimaryDescription());

        if(numCorrect >= passingGrade){
            System.out.println("Congratulations, you have passed the test");
            return true;
        } else {
            System.out.println("You failed stupid, try again");
            return false;
        }

    }

    /**
     * Asks player a math question, takes input, and determines if it's correct
     * Made public for testing purposes
     * @param question the question being asked of the player
     * @param correctAnswer correct answer to question
     * @return 1 if player is right, 0 otherwise
     */
    // TODO ask elizabeth about configuring this function for booleans
    public int askMathQuestionScanner(String question, String correctAnswer){
        System.out.println(question);
        System.out.print("> ");

        Scanner playerInput = new Scanner(System.in);
        String playerAnswer = playerInput.nextLine();

        if(playerAnswer.equals(correctAnswer)){
            System.out.println("Correct!");
            return 1;
        } else {
            System.out.println("Wrong!");
            return 0;
        }

    }

    /**
     * Asks web-browser players for answers to Math Test and keeps track of how many they get correct
     * @param room Room object where player is taking test
     * @param player Player object who is taking test
     * @param playerAnswer answer given by player
     * @return playerMessage indicating if they got it right or wrong
     */
//    public String askMathQuestionUI(Room room, Player player, String playerAnswer){
//        String playerMessage = "";
//
//        if(player.isTesting()){
//            room.setPrimaryDescription(startedTestMessage);
//
//            playerMessage = checkPlayerAnswer(playerAnswer);
//            currentQuestionIndex++;
//
//            if(currentQuestionIndex == questions.size()){
//                player.setPlayerIsTesting(false);
//                currentQuestionIndex = 0;
//            }
//
//            if(!player.isTesting()){
//                playerMessage = checkIfPlayerPassed(player, room);
//            }
//        }
//
//        return playerMessage;
//    }

    public String askMathQuestionUI(Room room, Player player, String playerAnswer) {
        String playerMessage = "Wrong!";

        if (player.isTesting()) {
            playerMessage = checkPlayerAnswer(playerAnswer);
            currentQuestionIndex++;

            if (currentQuestionIndex == questions.size()) {
                player.setPlayerIsTesting(false);
                currentQuestionIndex = 0;
            }

            if(!player.isTesting()) {
                playerMessage = checkIfPlayerPassed(player, room);
            }

            return playerMessage;
        }
        return playerMessage;
    }

    /**
     * Checks to see if player given answer is correct
     * @param playerAnswer answer given by the player
     * @return "correct" or "wrong" depending on whether or not player answered correctly
     */
    private String checkPlayerAnswer(String playerAnswer){
        String correctAnswer = questions.get(currentQuestionIndex).getCorrectAnswer();
        if(playerAnswer.equals(correctAnswer)){
            correctAnswerCount++;
            return "Correct!";
        } else {
            return "Wrong!";
        }
    }

    /**
     * Checks to see if player passed test given some
     * @param player Player object taking test
     * @param room Room object where player is taking test
     * @return "passed" or "failed" depending on whether or not player passed test
     */
    private String checkIfPlayerPassed(Player player, Room room) {
        int passingGrade = 4;
        if (correctAnswerCount >= passingGrade) {
            room.addAvailableDoors(room.getUnavailableDoors().get(0));
            room.setPrimaryDescription(room.getSecondaryDescription());
            player.removeItem("calculator");
            correctAnswerCount = 0;
            return "Congratualtion, you passed the test!";
        } else {
            correctAnswerCount = 0;
            return "You failed, try again";
        }
    }
}
