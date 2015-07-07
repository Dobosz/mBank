import com.meterware.httpunit.WebConversation;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.dobosz.bankproject.client.Constants;
import pl.dobosz.bankproject.client.exceptions.LoginFailedException;
import pl.dobosz.bankproject.scraper.MBank;
import pl.dobosz.bankproject.client.models.Account;
import pl.dobosz.bankproject.client.models.Credentials;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by dobosz on 03.07.15.
 */
public class AccountListTest {

    @Test
    public void ListFetchOk() throws IOException, SAXException, ParseException {
        MBank MBank = new MBank(new Credentials(Constants.USERNAME, Constants.PASSWORD));
        List<Account> accountJSONList = MBank.fetchAccountList();
        Assert.assertEquals("Account name must be \"eKonto\"", "eKonto", accountJSONList.get(0).productName);
        Assert.assertEquals("Account Nr must be \"87 1140 2004 0000 3502 6270 4040\"", "87 1140 2004 0000 3502 6270 4040", accountJSONList.get(0).accountNumber);
        Assert.assertEquals("Account name must be \"eMax plus\"", "eMax plus", accountJSONList.get(1).productName);
        Assert.assertEquals("Account Nr must be \"54 1140 2004 0000 3702 6270 4051\"", "54 1140 2004 0000 3702 6270 4051", accountJSONList.get(1).accountNumber);
    }

    @Test(expected = LoginFailedException.class)
    public void ListFetchWrongCredentials() throws IOException, SAXException, ParseException {
        MBank MBank = new MBank(new Credentials("user", "password"));
        List<Account> accountJSONList = MBank.fetchAccountList();
        Assert.assertEquals("Account name must be \"eKonto\"", "eKonto", accountJSONList.get(0).productName);
        Assert.assertEquals("Account Nr must be \"87 1140 2004 0000 3502 6270 4040\"", "87 1140 2004 0000 3502 6270 4040", accountJSONList.get(0).accountNumber);
        Assert.assertEquals("Account name must be \"eMax plus\"", "eMax plus", accountJSONList.get(1).productName);
        Assert.assertEquals("Account Nr must be \"54 1140 2004 0000 3702 6270 4051\"", "54 1140 2004 0000 3702 6270 4051", accountJSONList.get(1).accountNumber);
    }
}
