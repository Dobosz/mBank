package pl.dobosz.bankproject.client;

import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.exceptions.Exceptions;
import pl.dobosz.bankproject.client.models.Transaction;
import pl.dobosz.bankproject.scraper.MBank;
import pl.dobosz.bankproject.client.models.Account;
import pl.dobosz.bankproject.client.models.Credentials;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dobosz on 01.07.15.
 */
public class Main {
  public static void main(String[] args) throws Exceptions.LoginFailedException, IOException, SAXException, ParseException {
    //FIXME remove proxy
    System.getProperties().put("proxySet", "true");
    System.getProperties().put("proxyHost", "127.0.0.1");
    System.getProperties().put("proxyPort", "8080");

    MBank mbank = new MBank(new Credentials(Constants.USERNAME, Constants.PASSWORD));

    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    List<Account> accountList = mbank.fetchAccountList();
    for (Account account : accountList) {
      System.out.println(account);
      System.out.println("****************************************************************");
      List<Transaction> transactionList = mbank.fetchAccountHistory(account.accountNumber, formatter.parse("01.01.2014"), formatter.parse("01.10.2014"));
      for(Transaction transaction : transactionList)
        System.out.println(transaction);
      System.out.println("****************************************************************");
    }
  }
}
