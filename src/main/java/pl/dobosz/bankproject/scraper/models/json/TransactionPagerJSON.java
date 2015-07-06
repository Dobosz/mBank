package pl.dobosz.bankproject.scraper.models.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dobosz on 06.07.15.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class TransactionPagerJSON {
  @JsonSerialize
  @JsonProperty("pagesCount")
  public Integer pagesCount = Integer.MAX_VALUE; //Get all
  @JsonSerialize
  @JsonProperty("CategoryId")
  public String categoryId= "0";
  @JsonSerialize
  @JsonProperty("ProductIds")
  public List<String> productIds;
  @JsonSerialize
  @JsonProperty("ShowIrrelevantTransactions")
  public boolean showIrrelevantTransactions = true;
  @JsonSerialize
  @JsonProperty("ShowSavingsAndInvestments")
  public boolean showSavingsAndInvestments = true;
  @JsonSerialize
  @JsonProperty("UseAbsoluteSearch")
  public boolean useAbsoluteSearch = false; //TODO Check it
  @JsonSerialize
  @JsonProperty("SaveShowIrrelevantTransactions")
  public boolean saveShowIrrelevantTransactions = false;
  @JsonSerialize
  @JsonProperty("SaveShowSavingsAndInvestments")
  public boolean saveShowSavingsAndInvestments = false;
  @JsonSerialize
  @JsonProperty("AmountFrom")
  public String amountFrom = null;
  @JsonSerialize
  @JsonProperty("AmountTo")
  public String amountTo = null;
  @JsonSerialize
  @JsonProperty("ShowCreditTransactionTypes")
  public boolean showCreditTransactionTypes = false;
  @JsonSerialize
  @JsonProperty("ShowDebitTransactionTypes")
  public boolean showDebitTransactionTypes = false;
  @JsonSerialize
  @JsonProperty("NavigationSource")
  public String navigationSource = "Cards";
  @JsonSerialize
  @JsonProperty("periodTo")
  public String periodTo;
  @JsonSerialize
  @JsonProperty("periodFrom")
  public String periodFrom;

  public TransactionPagerJSON(String productId, Date from, Date to) {
    productIds = new ArrayList<String>();
    productIds.add(productId);

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    periodFrom = format.format(from);
    periodTo = format.format(to);
  }
}
