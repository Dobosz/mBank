package pl.dobosz.mbank.models.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by dobosz on 01.07.15.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Login {

    @JsonSerialize
    @JsonProperty("UserName")
    private String username;
    @JsonSerialize
    @JsonProperty("Password")
    private String password;
    @JsonSerialize
    @JsonProperty("Scenario")
    private String scenario = "Default";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
}
