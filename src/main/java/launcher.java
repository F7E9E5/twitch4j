import java.io.IOException;
import java.util.ArrayList;

public class launcher {
    public static void main(String[] Args) throws IOException {
        System.setProperty("org.graphstream.ui", "swing");

        int limit=50;
        TwitchInfo twitchInfo=new TwitchInfo(limit);
        ArrayList<String> streamerName=twitchInfo.getStreamerName();
        GraphMaker graphMaker=new GraphMaker(limit, streamerName);
    }
}
