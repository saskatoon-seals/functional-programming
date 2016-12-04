package elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Location {
    private String name;
    private String description;
    
    List<Item> items = new ArrayList<>();
    
    public Location(String name) {
        this(name, "Default location");
    }
    
    public Location(String name, String description){
        this.name = name;
        this.description = description;
    }
    
    public Location withItem(Item item) {
        items.add(item);
        return this;
    }

    public Optional<Item> pickItem(String itemName) {        
        Optional<Item> pickedItem = getItemByName(itemName);
        pickedItem.ifPresent(item -> {items.remove(item);});
        
        return pickedItem;
    }
    
    private Optional<Item> getItemByName(String itemName) {
        return items.stream()
                    .filter(item -> item.getName().equals(itemName))
                    .findFirst();
    }
    
    public String getItems() {
        return items.stream()
                    .map(Item::getName)
                    .collect(Collectors.joining(", "));
    }
}
