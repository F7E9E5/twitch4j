import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.exception.UnauthorizedException;
import com.github.twitch4j.helix.domain.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
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
    ArrayList<Set<String>> viewerList=new ArrayList<>();
    TwitchClient twitchClient;
    Set<String> userList=new HashSet<>();
    int[][] count=new int[limit][limit];

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
        getViewerList();
//        getRelation();
    }

    private void getName() {
        topStreamList.getStreams().forEach(stream -> streamerName.add(stream.getUserName()));
        topStreamList.getStreams().forEach(stream->System.out.println(stream.getUserLogin()));
    }

    private void getViewerList() throws IOException {
        int num=1;
        for (Stream stream:topStreamList.getStreams()) {
            System.out.println("#"+num+ ": "+stream.getUserLogin());
            num++;
            String urlstr="http://tmi.twitch.tv/group/user/"+stream.getUserLogin()+"/chatters";
            URL url = new URL(urlstr);
            JSONObject json = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));

            try {
                JSONArray array=new JSONArray(json.getJSONObject("chatters").getJSONArray("viewers"));
                for (int i=0; i<array.length(); i++) {
                    viewerList.add(Collections.singleton(array.getString(i)));
                    System.out.println(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRelation() {
        for (String userName:userList) {
            UserList thisUserList= twitchClient.getHelix().getUsers(OAuthToken, null, List.of(userName)).execute();
            User thisUser=thisUserList.getUsers().get(0);

            System.out.println("userName : " + userName + " userId : " + thisUser.getId() + " trying");

            FollowList thisUserFollowList=twitchClient.getHelix().getFollowers(OAuthToken, thisUser.getId(), null, null, 100).execute();
            HelixPagination page=thisUserFollowList.getPagination();
            ArrayList<String> thisUserfollowingList=new ArrayList<>();
            thisUserFollowList.getFollows().forEach(user->thisUserfollowingList.add(user.getToId()));
            thisUserFollowList.getFollows().forEach(user->System.out.println(user.getToName()));
            assert(page!=null);
            do {
                thisUserFollowList=twitchClient.getHelix().getFollowers(OAuthToken, thisUser.getId(), null, page.getCursor(), 100).execute();
                thisUserFollowList.getFollows().forEach(user->thisUserfollowingList.add(user.getToId()));
            } while ((page=thisUserFollowList.getPagination())!=null);

            System.out.println("user "+userName+"is folloinwg " + thisUserfollowingList.size() + " streamers");
        }

////        for (int i=0; i<limit; i++) {
////            for (int j=0; j<i; j++) {
//        int i=0;
//        int j=1;
//                int iii=viewerList.get(i).size();
//                int jjj=viewerList.get(j).size();
//
//                //check if viewers of streamer#A following both streamer#A and streamer#B
//                int a=(iii<jjj)?i:j;
//                int b=(a==iii)?j:i;
//
//                System.out.println(viewerList.get(a).size());
//                for (String userName:viewerList.get(a)) {
//                    FollowList firstList=twitchClient.getHelix().getFollowers(OAuthToken, user.getId(), topStreamList.getStreams().get(a).getUserId(), null, 1).execute();
//                    FollowList secondList=twitchClient.getHelix().getFollowers(OAuthToken, user.getId(), topStreamList.getStreams().get(b).getUserId(), null, 1).execute();
//                    if (!firstList.getFollows().isEmpty() && !secondList.getFollows().isEmpty()) {
//                        System.out.println(userName + " is following both");
//                        count[i][j]+=1;
//                    }
//                    else System.out.println("not following ");
//                }
//                System.out.println("count between streamer#" + i + " and streamer#" + j + " is " + count[i][j] );
////            }
//
////        }
////
    }
}
