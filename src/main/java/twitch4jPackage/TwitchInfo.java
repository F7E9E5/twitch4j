package twitch4jPackage;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.exception.UnauthorizedException;
import com.github.twitch4j.helix.domain.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitchInfo {
    private final String ClientID = "xxx";
    private final String ClientSecret = "xxx";
    private final String RedirectionURL = "xxx";
    private final String OAuthToken = "xxx";

    int limit;

    private final StreamList topStreamList;
    private final ArrayList<String> streamerLoginName = new ArrayList<>();
    private final ArrayList<Set<String>> viewerList = new ArrayList<>();
    private final TwitchClient twitchClient;
    private final Set<String> userList = new HashSet<>();
    private final int[][] count;
    private final ArrayList<Integer> viewerCount = new ArrayList<>();
    private final ArrayList<String> streamerName=new ArrayList<>();

    public ArrayList<String> getStreamerLoginName() {
        return streamerLoginName;
    }

    public int[][] getStreamerRelationship() {
        return count;
    }

    public int getLimit() {
        return limit;
    }

    public ArrayList<Integer> getViewerCount() {
        return viewerCount;
    }

    public StreamList getTopStreamList() { return topStreamList; }

    public ArrayList<String> getStreamerName() {return streamerName; }

    TwitchInfo(int limitParam) throws UnauthorizedException, IOException {
        limit = limitParam;
        count = new int[limit][limit];

        twitchClient = TwitchClientBuilder.builder().withDefaultAuthToken(new OAuth2Credential("twitch", ClientSecret)).withEnableHelix(true).build();
        topStreamList = twitchClient.getHelix().getStreams(OAuthToken, null, null, limit, null, Collections.singletonList("ko"), null, null).execute();

        getName();
        limit = getViewerList();
        getRelation();
    }

    private void getName() {
        topStreamList.getStreams().forEach(stream -> streamerLoginName.add(stream.getUserLogin()));
        topStreamList.getStreams().forEach(stream->streamerName.add(stream.getUserName()));
    }

    private int getViewerList() throws IOException {
        for (int i = 0; i < limit; i++) {
            String urlstr = "http://tmi.twitch.tv/group/user/" + topStreamList.getStreams().get(i).getUserLogin() + "/chatters";
            URL url = new URL(urlstr);
            JSONObject json;
            try {
                json = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
            } catch (SocketException ex) {
                limit--;
                continue;
            }

            Set<String> nowArray = new TreeSet<>();
            try {
                JSONArray array = new JSONArray(json.getJSONObject("chatters").getJSONArray("viewers"));
                viewerCount.add(json.getInt("chatter_count"));
                for (int j = 0; j < array.length(); j++) {
                    nowArray.add(array.getString(j));
                    userList.add(array.getString(j));
                }
                viewerList.add(nowArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return limit;
    }

    private void getRelation() {
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < i; j++) {
                Set<String> mergeSet = new HashSet<>();
                mergeSet.addAll(viewerList.get(i));
                mergeSet.addAll(viewerList.get(j));
                int result = viewerList.get(i).size() + viewerList.get(j).size() - mergeSet.size();
                count[i][j] = count[j][i] = result;
            }
        }
    }

    public URL getProfileImageURL(String userLoginName) throws MalformedURLException {
        UserList userlist = twitchClient.getHelix().getUsers(OAuthToken, null, List.of(userLoginName)).execute();
        User user = userlist.getUsers().get(0);
        String urlStr = user.getProfileImageUrl();
        return new URL(urlStr);
    }
}
