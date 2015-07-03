package pl.dobosz.mbank;

import pl.dobosz.mbank.models.Account;
import pl.dobosz.mbank.models.Credentials;
import pl.dobosz.mbank.models.json.Accounts;
import pl.dobosz.mbank.requests.AccountListRequestStep;
import pl.dobosz.mbank.requests.LoginRequestStep;
import pl.dobosz.mbank.requests.RequestStep;
import pl.dobosz.mbank.requests.VerificationTokenRequestStep;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dobosz on 03.07.15.
 */
public class StepExecutor {
    private Map<String, RequestStep> requestSteps = new LinkedHashMap<String, RequestStep>();

    public StepExecutor(Credentials credentials) {
        requestSteps.put("Login", new LoginRequestStep(credentials));
        requestSteps.put("VerificationToken", new VerificationTokenRequestStep());
        requestSteps.put("AccountList", new AccountListRequestStep());
    }

    public void executeAll(Context context) {
        for(RequestStep step : requestSteps.values() )
            step.execute(context);
    }

    //FIXME return right model, not from json package
    public List<Account> fatchAccountList() {
        AccountListRequestStep accountListRequestStep = (AccountListRequestStep) requestSteps.get("AccountList");
        return accountListRequestStep.getAccounts();
    }

}
