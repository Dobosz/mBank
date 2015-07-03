package pl.dobosz.mbank;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by dobosz on 02.07.15.
 */
public class HttpsURLConnectionFactory {

    private HttpsURLConnectionFactory() {}

    public static HttpsURLConnection getHttpsURLConnection(String method, String link, boolean json) {
        try {
            URL url = new URL(link);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            httpsURLConnection.setRequestMethod(method);

            if(json) {
                httpsURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                httpsURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                httpsURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            }
            else
                httpsURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            httpsURLConnection.setRequestProperty("Referer", "https://online.mbank.pl/pl/Login");
            httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0");

            httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            if(method.equalsIgnoreCase("POST"))
                httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);

            return httpsURLConnection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
