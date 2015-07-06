package pl.dobosz.bankproject.scraper.models.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

/**
 * Created by dobosz on 02.07.15.
 */
public class AccountsJSON {

  public static class Account {
    @JsonProperty("ProductName")
    public String productName;
    @JsonProperty("AccountNumber")
    public String accountNumber;
    @JsonProperty("Balance")
    public String balance;
  }

  @JsonProperty("accountDetailsList")
  private ArrayList<Account> accountDetailsList;

  public ArrayList<Account> getAccountDetailsList() {
    return accountDetailsList;
  }
}
