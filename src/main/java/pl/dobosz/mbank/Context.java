package pl.dobosz.mbank;

/**
 * Created by dobosz on 02.07.15.
 */
public class Context {
    //Bank2 cookie
    private String token;

    //mBank_tabId cookie
    private String tabId;

    //X-RequestStep-Verification-Token header
    private String verificationToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Override
    public String toString() {
        return "pl.dobosz.mbank.Context{" +
                "token='" + token + '\'' +
                ", tabId='" + tabId + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                '}';
    }
}
