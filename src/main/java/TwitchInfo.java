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
    int limit=50;

    private final StreamList topStreamList;
    private final ArrayList<String> streamerName=new ArrayList<>();
    private final ArrayList<Set<String>> viewerList=new ArrayList<>();
    private final TwitchClient twitchClient;
    private final Set<String> userList=new HashSet<>();
    private final int[][] count=new int[limit][limit];

    public ArrayList<String> getStreamerName() {
        return streamerName;
    }

    public int[][] getStreamerRelationship() {
        return count;
    }

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
        getRelation();
    }

    private void getName() {
        topStreamList.getStreams().forEach(stream -> streamerName.add(stream.getUserName()));
//        topStreamList.getStreams().forEach(stream->System.out.println(stream.getUserLogin()));
    }

    private void getViewerList() throws IOException {
        int num=1;
        for (Stream stream:topStreamList.getStreams()) {
//            System.out.println("#"+num+ ": "+stream.getUserLogin());
            num++;
            String urlstr="http://tmi.twitch.tv/group/user/"+stream.getUserLogin()+"/chatters";
            URL url = new URL(urlstr);
            JSONObject json = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));

            Set<String> nowArray=new TreeSet<>();
            try {
                JSONArray array=new JSONArray(json.getJSONObject("chatters").getJSONArray("viewers"));
                for (int i=0; i<array.length(); i++) {
                    nowArray.add(array.getString(i));
                    userList.add(array.getString(i));
//                    System.out.println(array.getString(i));
                }
//                System.out.println(array.length());
                viewerList.add(nowArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRelation() {
//        int num=0;
//        for (String userName:userList) {
//            num++;
//            if (num>1000) break;
//            UserList forThisUserId= twitchClient.getHelix().getUsers(OAuthToken, null, List.of(userName)).execute();
//            User thisUser=forThisUserId.getUsers().get(0);
//            String thisUserId=thisUser.getId();
//            System.out.println("user : " + thisUser.getDisplayName() + ", userName : " + userName + ", userId : " + thisUserId + " trying");
//
//            ArrayList<String> thisUserFollowingList=new ArrayList<>();
//
//            FollowList thisUserFollowingListPage=twitchClient.getHelix().getFollowers(OAuthToken, thisUserId, null, null, 100).execute();
//            HelixPagination page=new HelixPagination();
////            page=thisUserFollowingListPage.getPagination();
//
//            thisUserFollowingListPage.getFollows().forEach(user->thisUserFollowingList.add(user.getToId()));
////            thisUserFollowingListPage.getFollows().forEach(user->System.out.println(user.getToName()));
//            int sizeMemo=0;
//            Set<String> st=new HashSet<>();
//            thisUserFollowingListPage.getFollows().forEach(user->st.add(user.getToName()));
//            while ((page=thisUserFollowingListPage.getPagination())!=null) {
//                thisUserFollowingListPage = twitchClient.getHelix().getFollowers(OAuthToken, thisUserId, null, page.getCursor(), 100).execute();
//                thisUserFollowingListPage.getFollows().forEach(user -> thisUserFollowingList.add(user.getToId()));
////                thisUserFollowingListPage.getFollows().forEach(user->System.out.println(user.getToName()));
//                thisUserFollowingListPage.getFollows().forEach(user -> st.add(user.getToName()));
////                System.out.println(st.size());
//                if (st.size()==sizeMemo) break;
//                sizeMemo=st.size();
//            }

//            System.out.println("user "+userName+"is folloinwg " + thisUserFollowingList.size() + " streamers");
//        }


        for(int i=0; i<limit; i++) {
            for (int j=0; j<i; j++) {
                Set<String> mergeSet=new HashSet<>();
                mergeSet.addAll(viewerList.get(i));
                mergeSet.addAll(viewerList.get(j));
                int result=viewerList.get(i).size()+viewerList.get(j).size()-mergeSet.size();
//                System.out.println("watching both streamer#"+i+" and streamer#"+j+ " : " + result);
                count[i][j]=count[j][i]=result;
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
            }

        }

    }
}
