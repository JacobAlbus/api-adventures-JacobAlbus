import org.glassfish.grizzly.http.server.HttpServer;
import student.adventure.GameEngine;
import student.server.AdventureResource;
import student.server.AdventureServer;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        // Scanner prompt for name moved to main method for testing purposes
        System.out.println("Hello player! What would you like to name your adventurer?");
        System.out.print("> ");

        Scanner userInput = new Scanner(System.in);
        String name = userInput.nextLine();
        System.out.println(".\n" + ".\n" + ".\n.");

        String path = "src/main/resources/Rooms.json";
        GameEngine engine = new GameEngine(path, name);
        engine.playGame();
//        HttpServer server = AdventureServer.createServer(AdventureResource.class);
//        server.start();
    }
}