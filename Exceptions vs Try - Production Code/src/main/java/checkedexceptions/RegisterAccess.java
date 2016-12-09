package checkedexceptions;

//@TODO:
//You would write this code, wrapping exception with Tries
public class RegisterAccess {

  public static final int MIN_ADDRESS = 10;
  public static final int MAX_ADDRESS = 20;

  //Read:

//  public static Try<Register, IOException> read(int address) {
//    return readHelper.andThen(Util.exceptionMapper())
//                     .apply(address);
//  }
//
//  //This function should also have read side-effect (request to FPGA)
//  private static Function<Integer, Try<Register, ?>> readHelper = address -> {
//    return Try.of(() -> Microcontroller.read(address));
//  };
//
//  //Write:
//
//  public static Try<Boolean, IOException> write(Register register) {
//    return writeHelper.andThen(Util.exceptionMapper())
//                      .apply(register);
//  }
//
//  //This function should also have write side-effect (request to FPGA)
//  private static Function<Register, Try<Boolean, ?>> writeHelper = register -> {
//    return Try.of(() -> Microcontroller.write(register));
//  };
}
