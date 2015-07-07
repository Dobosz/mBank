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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dobosz on 03.07.15.
 */
public class AccountHistoryStep extends Step<List<Transaction>> {
  private static final String NAVIGATION_TO_HISTORY_CALL_URL = "https://online.mbank.pl/pl/MyDesktop/Desktop/SetNavigationToAccountHistory";
  private static final String ACCOUNT_HISTORY_CALL_URL = "https://online.mbank.pl/pl/Pfm/TransactionHistory";
  private static final String HISTORY_INDEX_CALL_URL = "https://online.mbank.pl/pl/Pfm/History/Index";
  private static final String TRANSACTION_LIST_CALL_URL = "https://online.mbank.pl/pl/Pfm/TransactionHistory/TransactionList";
  private static final String TRANSACTION_DETAILS_CALL_URL = "https://online.mbank.pl/pl/Pfm/Transaction/Details";
  private static final String ACCOUNT_NUMBER_PARAMETER_NAME = "accountNumber";

  private String accountNumber;
  private Date from;
  private Date to;
  private String productId;

  private List<Transaction> transactionList = new ArrayList<Transaction>();
  private Transaction previousLastTransaction = null;

  private int transactionCounter = 0;

  public AccountHistoryStep(WebConversation webConversation, String accountNumber, Date from, Date to) {
    super(webConversation);
    this.accountNumber = accountNumber;
    this.from = from;
    this.to = to;
  }

  @Override
  public List<Transaction> execute() throws IOException, SAXException, ParseException {
    setupAccountNumber(accountNumber);
    setupAccountIndex();
    getTransactionHistory();
    TransactionPagerJSON transactionPagerJSON = new TransactionPagerJSON(productId, from, to);
    getTransactionHistoryWithFilter(objectMapper.writeValueAsString(transactionPagerJSON));
    System.out.println(" Done");
    return getTransactionList();
  }

  private void setupAccountNumber(String accountNumber) throws IOException, SAXException {
    WebRequest webRequest = new PostMethodWebRequest(NAVIGATION_TO_HISTORY_CALL_URL);
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    webRequest.setParameter(ACCOUNT_NUMBER_PARAMETER_NAME, accountNumber);
    webConversation.getResponse(webRequest);
  }

  private void setupAccountIndex() throws IOException, SAXException {
    WebRequest webRequest = new GetMethodWebRequest(HISTORY_INDEX_CALL_URL);
    webConversation.getResponse(webRequest);
  }

  private void getTransactionHistory() throws IOException, SAXException {
    WebRequest webRequest = new GetMethodWebRequest(ACCOUNT_HISTORY_CALL_URL);
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    productId = parseThirdStageForProductId(response);
  }

  private void getTransactionHistoryWithFilter(String encodedData) throws IOException, SAXException, ParseException {
    InputStream inputStream = new ByteArrayInputStream(encodedData.getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(TRANSACTION_LIST_CALL_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    parseFourthStageForTransactions(response, from);
  }

  private void getTransactionHistoryWithFilter(Date from, Date to) throws IOException, SAXException, ParseException {
    TransactionPagerJSON transactionPagerJSON = new TransactionPagerJSON(productId, from, to);
    ObjectMapper objectMapper = new ObjectMapper();
    String encodedData = objectMapper.writeValueAsString(transactionPagerJSON);
    getTransactionHistoryWithFilter(encodedData);
  }


  private String parseThirdStageForProductId(String response) {
    Document document = Jsoup.parse(response);
    Element filterChecker = document.select(".checkbox-area.checked").first();
    Element productIdInput = filterChecker.select("input[value]").first();
    return productIdInput.attr("value");
  }

  private void parseFourthStageForTransactions(String response, Date from) throws ParseException, IOException, SAXException {
    Document document = Jsoup.parse(response);
    Elements transactionElement = document.select(".content-list-row.collapsed");
    Elements columnsElement = transactionElement.select("header.content-list-row-header");

    Transaction lastTransaction = null;
    for (Element transactionRow : columnsElement) {
      Transaction transaction = parseForTransactionData(transactionRow, webConversation);
      transactionList.add(transaction);
      lastTransaction = transaction;
      System.out.print("\rFetching: " + transactionCounter);
      transactionCounter++;
    }
    if (transactionList.size() > 0) {
      if (previousLastTransaction == null || (lastTransaction != null && !lastTransaction.uid.equals(previousLastTransaction.uid))) {
        previousLastTransaction = lastTransaction;
        getTransactionHistoryWithFilter(from, lastTransaction.transactionOn);
      } else
        transactionList.remove(transactionList.size() - 1);
    }
  }

  private Transaction parseForTransactionData(Element transactionRow, WebConversation webConversation) throws ParseException, IOException, SAXException {
    Transaction transaction = new Transaction();

    transaction.uid = transactionRow.parents().attr("data-id");

    Element amountElement = transactionRow.select("div.column.amount").first();
    String amountElementValue = amountElement.select("strong").first().text();
    BigDecimal amount = (BigDecimal) decimalFormat.parseObject(amountElementValue);
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

    getTransactionDetails(webConversation, transaction);

    return transaction;
  }

  private void getTransactionDetails(WebConversation webConversation, Transaction transaction) throws IOException, SAXException, ParseException {
    WebRequest webRequest = new GetMethodWebRequest(TRANSACTION_DETAILS_CALL_URL);
    webRequest.setParameter("transactionId", transaction.uid);
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String response = webResponse.getText();
    ParseForTransactionDetails(response, transaction);
  }

  private void ParseForTransactionDetails(String response, Transaction transaction) throws ParseException {
    Document document = Jsoup.parse(response);

    Element currencyBalanceHeaderElement = document.select("th:contains(Saldo po operacji)").first();
    Element currencyBalanceElement = currencyBalanceHeaderElement.parent().select("td").first();
    String currencyBalanceValue = currencyBalanceElement.text();
    currencyBalanceValue = trimPLNValue(currencyBalanceValue);
    BigDecimal currencyBalance = (BigDecimal) decimalFormat.parseObject(currencyBalanceValue);
    transaction.currencyBalance = currencyBalance;

    Element partyIbanHeaderElement = null;
    if(transaction.currencyAmount.intValue() < 0)
      partyIbanHeaderElement = document.select("th:contains(Rachunek odbiorcy)").first();
    else
      partyIbanHeaderElement = document.select("th:contains(Rachunek nadawcy)").first();

    if(partyIbanHeaderElement != null) {
      Element partyIbanRecordElement = partyIbanHeaderElement.parent();
      Element partyIbanElement = partyIbanRecordElement.select("td").first();
      String partyIban = partyIbanElement.text();
      transaction.partyIban = partyIban.replace(" ","");
    }
  }

  private String trimPLNValue(String currencyBalanceValue) {
    return currencyBalanceValue.substring(0, currencyBalanceValue.length() -4).replace(",", ".").replace(" ","");
  }

  private List<Transaction> getTransactionList() {
    return transactionList;
  }
}
