package pl.dobosz.mbank.models;

import org.codehaus.jackson.annotate.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by dobosz on 03.07.15.
 */
public class Account {
    private String productName;
    private String accountNumber;
    private BigDecimal balance;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return productName + "\t\t" + accountNumber + "\t\t" + balance.setScale(2).toPlainString();
    }
}
