package pl.dobosz.mbank;

import pl.dobosz.mbank.models.Account;
import pl.dobosz.mbank.models.Credentials;
import pl.dobosz.mbank.models.json.Accounts;
import pl.dobosz.mbank.requests.LoginRequestStep;
import pl.dobosz.mbank.requests.VerificationTokenRequestStep;

import java.util.List;

/**
 * Created by dobosz on 01.07.15.
 */
public class Main {
    public static void main(String[] args) throws LoginRequestStep.LoginFailedException, VerificationTokenRequestStep.NoTokenException {

        //App context
        Context context = new Context();

        StepExecutor stepExecutor = new StepExecutor(new Credentials(Constants.USERNAME, Constants.PASSWORD));
        stepExecutor.executeAll(context);
        List<Account> accountList = stepExecutor.fatchAccountList();
        for(Account account : accountList)
            System.out.println(account);
    }
}
