package twitch4jPackage;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

public class GraphMaker {
    int limit;
    private float avg;

    private int[][] relationCount;
    private ArrayList<Integer> viewerCount;
    private ArrayList<String> streamerLoginName;
    private HashMap<String, Integer> nameToNum = new HashMap<>();

    TwitchInfo twitchInfo;

    private final Graph graph = new SingleGraph("twitch4j", false, true);
    private JPanel board=new JPanel();

    private Node[] nodes;
    private Edge[][] edges;

    SwingViewer viewer;
    View view;
    ViewPanel viewPanel;

    GraphMaker(TwitchInfo twitchInfoParam, int limitParam) throws IOException {
        this.twitchInfo=twitchInfoParam;
        init(limitParam);
    }

    public ViewPanel makeGraph() {
        viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        graph.setAutoCreate(true);
        graph.setStrict(false);
        setAttribution();
        setEdge();

        view = viewer.addDefaultView(false);

        viewPanel=(ViewPanel)view;
        viewPanel.setPreferredSize(new Dimension(900, 800));

        return viewPanel;
    }

    private void init(Integer limitParam) throws IOException {
        this.limit = limitParam;
        this.twitchInfo = new TwitchInfo(limit);
        this.streamerLoginName = twitchInfo.getStreamerLoginName();
        this.viewerCount = twitchInfo.getViewerCount();
        this.relationCount = twitchInfo.getStreamerRelationship();
        this.limit = twitchInfo.getLimit();
        avg = getAvg();
    }

    private float getAvg() {
        AtomicInteger ret = new AtomicInteger();
        viewerCount.forEach(ret::addAndGet);
        return (float) ret.get() / limit;
    }

    private void setAttribution() {
        nodes = new Node[limit];

        for (int i = 0; i < limit; i++) {
            nodes[i] = graph.addNode(String.valueOf(i));
            nameToNum.put(streamerLoginName.get(i), i);
            nodes[i].setAttribute("ui.label", streamerLoginName.get(i));
            String tmp = Float.toString(max((float) viewerCount.get(i) / avg * 20, 20));
            nodes[i].setAttribute("ui.style", "text-size: " + tmp + ";");
            nodes[i].setAttribute("ui.style", "size-mode: dyn-size;");
            nodes[i].setAttribute("ui.size", (float) viewerCount.get(i) / avg * 50);
            nodes[i].setAttribute("ui.style", "fill-mode: dyn-plain;");
            nodes[i].setAttribute("ui.style", "fill-color: rgb(100,65,165);");
        }
    }

    private void setEdge() {
        edges = new Edge[limit][limit];

        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < limit; j++) {
                edges[i][j] = graph.addEdge(String.valueOf(i)+String.valueOf(j), nodes[i], nodes[j]);
                edges[i][j].setAttribute("layout.weight", 0.001 * relationCount[i][j]);
            }
        }
    }
}
