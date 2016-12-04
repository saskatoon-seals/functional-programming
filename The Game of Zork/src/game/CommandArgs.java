package game;

import java.util.ArrayList;
import java.util.List;

public class CommandArgs {
    
    private String command;
    private List<String> arguments = new ArrayList<>();

    public CommandArgs withCommand(String token) {
        command = token;
        return this;
    }
    
    public CommandArgs withArgument(String token) {
        arguments.add(token);
        return this;
    }
    
    public String getCommand() {
        return command;
    }
    
    public List<String> getArguments() {
        return arguments;
    }
}
