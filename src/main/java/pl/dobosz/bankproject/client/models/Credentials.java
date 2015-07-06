package pl.dobosz.bankproject.client.models;

/**
 * Created by dobosz on 02.07.15.
 */
public class Credentials {
  public String login;
  public String password;

  public Credentials(String login, String password) {
    this.login = login;
    this.password = password;
  }
}
