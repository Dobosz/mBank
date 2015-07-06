package pl.dobosz.bankproject.scraper.models.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by dobosz on 01.07.15.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class LoginJSON {

  @JsonSerialize
  @JsonProperty("UserName")
  public String username;
  @JsonSerialize
  @JsonProperty("Password")
  public String password;
  @JsonSerialize
  @JsonProperty("Scenario")
  private final String scenario = "Default";
}
