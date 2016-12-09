package trymonad;

public class Register {
  public int address;
  public int value;

  public Register(int address, int value) {
    this.address = address;
    this.value = value;
  }

  public static Register create(int address, int value) {
    return new Register(address, value);
  }

  public static Register create(int address) {
    return create(address, 0);
  }

}
