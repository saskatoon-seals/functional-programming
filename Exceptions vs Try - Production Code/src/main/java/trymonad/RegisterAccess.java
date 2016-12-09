package trymonad;

import java.io.IOException;
import java.util.function.Function;

import com.nextbreakpoint.Try;

import common.Microcontroller;
import common.Register;

//You would write this code, wrapping exception with Tries
public class RegisterAccess {

  /**
   * Reads a register on a given address
   *
   * @param address - address
   * @return try with register on success and IOException on failure
   */
  public static Try<Register, IOException> read(int address) {
    return readHelper.andThen(Util.exceptionMapper())
                     .apply(address);
  }

  private static Function<Integer, Try<Register, Exception>> readHelper = address -> {
    return Try.of(() -> Microcontroller.read(address));
  };

  /**
   * Writes a value of a given register
   *
   * @param register - register with a value to write
   * @return - try with true on success and IOException on failure
   */
  public static Try<Boolean, IOException> write(Register register) {
    return writeHelper.andThen(Util.exceptionMapper())
                      .apply(register);
  }

  private static Function<Register, Try<Boolean, Exception>> writeHelper = register -> {
    return Try.of(() -> Microcontroller.write(register));
  };
}
