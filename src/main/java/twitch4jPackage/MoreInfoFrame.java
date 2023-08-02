package twitch4jPackage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MoreInfoFrame extends JFrame {
    Integer number;
    TwitchInfo twitchInfo;
    final private Font font = new Font(null, Font.BOLD, 20);

    public MoreInfoFrame(TwitchInfo twitchInfoParam, Integer numberParam) throws IOException {
        this.number=numberParam;
        this.twitchInfo=twitchInfoParam;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600, 400));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        addProfileImage();
        addName();
        addLoginName();
        addChatterCount();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addProfileImage() throws IOException {
        URL url=twitchInfo.getProfileImageURL(twitchInfo.getStreamerLoginName().get(number));
        BufferedImage image = ImageIO.read(url);
        Image resizedImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        GridBagConstraints imageLabelGbc = new GridBagConstraints();
        imageLabelGbc.gridy = 0;
        imageLabelGbc.gridx = 0;
        imageLabelGbc.gridwidth=1;
        imageLabelGbc.gridheight=3;
        add(imageLabel, imageLabelGbc);
        imageLabel.setVisible(true);
    }

    private void addName() {
        JLabel nameLabel= new JLabel(twitchInfo.getStreamerName().get(number));
        nameLabel.setFont(font);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setPreferredSize(new Dimension(300, 200/3));
        GridBagConstraints nameLabelGbc=new GridBagConstraints();
        nameLabelGbc.gridy=0;
        nameLabelGbc.gridx=1;
        add(nameLabel, nameLabelGbc);
        nameLabel.setVisible(true);
    }

    private void addLoginName() {
        JLabel loginNameLabel = new JLabel(twitchInfo.getStreamerLoginName().get(number));
        loginNameLabel.setFont(font);
        loginNameLabel.setHorizontalAlignment(JLabel.CENTER);
        loginNameLabel.setVerticalAlignment(JLabel.CENTER);
        loginNameLabel.setPreferredSize(new Dimension(300, 200/3));
        GridBagConstraints loginNameLabelGbc = new GridBagConstraints();
        loginNameLabelGbc.gridy = 1;
        loginNameLabelGbc.gridx = 1;
        add(loginNameLabel, loginNameLabelGbc);
        loginNameLabel.setVisible(true);

        String url = "https://www.twitch.tv/" + twitchInfo.getStreamerLoginName().get(number);
        loginNameLabel.setForeground(Color.BLUE.darker());
        loginNameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginNameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addChatterCount() {
        int count=twitchInfo.getTopStreamList().getStreams().get(number).getViewerCount();

        JLabel countLabel=new JLabel("Chatter Count : " + count);
        countLabel.setFont(font);
        countLabel.setHorizontalAlignment(JLabel.CENTER);
        countLabel.setVerticalAlignment(JLabel.CENTER);
        countLabel.setPreferredSize(new Dimension(300, 200/3));
        GridBagConstraints countLabelGbc = new GridBagConstraints();
        countLabelGbc.gridy = 2;
        countLabelGbc.gridx = 1;
        add(countLabel, countLabelGbc);
        countLabel.setVisible(true);
    }
}
