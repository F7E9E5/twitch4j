package twitch4jPackage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MyFrame extends JFrame {
    int limit;
    TwitchInfo twitchInfo;

    public MyFrame(int limitParam) throws IOException {
        this.limit=limitParam;
        twitchInfo=new TwitchInfo(limit);

        GraphMaker graphMaker=new GraphMaker(twitchInfo, limit);
        BoardMaker boardMaker=new BoardMaker(twitchInfo, limit);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 800));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(graphMaker.makeGraph(), BorderLayout.CENTER);
        add(boardMaker.makeBoard(), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
