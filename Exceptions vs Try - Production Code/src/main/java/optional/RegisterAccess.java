package optional;

import java.util.Optional;

import com.nextbreakpoint.Try;

import common.Microcontroller;
import common.Register;

//You would write this code, wrapping exception with Tries
public class RegisterAccess {

  /**
   * Reads a register on a given address
   *
   * @param address - address
   * @return optional register value or empty
   */
  public static Optional<Register> read(int address) {
    return Try.of(() -> Microcontroller.read(address))
              .value();
  }

  /**
   * Writes a value of a given register
   *
   * @param register - register with a value to write
   * @return - true on success and false on failure
   */
  public static Boolean write(Register register) {
    return Try.of(() -> Microcontroller.write(register))
              .isPresent();
  }
}
