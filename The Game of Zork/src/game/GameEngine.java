package game;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import elements.Direction;
import elements.Item;
import elements.Location;
import elements.Player;

//Invokes a queue to invoke commands (intermediate/indirect invoker)
public class GameEngine {
    private Player player;        
    private List<Item> items = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private List<Direction> directions = new ArrayList<>();
    private AbstractMap<String, Function<List<String>, Command<String>>> commands = 
            new HashMap<>();
    WorkingQueue workingQueue = new WorkingQueue();
    
    /**
     * Constructor of game engine
     * 
     * @param locations - locations of the game
     */
    public GameEngine(Player player, Location... locations) {
        Arrays.stream(locations)
              .forEach(this.locations::add);
                
        this.player = player;
        initializeCommands();
    }
    
    //*******************************************************************************
    //                                Methods
    //*******************************************************************************
    
    /**
     * Finds a command that matches commandArgs, puts in on the queue and invokes a queue.
     * Execution of commands in a queue is in FIFO order.
     * 
     * @param commandArgs - one or multiple command arguments
     * @return - result of multiple possible commands
     */
    public List<String> executeCommands(CommandArgs... commandArgs) {
        addToCommandQueue(commandArgs);
        
        return workingQueue.executeCommands();
    }
    
    //*******************************************************************************
    //                              Helper methods
    //*******************************************************************************
    
    private void initializeCommands() {
        commands.put("pickup", arguments -> () -> player.pickup(arguments));
        commands.put("drop", arguments -> () -> player.drop(arguments));
    }
    
    private void addToCommandQueue(CommandArgs... commandArgs) {        
        Arrays.stream(commandArgs)
              .map(this::findCommand)
              .forEach(workingQueue::addCommand);        
    }
    
    private Command<String> findCommand(CommandArgs commandArgs) {
        return commands.get(commandArgs.getCommand())
                       .apply(commandArgs.getArguments());
    }
}
