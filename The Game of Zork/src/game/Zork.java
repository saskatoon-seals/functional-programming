package game;

import java.util.stream.Stream;

import elements.Location;
import elements.Player;

public class Zork {
    public static void main(String... args) {
        GameEngine gameEngine = new GameEngine(new Player(null), 
                new Location("A"), new Location("B"), 
                new Location("C"), new Location("D"));
        UserInterface userInterface = new UserInterface();
                
        CommandArgs[] commandArgs;        
        do {
            Stream<String> commandStream = userInterface.readCommand();
            //@TODO: Loop through all the commands in a given input stream
            commandArgs = userInterface.parseCommands(commandStream);                        
            
            gameEngine.executeCommands(commandArgs);
            
        } while(!commandArgs[0].getCommand().equalsIgnoreCase("quit")); //what if it's not the 1st?
    }
}
