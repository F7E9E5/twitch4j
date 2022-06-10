import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.exception.UnauthorizedException;
import com.github.twitch4j.helix.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitchInfo {
    String ClientID="nu2iy6l1zrpn95fo33fkkp7dkl0ks8";
    String ClientSecret="5a5y19o9tb5k10rsfn93ae00ps19kz";
    String RedirectionURL="https://twitchapps.com/tokengen/";
    String OAuthToken="0nqt5pcpj5j6lx849x711yblnfgrj4";

    TwitchOAuth twitchOAuth=new TwitchOAuth(ClientID, ClientSecret, RedirectionURL);
    int limit=100;

    StreamList topStreamList;
    ArrayList<String> streamerName=new ArrayList<>();
    ArrayList<ArrayList<String>> followerList=new ArrayList<>();
    TwitchClient twitchClient;

    TwitchInfo() throws UnauthorizedException, IOException {
        twitchClient = TwitchClientBuilder.builder()
            .withDefaultAuthToken(new OAuth2Credential("twitch", ClientSecret))
                .withEnableHelix(true)
            .build();

        topStreamList = twitchClient.getHelix().getStreams(OAuthToken,
                null,
                null,
                limit,
                null,
                Collections.singletonList("ko"),
                null,
                null).execute();

        getName();
        getFollowList();
    }

    private void getName() {
        topStreamList.getStreams().forEach(stream -> streamerName.add(stream.getUserName()));
//        for (String s:streamerName) {
//            System.out.println(s);
//        }
    }

    private void getFollowList() throws IOException {
        int num=0;
        for (Stream stream:topStreamList.getStreams()) {
            System.out.println("#"+num+ ": "+stream.getUserLogin());
//            System.out.println(stream.getUserId());
            num++;
            String urlstr="http://tmi.twitch.tv/group/user/"+stream.getUserLogin()+"/chatters";
            URL url = new URL(urlstr);

            try {
                JSONObject json = new JSONObject(url);
                String title = (String) json.get("broadcaster");
                System.out.println(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
