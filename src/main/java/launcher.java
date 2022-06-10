import java.io.IOException;
import java.util.ArrayList;

public class launcher {
    public static void main(String[] Args) throws IOException {
        TwitchInfo twitchInfo=new TwitchInfo();

        ArrayList<String> streamerName=twitchInfo.getStreamerName();
        int num=1;
        for (String name:streamerName) {
            System.out.println("streamer#"+num+" : "+name);
            num++;
        }
    }
}
