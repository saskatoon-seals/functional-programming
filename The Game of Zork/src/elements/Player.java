package elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import exceptions.MissingItemException;

public class Player {
    private final List<Item> items = new ArrayList<>();
    private Location currentLocation;
    
    public Player(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public String getItems() {
        return items.stream()
                    .map(Item::getName)
                    .collect(Collectors.joining(", "));
    }
    
    public String pickup(List<String> arguments) {
        if (arguments.size() != 1)
            throw new IllegalArgumentException("Can pick only a single item.");
        
        items.add(currentLocation.pickItem(arguments.get(0))
                                 .orElseThrow(() -> new MissingItemException()));
      
        return String.format("Picked %s.", arguments.get(0));
    }
    
    public String drop(List<String> arguments) {
        if (arguments.size() != 1)
            throw new IllegalArgumentException("Can drop only a single item.");
        
        items.stream()
             .filter(item -> item.getName().equals(arguments.get(0)))
             .findAny()
             .orElseThrow(MissingItemException::new);
        
        return String.format("Dropped %s.", arguments.get(0));
    }
}
