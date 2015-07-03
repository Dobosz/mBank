package pl.dobosz.mbank.requests;

import pl.dobosz.mbank.Context;
import pl.dobosz.mbank.models.Credentials;
import pl.dobosz.mbank.HttpsURLConnectionFactory;
import pl.dobosz.mbank.Utils;
import pl.dobosz.mbank.models.json.Login;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dobosz on 01.07.15.
 */
public class LoginRequestStep implements RequestStep {

    private static final String CALL_URL = "https://online.mbank.pl/pl/LoginMain/Account/JsonLogin";
    private static final String CALL_METHOD = "POST";
    private Credentials credentials;

    public LoginRequestStep(Credentials credentials) {
        this.credentials = credentials;
    }

    public void execute(Context context) throws RuntimeException {
        Login login = new Login();
        login.setUsername(credentials.getLogin());
        login.setPassword(credentials.getPassword());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokens = null;
        try {
            tokens = sendPost(objectMapper.writeValueAsString(login));
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.setToken(tokens.get("mBank2"));
        context.setTabId(tokens.get("mBank_tabId"));
    }

    private Map<String, String> sendPost(String encodedData) throws RuntimeException {
        HttpsURLConnection httpsURLConnection = HttpsURLConnectionFactory.getHttpsURLConnection(CALL_METHOD, CALL_URL, true);
        httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));

        String response = "";
        try {
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            outputStream.write(encodedData.getBytes());
            response = Utils.buildResponse(httpsURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseValidator(response);

        Map<String, String> tokens = new HashMap<String, String>();
        tokens.put("mBank2", Utils.getCookie("mBank2", httpsURLConnection.getHeaderFields()));
        tokens.put("mBank_tabId", Utils.getCookie("mBank_tabId", httpsURLConnection.getHeaderFields()));
        return tokens;
    }


    public void responseValidator(String response) {
        //Check if return "successful":true
        ObjectMapper objectMapper = new ObjectMapper();
        String json = response;
        ObjectNode node = null;
        try {
            node = objectMapper.readValue(json, ObjectNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!node.has("successful") || !Boolean.valueOf(node.get("successful").toString()))
            throw new LoginFailedException();
    }
}
