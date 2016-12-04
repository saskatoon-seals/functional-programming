package game;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Actual (direct) invoker
public class WorkingQueue {
    private List<Command<String>> commandsQueue = new ArrayList<>();
    
    public WorkingQueue addCommand(Command<String> command) {
        commandsQueue.add(command);
        return this;
    }

    public List<String> executeCommands() {                                
        List<String> results = commandsQueue.stream()
                                            .map(Command::execute)
                                            .collect(Collectors.toList());
        
        commandsQueue.clear();
        
        return results;
    }
}
