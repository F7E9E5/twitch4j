import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.exception.UnauthorizedException;
import com.github.twitch4j.helix.domain.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitchInfo {
    private final String ClientID="nu2iy6l1zrpn95fo33fkkp7dkl0ks8";
    private final String ClientSecret="5a5y19o9tb5k10rsfn93ae00ps19kz";
    private final String RedirectionURL="https://twitchapps.com/tokengen/";
    private final String OAuthToken="0nqt5pcpj5j6lx849x711yblnfgrj4";

    TwitchOAuth twitchOAuth=new TwitchOAuth(ClientID, ClientSecret, RedirectionURL);
    int limit=-1;

    private final StreamList topStreamList;
    private final ArrayList<String> streamerName=new ArrayList<>();
    private final ArrayList<Set<String>> viewerList=new ArrayList<>();
    private final TwitchClient twitchClient;
    private final Set<String> userList=new HashSet<>();
    private final int[][] count;

    public ArrayList<String> getStreamerName() {
        return streamerName;
    }
    public int[][] getStreamerRelationship() {
        return count;
    }

    TwitchInfo(int limitParam) throws UnauthorizedException, IOException {
        limit=limitParam;
        count=new int[limit][limit];

        twitchClient = TwitchClientBuilder.builder().withDefaultAuthToken(new OAuth2Credential("twitch", ClientSecret)).withEnableHelix(true).build();
        topStreamList = twitchClient.getHelix().getStreams(OAuthToken,null, null, limit, null, Collections.singletonList("ko"), null, null).execute();

        getName();
        getViewerList();
        getRelation();
    }

    private void getName() {
        topStreamList.getStreams().forEach(stream -> streamerName.add(stream.getUserName()));
    }

    private void getViewerList() throws IOException {
        for (Stream stream:topStreamList.getStreams()) {
            String urlstr="http://tmi.twitch.tv/group/user/"+stream.getUserLogin()+"/chatters";
            URL url = new URL(urlstr);
            JSONObject json = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));

            Set<String> nowArray=new TreeSet<>();
            try {
                JSONArray array=new JSONArray(json.getJSONObject("chatters").getJSONArray("viewers"));
                for (int i=0; i<array.length(); i++) {
                    nowArray.add(array.getString(i));
                    userList.add(array.getString(i));
                }
                viewerList.add(nowArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRelation() {
        for(int i=0; i<limit; i++) {
            for (int j=0; j<i; j++) {
                Set<String> mergeSet=new HashSet<>();
                mergeSet.addAll(viewerList.get(i));
                mergeSet.addAll(viewerList.get(j));
                int result=viewerList.get(i).size()+viewerList.get(j).size()-mergeSet.size();
                System.out.println("watching both streamer#"+i+" and streamer#"+j+ " : " + result);
                count[i][j]=count[j][i]=result;
            }
        }
    }
}
