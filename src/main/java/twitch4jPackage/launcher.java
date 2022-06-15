package twitch4jPackage;

import java.io.IOException;

public class launcher {
    public static void main(String[] Args) throws IOException {
        System.setProperty("org.graphstream.ui", "swing");

        int limit=20;
        MyFrame myFrame=new MyFrame(limit);
    }
}
