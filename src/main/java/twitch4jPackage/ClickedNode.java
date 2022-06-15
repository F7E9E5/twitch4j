package twitch4jPackage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

public class ClickedNode {
    TwitchInfo twitchInfo;
    String id;
    Integer num;

    JFrame infoFrame = new JFrame();
    JPanel panel=new JPanel();

    public ClickedNode(TwitchInfo twitchInfoParam, String idParam, Integer numParam) throws IOException {
        this.twitchInfo = twitchInfoParam;
        this.id = idParam;
        this.num = numParam;

        displayInfo();
    }

    public void displayInfo() throws IOException {
        infoFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        infoFrame.setLayout(new GridBagLayout());
        infoFrame.setPreferredSize(new Dimension(900, 600));
        infoFrame.setLocationRelativeTo(null);

        panel.setPreferredSize(new Dimension(900, 600));
        infoFrame.add(panel);
        panel.setVisible(true);

        setImage();

        //set user login name to user profile http

        //user follower num

        //user current viewer num

        infoFrame.pack();
        infoFrame.setLocationRelativeTo(null);
        infoFrame.setVisible(true);
    }

    private void setImage() throws IOException {
        URL profileImageURL = null;
        try {
            profileImageURL = twitchInfo.getProfileImageURL(id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert profileImageURL != null;
        BufferedImage image = ImageIO.read(profileImageURL);
        Image resizedImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
        imageLabel.setPreferredSize(new Dimension(200, 200));
        GridBagConstraints imageLabelGbc = new GridBagConstraints();
        imageLabelGbc.insets = new Insets(5, 5, 5, 5);
        imageLabelGbc.gridy = 3;
        imageLabelGbc.gridx = 1;
        panel.add(imageLabel, imageLabelGbc);
        imageLabel.setVisible(true);
    }
}
