package checkedexceptions;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Client {
  public static final int MAX_ADDRESS = 30;
  public static final Logger LOG = Logger.getLogger("Register access");

  private static long update() {
    int count = 0;

    for (int address = 0; address < MAX_ADDRESS; address++) {
      try {
        RegisterAccess.write(
            RegisterAccess.read(address));

        count++;
      } catch (RegisterAccessException e) {
        LOG.error(
            String.format("Updating register on address %d failed.",
                          address));
      } catch (Exception e) {
        LOG.fatal(
            String.format("Updating register on address %d failed.", address),
            e);
      }
    }

    return count;
  };

  public static void main(String... args) {
    PropertyConfigurator.configure(".settings/log4j.properties");

    LOG.info("\nNumber of successful updates is: " +
             update());
  }
}
