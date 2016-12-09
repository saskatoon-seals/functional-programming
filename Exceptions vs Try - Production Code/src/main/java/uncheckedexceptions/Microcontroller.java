package uncheckedexceptions;

import common.Register;

//This class is now modified, in order to demonstrate bubbling up of runtime exception
public class Microcontroller {

  public static final int MIN_ADDRESS = 10;
  public static final int MAX_ADDRESS = 20;

  public static final int LUCKY_ADDRESS = 13;

  public static Register read(int address) {
    if (!validate(address))
      throw new IllegalStateException();

    //Hypothetical read of register on given address.

    return Register.create(address, -2 * address);
  }

  public static Boolean write(Register register) {
    if (!validate(register.address))
      throw new IllegalStateException();

    //Hypothetical flush of register.

    return true;
  }

  private static boolean validate(int address) {
    if (address == LUCKY_ADDRESS) {
      throw new IllegalArgumentException();
    }

    return address > MIN_ADDRESS && address < MAX_ADDRESS;
  }
}
