package pl.dobosz.mbank.models.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by dobosz on 02.07.15.
 */
public class Accounts {

    public static class Account {
        @JsonProperty("ProductName")
        private String productName;
        @JsonProperty("AccountNumber")
        private String accountNumber;
        @JsonProperty("Balance")
        private String balance;


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

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }

    @JsonProperty("accountDetailsList")
    private ArrayList<Account> accountDetailsList;

    public ArrayList<Account> getAccountDetailsList() {
        return accountDetailsList;
    }

    public void setAccountDetailsList(ArrayList<Account> accountDetailsList) {
        this.accountDetailsList = accountDetailsList;
    }
}
