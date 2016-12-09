package trymonad;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
    return stream.mapToObj(update)
                 .filter(Predicate.isEqual(true))
                 .count();
  };

  /*
   * Handling exception is optionally. You can also let stream proceed it's work because
   * it won't be interrupted.
   */
  private static IntFunction<Boolean> update = address -> {
      return RegisterAccess.read(address)
                           .flatMap(RegisterAccess::write)
                           .onFailure(e -> Client.handleException(e, address))
                           .isPresent();
  };

  private static void handleException(Exception exception, Integer address) {
    LOG.error(
        String.format("Updating register on address %d failed.", address));

    if (exception.getCause() instanceof RuntimeException)
      LOG.fatal(
          String.format("Updating register on address %d failed.", address),
          exception);
  }

  public static void main(String... args) {
    PropertyConfigurator.configure(".settings/log4j.properties");

    LOG.info("\nNumber of successful updates is: " +
             update(IntStream.range(0, MAX_ADDRESS)));
  }
}
