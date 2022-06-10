import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;

public class TwitchOAuth {
    CredentialManager credentialManager= CredentialManagerBuilder.builder().build();
    String ClientID;
    String ClientSecret;
    String RedirectionURL;
    OAuth2Credential credential;
    TwitchOAuth(String clientID, String clientSecret, String redirectionURL) {
        ClientID=clientID;
        ClientSecret=clientSecret;
        RedirectionURL=redirectionURL;

        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(ClientID, ClientSecret, RedirectionURL));
        credential=new OAuth2Credential("twitch", ClientSecret);
        credentialManager.addCredential("twitch", credential);
    }
}
