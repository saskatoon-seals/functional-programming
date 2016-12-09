package uncheckedexceptions;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import common.Register;

public class Client {
  public static final int MAX_ADDRESS = 30;
  public static final Logger LOG = Logger.getLogger("Register access");

  /**
   * Update a set of registers
   *
   * @param stream - stream of register addresses
   * @return number of successfully updated registers
   */
  private static long update(IntStream stream) {
    /*
     * Annoying filtering of possible null values must be written. It can also be forgotten,
     * which will cause NullPointerException.
     */
    return stream.mapToObj(Client::read)
                 .filter(register -> register != null)
                 .map(Client::write)
                 .filter(Predicate.isEqual(true))
                 .count();
  };

  //Every exception needs to be handled at the client level. Compiler want warn you about it.
  private static Register read(int address) {
    try {
      return RegisterAccess.read(address);
    } catch (Exception e) {
      LOG.fatal(
          String.format("Updating register on address %d failed.", address),
          e);
    }

    return null;
  }

  private static boolean write(Register register) {
    try {
      RegisterAccess.write(register);
    } catch (Exception e) {
      LOG.error(
          String.format("Writing register on address %d failed.",
                        register.address));
    }

    return false;
  }


  public static void main(String... args) {
    PropertyConfigurator.configure(".settings/log4j.properties");

    LOG.info("\nNumber of successful updates is: " +
             update(IntStream.range(0, MAX_ADDRESS)));
  }
}
