package trymonad;
import java.io.IOException;

//Class out of our control
public class Microcontroller {

  public static final int MIN_ADDRESS = 10;
  public static final int MAX_ADDRESS = 20;

  public static final int LUCKY_ADDRESS = 13;

  public static Register read(int address) throws IOException {
    if (!validate(address))
      throw new IOException();

    //Hypothetical read of register on given address..

    //Returns a register with a random value
    return Register.create(address, -2 * address); 
  }

  public static Boolean write(Register register) throws IOException {
    if (!validate(register.address))
      throw new IOException();

    //Hypothetical write of register..

    return true;
  }

  private static boolean validate(int address) {
    if (address == LUCKY_ADDRESS) {
      throw new IllegalArgumentException();
    }

    return address > MIN_ADDRESS && address < MAX_ADDRESS;
  }
}
