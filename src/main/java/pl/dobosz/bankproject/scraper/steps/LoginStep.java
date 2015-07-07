package pl.dobosz.bankproject.scraper.steps;

import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.exceptions.Exceptions;
import pl.dobosz.bankproject.client.models.Credentials;
import pl.dobosz.bankproject.scraper.models.json.LoginJSON;
import org.codehaus.jackson.node.ObjectNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Created by dobosz on 01.07.15.
 */
public class LoginStep extends Step<Void> {

  private static final String FIRST_STAGE_URL = "https://online.mbank.pl/pl/LoginMain/Account/JsonLogin";
  private static final String TAB_ID_COOKIE_NAME = "mBank_tabId";
  private static final String TAB_ID_HEADER_NAME = "X-Tab-Id";
  private static final String TOKEN_NAME = "__AjaxRequestVerificationToken";
  private static final String SECOND_STAGE_URL = "https://online.mbank.pl/pl";
  private static final String HEADER_TOKEN_NAME = "X-Request-Verification-Token";

  private Credentials credentials;

  public LoginStep(WebConversation webConversation, Credentials credentials) {
    super(webConversation);
    this.credentials = credentials;
  }

  @Override
  public Void execute() throws IOException, SAXException, ParseException {
    LoginJSON loginJSON = new LoginJSON();
    loginJSON.username = credentials.login;
    loginJSON.password = credentials.password;
    sendLogIn(objectMapper.writeValueAsString(loginJSON));
    String token = getMetaToken();
    webConversation.setHeaderField(HEADER_TOKEN_NAME, token);
    return null;
  }

  private void sendLogIn(String encodedData) throws RuntimeException, IOException, SAXException {
    InputStream inputStream = new ByteArrayInputStream(encodedData.getBytes(StandardCharsets.UTF_8));
    WebRequest webRequest = new PostMethodWebRequest(FIRST_STAGE_URL, inputStream, "application/json;charset=utf-8");
    webRequest.setHeaderField("X-Requested-With", "XMLHttpRequest");
    webRequest.setHeaderField("Accept", "application/json, text/javascript, */*; q=0.01");
    webRequest.setHeaderField("Referer", "https://online.bankproject.pl/pl/Login");
    WebResponse webResponse = webConversation.getResponse(webRequest);
    webConversation.setHeaderField(TAB_ID_HEADER_NAME, webConversation.getCookieValue(TAB_ID_COOKIE_NAME));
    String json = webResponse.getText();
    assertJSONContainsSuccessfulTrue(json);
  }

  public void assertJSONContainsSuccessfulTrue(String json) throws IOException {
    //Check if return "successful":true
    ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
    if (!node.has("successful") || !Boolean.valueOf(node.get("successful").toString()))
      throw new Exceptions.LoginFailedException();
  }

  public String getMetaToken() throws IOException, SAXException {
    WebRequest webRequest = new GetMethodWebRequest(SECOND_STAGE_URL);
    WebResponse webResponse = webConversation.getResponse(webRequest);
    String html = webResponse.getText();
    Document document = Jsoup.parse(html);
    Elements meta = document.select("meta[name=" + TOKEN_NAME + "]");
    return meta.first().attr("content");
  }
}
