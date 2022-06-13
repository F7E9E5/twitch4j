import java.io.IOException;
import java.util.ArrayList;

public class launcher {
    public static void main(String[] Args) throws IOException {
        System.setProperty("org.graphstream.ui", "swing");

        int limit=10;
        GraphMaker graphMaker=new GraphMaker(limit);
    }
}
