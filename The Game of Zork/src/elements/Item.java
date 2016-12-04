package elements;

public class Item {
    private String name;
    private String description;
    
    public Item(String name) {
        this(name, "Default item");
    }
    
    public Item(String name, String description){
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
}
