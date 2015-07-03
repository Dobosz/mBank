package pl.dobosz.mbank.requests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pl.dobosz.mbank.Context;
import pl.dobosz.mbank.HttpsURLConnectionFactory;
import pl.dobosz.mbank.Utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;

/**
 * Created by dobosz on 02.07.15.
 */
public class VerificationTokenRequestStep implements RequestStep {

    private static final String TOKEN_NAME = "__AjaxRequestVerificationToken";
    private static final String CALL_URL = "https://online.mbank.pl/pl";
    private static final String CALL_METHOD = "GET";

    public void execute(Context context) throws RuntimeException {
        String token = sendGET(context);
        context.setVerificationToken(token);
    }

    public String sendGET(Context context) throws NoTokenException {
        HttpsURLConnection httpsURLConnection = HttpsURLConnectionFactory.getHttpsURLConnection(CALL_METHOD, CALL_URL, false);

        //setCookie
        httpsURLConnection.addRequestProperty("Cookie", buildCookieHeader(context));

        String response = null;
        try {
            response = Utils.buildResponse(httpsURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseValidator(response);

        Document document = Jsoup.parse(response);
        Elements meta = document.select("meta[name=" + TOKEN_NAME + "]");
        if(meta.size() == 0)
            throw new NoTokenException();
        String token = meta.first().attr("content");

        return token;

    }

    public void responseValidator(String response) {
        if(response.isEmpty())
            throw new ResponseEmptyException();
    }

    private String buildCookieHeader(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mBank2=");
        stringBuilder.append(context.getToken());
        stringBuilder.append("; "); //delimiter
        stringBuilder.append("mBank_tabId=");
        stringBuilder.append(context.getTabId());
        return stringBuilder.toString();
    }
}
