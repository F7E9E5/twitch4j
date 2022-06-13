import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.util.ArrayList;

public class GraphMaker {
    int limit=-1;
    Node[] nodes;
    ArrayList<String> streamerName;

    MyFrame frame=new MyFrame();
    Graph graph=new SingleGraph("twitch4j", false, true);

    GraphMaker(int limitParam, ArrayList<String> streamerNameParam) {
        limit=limitParam;
        streamerName=streamerNameParam;

        Viewer viewer=new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        View view=viewer.addDefaultView(false);

        frame.setPreferredSize(new Dimension(1600, 900));
        frame.add((Component)view, BorderLayout.CENTER);

        graph.setAutoCreate(true);
        graph.setStrict(false);

        setAttribution();

        frame.setVisible(true);
    }

    private void setAttribution() {
        nodes=new Node[limit];
        for (int i=0; i<limit; i++) {
            nodes[i]=graph.addNode(streamerName.get(i));
            nodes[i].setAttribute("ui.label", streamerName.get(i));
            nodes[i].setAttribute("xy", i*10, (i%10)*10);
        }
    }
}
