package game;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import elements.Item;
import elements.Location;
import elements.Player;
import exceptions.MissingItemException;

public class GameEngineTest {
    
    private CommandArgs pickupAxe, dropKey;   
    private GameEngine gameEngine;
    private Location location;
    private AbstractMap<String, Item> items = new HashMap<>();
    private Player player;
    
    @Before
    public void setup() {        
        items.put("axe", new Item("axe"));
        items.put("key", new Item("key"));
        location = new Location("A").withItem(items.get("axe"));
        player = new Player(location);
        gameEngine = new GameEngine(player, location);
        
        pickupAxe = new CommandArgs()
                .withCommand("pickup")
                .withArgument("axe");
        
        dropKey = new CommandArgs()
                .withCommand("drop")
                .withArgument("key");
    }

    @Test
    public void pickingAxeReturnsExpectedString() {
        List<String> result = gameEngine.executeCommands(pickupAxe);
               
        assertEquals(1, result.size());
        assertEquals("Picked axe.", result.get(0));
    }
    
    @Test
    public void pickingDifferentItemReturnsExpectedString() {
        CommandArgs commandArgs = new CommandArgs()
                                        .withCommand("pickup")
                                        .withArgument("key");        
        location.withItem(items.get("key"));

        List<String> result = gameEngine.executeCommands(commandArgs);
        
        assertEquals(1, result.size());
        assertEquals("Picked key.", result.get(0));
    }
    
    @Test(expected=MissingItemException.class)
    public void failsPickingUpItemThatIsMissingOnLocation() {
        gameEngine.executeCommands(new CommandArgs()
                                        .withCommand("pickup")
                                        .withArgument("key"));
    }
    
    @Test
    public void addsPickedItemToPlayerAndRemovesItFromLocation() {
        gameEngine.executeCommands(pickupAxe);
        
        assertEquals("axe", player.getItems());
        assertEquals("", location.getItems());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void throwsIfPlayerTriesToPickMoreThanOneItem() {
        gameEngine.executeCommands(pickupAxe.withArgument("key"));
    }
    
    @Test
    public void whenPlayerIsCarryingKeyDroppingKeySucceeds() {
        //@TODO: Don't use pickup method
        location.withItem(items.get("key"));
        player.pickup(Arrays.asList(new String[]{"key"}));
        
        List<String> result = gameEngine.executeCommands(dropKey);
        
        assertEquals(1, result.size());
        assertEquals("Dropped key.", result.get(0));
    }
    
    @Test(expected=MissingItemException.class)
    public void throwsWhenTryingToDropItemPlayerIsNotCarrying() {
        gameEngine.executeCommands(dropKey);
    }
    
    @Test
    public void droppingDifferentItemReturnsExpectedString() {
        //@TODO: Don't use pickup method
        player.pickup(Arrays.asList(new String[]{"axe"}));
        
        List<String> result = gameEngine.executeCommands(new CommandArgs().withCommand("drop")
                                                                          .withArgument("axe"));
        
        assertEquals(1, result.size());
        assertEquals("Dropped axe.", result.get(0));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void throwsIfTryingToDropMultipleItemsAtOnce() {
        gameEngine.executeCommands(dropKey.withArgument("axe"));
    }
    
    @Test
    public void picksAndDropsAxeSuccessfully() {
        //The order of commands' execution is FIFO
        gameEngine.executeCommands(pickupAxe, new CommandArgs().withCommand("drop")
                                                                          .withArgument("axe"));
    }
    
    @Test
    public void pickingTwoItemsSequentiallySuccess() {
        location.withItem(items.get("key"));
        
        gameEngine.executeCommands(pickupAxe, new CommandArgs().withCommand("pickup")
                .withArgument("key"));
        
        assertEquals("axe, key", player.getItems());
    }
    
    @Test
    public void droppingItemAddsItToLocation() {
        gameEngine.executeCommands(pickupAxe);
        
        assertEquals("", location.getItems());
        
        gameEngine.executeCommands(new CommandArgs().withCommand("drop").withArgument("axe"));
        
        assertEquals("axe", location.getItems());
    }
}
