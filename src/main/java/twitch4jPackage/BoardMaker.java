package twitch4jPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class BoardMaker extends JScrollPane {
    int limit;
    TwitchInfo twitchInfo;
    ArrayList<String> streamerLoginName;

    public BoardMaker(TwitchInfo twitchInfoParam, int limitParam) {
        this.twitchInfo=twitchInfoParam;
        this.limit=limitParam;

        init();
    }

    private void init() {
        this.streamerLoginName=twitchInfo.getStreamerLoginName();
    }

    public BoardMaker makeBoard() {
        setPreferredSize(new Dimension(300, 800));
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ScrollPaneLayout scrollPaneLayout=new ScrollPaneLayout();
        setLayout(scrollPaneLayout);

        JPanel bigPanel=new JPanel();
        bigPanel.setLayout(new GridLayout(limit, 1));
        setViewportView(bigPanel);

        Font font=new Font(null, Font.BOLD, 20);
        //bigPanel.setBackground(Color.yellow);

        for (int i=0; i<limit; i++) {
            JPanel panel=new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(new Dimension(270, 30));

            JLabel label=new JLabel(twitchInfo.getStreamerName().get(i));
            label.setPreferredSize(new Dimension(170, 30));
            label.setForeground(Color.white);
            
            panel.add(label, BorderLayout.CENTER);
            panel.setBackground(new Color(142, 78, 236));
            panel.setBorder(BorderFactory.createLineBorder(Color.black));
            
            label.setVisible(true);
            label.setFont(font);

            JButton button=new JButton("More");
            button.setPreferredSize(new Dimension(100, 30));
            button.setBackground(Color.white);
            panel.add(button, BorderLayout.EAST);
            button.setVisible(true);
            int finalI = i;
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        MoreInfoFrame moreInfoFrame=new MoreInfoFrame(twitchInfo, finalI);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            bigPanel.add(panel);
            panel.setVisible(true);
        }

        setVisible(true);

        return this;
    }
}
