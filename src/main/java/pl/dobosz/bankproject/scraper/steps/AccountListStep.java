package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.*;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.models.Account;
import pl.dobosz.bankproject.scraper.models.json.AccountsJSON;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dobosz on 02.07.15.
 */
public class AccountListStep extends Step<List<Account>> {
  private static final String CALL_URL = "https://online.mbank.pl/pl/MyDesktop/Desktop/GetAccountsList";

  private AccountsJSON accountsJSON = null;

  public AccountListStep(WebConversation webConversation) {
    super(webConversation);
  }

  @Override
  public List<Account> execute() throws IOException, SAXException, ParseException {
    String json = sendPost(webConversation);
    ObjectMapper objectMapper = new ObjectMapper();
    DisableCrashOnUnknownFields(objectMapper);
    accountsJSON = objectMapper.readValue(json, AccountsJSON.class);
    return getAccounts();
  }

  private void DisableCrashOnUnknownFields(ObjectMapper objectMapper) {
    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private String sendPost(WebConversation webConversation) throws IOException, SAXException {
    InputStream inputStream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(CALL_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    webRequest.setHeaderField("Accept", "application/json, text/javascript, */*; q=0.01");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String json = webResponse.getText();
    return json;
  }

  private List<Account> getAccounts() throws ParseException {
    List<Account> accountList = new ArrayList<Account>();
    for (AccountsJSON.Account accountJson : accountsJSON.getAccountDetailsList()) {
      Account account = new Account();
      account.productName = accountJson.productName;
      account.accountNumber = accountJson.accountNumber;
      account.balance = (BigDecimal) decimalFormat.parseObject(accountJson.balance);
      accountList.add(account);
    }
    return accountList;
  }
}
