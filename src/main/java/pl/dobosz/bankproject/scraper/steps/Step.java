package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.WebConversation;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by dobosz on 07.07.15.
 */
public abstract class Step<T> {
  public DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.forLanguageTag("pl-PL"));
  public ObjectMapper objectMapper = new ObjectMapper();

  public WebConversation webConversation;

  public Step(WebConversation webConversation) {
    this.webConversation = webConversation;
    decimalFormat.setParseBigDecimal(true);
  }

  public abstract T execute() throws IOException, SAXException, ParseException;
}
