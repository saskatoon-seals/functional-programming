package game;

/*
 * This interface is identical to java.util.Supplier, but I wanted to make it 
 * explicit so it's a command interface with an execute method.
 */
@FunctionalInterface
public interface Command <T extends Object> {

    T execute();
}
