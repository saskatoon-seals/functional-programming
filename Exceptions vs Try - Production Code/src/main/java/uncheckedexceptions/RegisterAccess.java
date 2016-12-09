package uncheckedexceptions;

import checkedexceptions.RegisterAccessException;
import common.Register;

//It tourns out that the middleware layer isn't needed anymore. It was here only to "handle" checked exceptions.
public class RegisterAccess {

  /**
   * Reads a register on a given address
   *
   * @param address - address
   * @return register
   * @throws RegisterAccessException - exception
   */
  public static Register read(int address) {
    return Microcontroller.read(address);
  }

  /**
   * Writes a value of a given register
   *
   * @param register - register with a value to write
   * @throws RegisterAccessException - exception
   */
  public static void write(Register register) {
    Microcontroller.write(register);
  }
}
