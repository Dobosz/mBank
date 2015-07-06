package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.models.Transaction;
import pl.dobosz.bankproject.scraper.models.json.TransactionPagerJSON;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dobosz on 03.07.15.
 */
public class AccountHistoryStep implements Step {
  private static final String NAVIGATION_TO_HISTORY_CALL_URL = "https://online.mbank.pl/pl/MyDesktop/Desktop/SetNavigationToAccountHistory";
  private static final String ACCOUNT_HISTORY_CALL_URL = "https://online.mbank.pl/pl/Pfm/TransactionHistory";
  private static final String HISTORY_INDEX_CALL_URL = "https://online.mbank.pl/pl/Pfm/History/Index";
  private static final String TRANSACTION_LIST_CALL_URL = "https://online.mbank.pl/pl/Pfm/TransactionHistory/TransactionList";
  private static final String TRANSACTION_DETALS_CALL_URL = "https://online.mbank.pl/pl/Pfm/Transaction/Details";
  private static final String ACCOUNT_NUMBER_PARAMETER_NAME = "accountNumber";

  private String accountNumber;
  private Date from;
  private Date to;
  private String productId;

  private List<Transaction> transactionList = new ArrayList<Transaction>();

  NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("pl-PL"));

  private Transaction previousLastTransaction = null;

  public AccountHistoryStep(String accountNumber, Date from, Date to) {
    this.accountNumber = accountNumber;
    this.from = from;
    this.to = to;
  }

  public void execute(WebConversation webConversation) throws RuntimeException, IOException, SAXException, ParseException {
    sendFirstStage(webConversation, accountNumber);
    sendSecondStage(webConversation);
    sendThirdStage(webConversation);

    TransactionPagerJSON transactionPagerJSON = new TransactionPagerJSON(productId, from, to);
    ObjectMapper objectMapper = new ObjectMapper();

    sendFourthStage(webConversation, objectMapper.writeValueAsString(transactionPagerJSON));
  }

  private void sendFirstStage(WebConversation webConversation, String accountNumber) throws IOException, SAXException {
    WebRequest webRequest = new PostMethodWebRequest(NAVIGATION_TO_HISTORY_CALL_URL);
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    webRequest.setParameter(ACCOUNT_NUMBER_PARAMETER_NAME, accountNumber);
    webConversation.getResponse(webRequest);
  }

  private void sendSecondStage(WebConversation webConversation) throws IOException, SAXException {
    WebRequest webRequest = new GetMethodWebRequest(HISTORY_INDEX_CALL_URL);
    webConversation.getResponse(webRequest);
  }

  private void sendThirdStage(WebConversation webConversation) throws IOException, SAXException {
    WebRequest webRequest = new GetMethodWebRequest(ACCOUNT_HISTORY_CALL_URL);
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    productId = parseThirdStageForProductId(response);
  }

  private void sendFourthStage(WebConversation webConversation, String encodedData) throws IOException, SAXException, ParseException {
    InputStream inputStream = new ByteArrayInputStream(encodedData.getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(TRANSACTION_LIST_CALL_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    parseFourthStageForTransactions(response, from, webConversation);
  }

  private void sendFourthStage(WebConversation webConversation, Date from, Date to) throws IOException, SAXException, ParseException {
    TransactionPagerJSON transactionPagerJSON = new TransactionPagerJSON(productId, from, to);
    ObjectMapper objectMapper = new ObjectMapper();
    String encodedData = objectMapper.writeValueAsString(transactionPagerJSON);
    InputStream inputStream = new ByteArrayInputStream(encodedData.getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(TRANSACTION_LIST_CALL_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    parseFourthStageForTransactions(response, from, webConversation);
  }

  private String parseThirdStageForProductId(String response) {
    Document document = Jsoup.parse(response);
    Element filterChecker = document.select(".checkbox-area.checked").first();
    Element productIdInput = filterChecker.select("input[value]").first();
    String productId = productIdInput.attr("value");
    return productId;
  }

  private void parseFourthStageForTransactions(String response, Date from, WebConversation webConversation) throws ParseException, IOException, SAXException {
    Document document = Jsoup.parse(response);
    Elements transactionElement = document.select(".content-list-row.collapsed");
    Elements columnsElement = transactionElement.select("header.content-list-row-header");

    Transaction lastTransaction = null;
    for(Element transactionRow : columnsElement) {
      Transaction transaction = parseForTransactionData(transactionRow, webConversation);
      transactionList.add(transaction);
      lastTransaction = transaction;
    }

    if(lastTransaction != null && !lastTransaction.equals(previousLastTransaction)) {
      previousLastTransaction = lastTransaction;
      sendFourthStage(webConversation, from, lastTransaction.transactionOn);
    } else {
      if(transactionList.size() > 0)
        transactionList.remove(transactionList.size() - 1);
    }
  }

  private Transaction parseForTransactionData(Element transactionRow, WebConversation webConversation) throws ParseException, IOException, SAXException {
    Transaction transaction = new Transaction();

    transaction.uid = transactionRow.parents().attr("data-id");

    Element amountElement = transactionRow.select("div.column.amount").first();
    String amountElementValue = amountElement.select("strong").first().text();
    BigDecimal amount = new BigDecimal(numberFormat.parse(amountElementValue).doubleValue(), MathContext.DECIMAL32);
    transaction.currencyAmount = amount;

    Element descriptionElement = transactionRow.select("div.column.description").first();
    String description = descriptionElement.select("span.label").first().text();
    transaction.title = description;

    Element dateElement = transactionRow.select("div.column.date").first();
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    Date date = format.parse(dateElement.text());
    transaction.transactionOn = date;

    Element categoryElement = transactionRow.select("div.column.category").first();
    String category = categoryElement.select("span.text").text();
    transaction.kind = category;

    sendForTransactionDetails(webConversation, transaction);

    return transaction;
  }

  private void sendForTransactionDetails(WebConversation webConversation, Transaction transaction) throws IOException, SAXException, ParseException {
    WebRequest webRequest = new GetMethodWebRequest(TRANSACTION_DETALS_CALL_URL);
    webRequest.setParameter("transactionId", transaction.uid);
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    ParseForTransactionDetails(response, transaction);
  }

  private static final String AFTER_BALANCE_HEADER_NAME = "Saldo po operacji";
  private static final String RECIPIENT_ACCOUNT_HEADER_NAME = "Rachunek odbiorcy";
  private static final String SENDER_ACCOUNT_HEADER_NAME = "Rachunek nadawcy";

  private void ParseForTransactionDetails(String response, Transaction transaction) throws ParseException {
    Document document = Jsoup.parse(response);

    Element currencyBalanceHeaderElement = document.select("th:contains(" + AFTER_BALANCE_HEADER_NAME + ")").first();
    Element currencyBalanceElement = currencyBalanceHeaderElement.parent().select("td").first();
    String currencyBalanceValue = currencyBalanceElement.text();
    currencyBalanceValue = currencyBalanceValue.substring(0, currencyBalanceValue.length() -4).replace(",", ".").replace(" ","");
    BigDecimal currencyBalance = new BigDecimal(numberFormat.parse(currencyBalanceValue).doubleValue(), MathContext.DECIMAL32);
    transaction.currencyBalance = currencyBalance;

    Element partyIbanHeaderElement = null;
    if(transaction.currencyAmount.intValue() < 0)
      partyIbanHeaderElement = document.select("th:contains(" + RECIPIENT_ACCOUNT_HEADER_NAME + ")").first();
    else
      partyIbanHeaderElement = document.select("th:contains(" + SENDER_ACCOUNT_HEADER_NAME +")").first();

    if(partyIbanHeaderElement != null) {
      Element partyIbanRecordElement = partyIbanHeaderElement.parent();
      Element partyIbanElement = partyIbanRecordElement.select("td").first();
      String partyIban = partyIbanElement.text();
      transaction.partyIban = partyIban.replace(" ","");
    }
  }

  public List<Transaction> getTransactionList() {
    return transactionList;
  }
}
