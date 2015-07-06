package pl.dobosz.bankproject.client;

/**
 * Created by dobosz on 03.07.15.
 */
public interface Exceptions {
  class LoginFailedException extends RuntimeException {}
  class NoAccoundsException extends RuntimeException {}
  class UnknowScrapeException extends RuntimeException {}
}
