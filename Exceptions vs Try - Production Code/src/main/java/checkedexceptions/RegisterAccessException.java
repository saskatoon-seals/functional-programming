package checkedexceptions;

import java.io.IOException;

public class RegisterAccessException extends Exception {

	public RegisterAccessException(Exception e) {
		super(e);
	}

}
