package pl.dobosz.bankproject.scraper;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.models.Account;
import pl.dobosz.bankproject.client.models.Credentials;
import pl.dobosz.bankproject.client.models.Transaction;
import pl.dobosz.bankproject.scraper.steps.AccountHistoryStep;
import pl.dobosz.bankproject.scraper.steps.AccountListStep;
import pl.dobosz.bankproject.scraper.steps.LoginStep;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by dobosz on 03.07.15.
 */
public class MBank {
  private WebConversation webConversation = new WebConversation();
  private Credentials credentials = null;
  private boolean loggedIn = false;

  public MBank(Credentials credentials) {
    this.credentials = credentials;
    //JavaScripts are not needed
    HttpUnitOptions.setScriptingEnabled(false);
  }

  public MBank(Credentials credentials, WebConversation webConversation) {
    this(credentials);
    this.webConversation = webConversation;
  }

  private void signIn() throws IOException, SAXException {
    new LoginStep(credentials).execute(webConversation);
  }

  public List<Account> fetchAccountList() throws ParseException, IOException, SAXException {
    if(!loggedIn)
      signIn();
    AccountListStep accountListStep = new AccountListStep();
    accountListStep.execute(webConversation);
    return accountListStep.getAccounts();
  }

  public List<Transaction> fetchAccountHistory(String accountNumber, Date from, Date to) throws IOException, SAXException, ParseException {
    if(!loggedIn)
      signIn();
    AccountHistoryStep accountHistoryStep = new AccountHistoryStep(accountNumber, from, to);
    accountHistoryStep.execute(webConversation);
    return accountHistoryStep.getTransactionList();
  }
}
