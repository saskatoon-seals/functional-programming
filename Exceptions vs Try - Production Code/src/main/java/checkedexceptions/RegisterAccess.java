package checkedexceptions;

import java.io.IOException;

import common.Microcontroller;
import common.Register;

//Middleware layer written by a developer.
public class RegisterAccess {

  /**
   * Reads a register on a given address
   *
   * @param address - address
   * @return register
   * @throws RegisterAccessException - exception
   */
  public static Register read(int address) throws RegisterAccessException {
    try {
      return Microcontroller.read(address);
    } catch (IOException e) {
      throw new RegisterAccessException(e);
    }
    //Intentionally forgot to catch a runtime exception.
  }

  /**
   * Writes a value of a given register
   *
   * @param register - register with a value to write
   * @throws RegisterAccessException - exception
   */
  public static void write(Register register) throws RegisterAccessException {
    try {
      Microcontroller.write(register);
    } catch (IOException e) {
      throw new RegisterAccessException(e);
    } catch (IllegalArgumentException e) {
      /*
       * The exception thrown by Microcontroller is a runtime exception, so this catch can be
       * "forgotten".
       * If it is "forgotten", the upper transaction will break, even if only one out
       * of N operations fails.
       */
      throw new RegisterAccessException(e);
    }
  }
}
