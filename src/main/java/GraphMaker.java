import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphMaker {
    int limit;

    private ArrayList<String> streamerLoginName;
    private ArrayList<Integer> viewerCount;
    private final int[][] relationCount;

    private Node[] nodes;
    private Edge[][] edges;
    private final MyFrame frame=new MyFrame();
    private final Graph graph=new SingleGraph("twitch4j", false, true);

    private float avg;

    GraphMaker(int limitParam) throws IOException {
        limit=limitParam;
        TwitchInfo twitchInfo=new TwitchInfo(limit);
        streamerLoginName=twitchInfo.getStreamerLoginName();
        viewerCount=twitchInfo.getViewerCount();
        relationCount=twitchInfo.getStreamerRelationship();
        limit=twitchInfo.getLimit();

        Viewer viewer=new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        View view=viewer.addDefaultView(false);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add((Component)view, BorderLayout.CENTER);

        graph.setAutoCreate(true);
        graph.setStrict(false);

        avg=getAvg();
        System.out.println(avg);

        setAttribution();
        setEdge();

        frame.setVisible(true);
    }

    private float getAvg() {
        AtomicInteger ret= new AtomicInteger();
        viewerCount.forEach(ret::addAndGet);
        return (float)ret.get()/limit;
    }

    private void setAttribution() {
        nodes=new Node[limit];
        for (int i=0; i<limit; i++) {
            nodes[i]=graph.addNode(streamerLoginName.get(i));
            nodes[i].setAttribute("ui.label", streamerLoginName.get(i));
            nodes[i].setAttribute("ui.style", "size-mode: dyn-size;");
            nodes[i].setAttribute("ui.size", (float)viewerCount.get(i)/avg*100);
            nodes[i].setAttribute("ui.style", "fill-mode: dyn-plain;");
            nodes[i].setAttribute("ui.style", "fill-color: rgb(100,65,165);");
        }
    }

    private void setEdge() {
        edges=new Edge[limit][limit];

        for (int i=0; i<limit; i++) {
            for (int j=i+1; j<limit; j++) {
                edges[i][j]=graph.addEdge(streamerLoginName.get(i)+streamerLoginName.get(j), nodes[i], nodes[j]);
                edges[i][j].setAttribute("layout.weight", 0.001*relationCount[i][j]);
            }
        }
    }
}
