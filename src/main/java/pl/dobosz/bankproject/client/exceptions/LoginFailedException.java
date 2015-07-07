package pl.dobosz.bankproject.client.exceptions;

/**
 * Created by dobosz on 07.07.15.
 */
public class LoginFailedException extends RuntimeException {
  public LoginFailedException() {
    super("Invalid login or password");
  }
}
