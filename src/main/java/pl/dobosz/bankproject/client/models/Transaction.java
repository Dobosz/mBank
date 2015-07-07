package pl.dobosz.bankproject.client.models;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dobosz on 06.07.15.
 */
public class Transaction {

  /**
   * (Almost) unique operation identifier in scope of an { @link Account }.
   * <p>
   * Depending on target, the value either comes from the target directly,
   * or is created synthetically based on MoneyTransaction attributes.
   * <p>
   * In the second case it is possible for two transactions to have the same UID.
   * Imagine two ATM withdrawals the same day for the same amount.
   */
  public String uid ;

  /**
   * Amount expressed in transaction currency. Example: 22.34 GBP.
   */
  public BigDecimal currencyAmount ;

  /**
   * Account balance after transaction. It is expressed in account currency.
   * <p>
   * This one is often missing.
   */
  public BigDecimal currencyBalance ;

  /**
   * When the transaction took place.
   */
  public Date transactionOn ;

  /**
   * When the transaction was officially booked on the { @link Account }.
   * This is typically the same or several days later than transactionOn.
   */
  public Date bookedOn ;

  /**
   * Transaction kind as presented in the target system, i.e. "KAPITALIZACJA ODSETEK".
   * This field can be empty.
   */
  public String kind ;

  /**
   * Transaction Description as presented in the target system.
   * This field can be empty.
   */
  public String title ;

  /**
   * The second party of the transaction. For withdrawals it is creditor.
   * For deposits it is debtor. This is typically combined name and address.
   */
  public String party ;

  /**
   * Bank Account Number of the second party of transaction as presented in the target system.
   * <p>
   * Note: This field may hold a bank account account number in non-iban format.
   * This field can be empty.
   */
  public String partyIban ;

  @Override
  public String toString() {
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    return "uid=" + uid +
        ", partyIban=" + partyIban +
        ", currencyBalance=" + currencyBalance +
        ", currencyAmount=" + currencyAmount +
        ", transactionOn=" + formatter.format(transactionOn) +
        ", kind='" + kind + '\'' +
        ", title='" + title + '\'';
  }
}
