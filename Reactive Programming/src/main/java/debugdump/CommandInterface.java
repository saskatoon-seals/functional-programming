package debugdump;

public interface CommandInterface {

  public String execute(String... args) throws CommandException;
}
