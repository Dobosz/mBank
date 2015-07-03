package pl.dobosz.mbank.requests;

import pl.dobosz.mbank.Context;

/**
 * Created by dobosz on 03.07.15.
 */
public interface RequestStep {
    void execute(Context context) throws RuntimeException;
    void responseValidator(String response);

    class LoginFailedException extends RuntimeException { }
    class NoTokenException extends RuntimeException { }
    class NoAccountsException extends RuntimeException { }
    class ResponseEmptyException extends RuntimeException { }
}
