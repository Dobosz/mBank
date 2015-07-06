package pl.dobosz.bankproject.client.models;

import java.math.BigDecimal;

/**
 * Created by dobosz on 03.07.15.
 */
public class Account {
  public String productName;
  public String accountNumber;
  public BigDecimal balance;

  @Override
  public String toString() {
    return productName + "\t\t" + accountNumber + "\t\t" + balance.setScale(2).toPlainString();
  }
}
