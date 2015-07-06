package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.*;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.models.Account;
import pl.dobosz.bankproject.client.Exceptions;
import pl.dobosz.bankproject.scraper.models.json.AccountsJSON;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dobosz on 02.07.15.
 */
public class AccountListStep implements Step {
  private static final String CALL_URL = "https://online.mbank.pl/pl/MyDesktop/Desktop/GetAccountsList";

  private AccountsJSON accountsJSON = null;

  NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("pl-PL"));

  public void execute(WebConversation webConversation) throws RuntimeException, IOException, SAXException {
    String json = sendPost(webConversation);
    ObjectMapper objectMapper = new ObjectMapper();
    //Disable crash on unknown properties
    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    accountsJSON = objectMapper.readValue(json, AccountsJSON.class);
    if (accountsJSON == null || accountsJSON.getAccountDetailsList().size() == 0)
      throw new Exceptions.NoAccoundsException();
  }

  public void responseValidator(String response) {
    if (response == null || response.isEmpty())
      throw new Exceptions.UnknowScrapeException();
  }

  private String sendPost(WebConversation webConversation) throws IOException, SAXException {
    InputStream inputStream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(CALL_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    webRequest.setHeaderField("Accept", "application/json, text/javascript, */*; q=0.01");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    responseValidator(response);

    return response;
  }

  public List<Account> getAccounts() throws ParseException {
    List<Account> accountList = new ArrayList<Account>();
    //Mapping
    for (AccountsJSON.Account accountJson : accountsJSON.getAccountDetailsList()) {
      Account account = new Account();
      account.productName = accountJson.productName;
      account.accountNumber = accountJson.accountNumber;

      BigDecimal balance = new BigDecimal(numberFormat.parse(accountJson.balance).doubleValue(), MathContext.DECIMAL32);

      account.balance = balance;
      accountList.add(account);
    }
    return accountList;
  }
}
