package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.WebConversation;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by dobosz on 03.07.15.
 */
public interface Step {
  void execute(WebConversation webConversation) throws RuntimeException, IOException, SAXException, ParseException;
}
