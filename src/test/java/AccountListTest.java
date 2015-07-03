import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import pl.dobosz.mbank.Constants;
import pl.dobosz.mbank.Context;
import pl.dobosz.mbank.StepExecutor;
import pl.dobosz.mbank.models.Account;
import pl.dobosz.mbank.models.Credentials;
import pl.dobosz.mbank.models.json.Accounts;
import pl.dobosz.mbank.requests.RequestStep;

import java.util.List;

/**
 * Created by dobosz on 03.07.15.
 */
public class AccountListTest {

    @Test
    public void ListFetchOk() {
        Context context = new Context();
        StepExecutor stepExecutor = new StepExecutor(new Credentials(Constants.USERNAME, Constants.PASSWORD));
        stepExecutor.executeAll(context);
        List<Account> accountJSONList = stepExecutor.fatchAccountList();
        Assert.assertEquals("Account name must be \"eKonto\"", "eKonto", accountJSONList.get(0).getProductName());
        Assert.assertEquals("Account Nr must be \"87 1140 2004 0000 3502 6270 4040\"", "87 1140 2004 0000 3502 6270 4040", accountJSONList.get(0).getAccountNumber());
        Assert.assertEquals("Account name must be \"eMax plus\"", "eMax plus", accountJSONList.get(1).getProductName());
        Assert.assertEquals("Account Nr must be \"54 1140 2004 0000 3702 6270 4051\"", "54 1140 2004 0000 3702 6270 4051", accountJSONList.get(1).getAccountNumber());
    }

    @Test(expected = RequestStep.LoginFailedException.class)
    public void ListFetchWrongCredentials() {
        Context context = new Context();
        StepExecutor stepExecutor = new StepExecutor(new Credentials("user", "password"));
        stepExecutor.executeAll(context);
        List<Account> accountJSONList = stepExecutor.fatchAccountList();
        Assert.assertEquals("Account name must be \"eKonto\"", "eKonto", accountJSONList.get(0).getProductName());
        Assert.assertEquals("Account Nr must be \"87 1140 2004 0000 3502 6270 4040\"", "87 1140 2004 0000 3502 6270 4040", accountJSONList.get(0).getAccountNumber());
        Assert.assertEquals("Account name must be \"eMax plus\"", "eMax plus", accountJSONList.get(1).getProductName());
        Assert.assertEquals("Account Nr must be \"54 1140 2004 0000 3702 6270 4051\"", "54 1140 2004 0000 3702 6270 4051", accountJSONList.get(1).getAccountNumber());
    }

}
