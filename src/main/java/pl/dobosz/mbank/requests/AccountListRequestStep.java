package pl.dobosz.mbank.requests;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import pl.dobosz.mbank.Context;
import pl.dobosz.mbank.HttpsURLConnectionFactory;
import pl.dobosz.mbank.Utils;
import pl.dobosz.mbank.models.Account;
import pl.dobosz.mbank.models.json.Accounts;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dobosz on 02.07.15.
 */
public class AccountListRequestStep implements RequestStep {
    private static final String CALL_URL = "https://online.mbank.pl/pl/MyDesktop/Desktop/GetAccountsList";
    private static final String CALL_METHOD = "POST";

    Accounts accounts = null;

    public void execute(Context context) throws RuntimeException {
        String json = sendPost(context);
        ObjectMapper objectMapper = new ObjectMapper();
        //Disable crash on unknown properties
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            accounts = objectMapper.readValue(json, Accounts.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(accounts == null || accounts.getAccountDetailsList().size() == 0)
            throw new NoAccountsException();
    }

    public void responseValidator(String response) {
        if(response == null || response.isEmpty())
            throw new ResponseEmptyException();
    }

    private String sendPost(Context context) {
        HttpsURLConnection httpsURLConnection = HttpsURLConnectionFactory.getHttpsURLConnection(CALL_METHOD, CALL_URL, true);

        //setCookie
        httpsURLConnection.addRequestProperty("Cookie", "mBank2="+context.getToken()+"; mBank_tabId="+context.getTabId());
        //set X-RequestStep-Verification-Token header
        httpsURLConnection.addRequestProperty("X-Request-Verification-Token", context.getVerificationToken());
        //set X-Tab-Id header
        httpsURLConnection.addRequestProperty("X-Tab-Id", context.getTabId());

        httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(2));

        String response = "";
        try {
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            outputStream.write("{ }".getBytes());
            response = Utils.buildResponse(httpsURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseValidator(response);

        return response;

    }

    public List<Account> getAccounts() {
        List<Account> accountList = new ArrayList<Account>();
        //Mapping
        for(Accounts.Account accountJson : accounts.getAccountDetailsList()) {
            Account account = new Account();
            account.setProductName(accountJson.getProductName());
            account.setAccountNumber(accountJson.getAccountNumber());

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("pl-PL"));
            BigDecimal balance = null;
            try {
                balance = new BigDecimal(numberFormat.parse(accountJson.getBalance()).doubleValue(), MathContext.DECIMAL32);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            account.setBalance(balance);
            accountList.add(account);
        }

        return accountList;
    }
}
