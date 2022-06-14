import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphMaker implements ViewerListener {
    int limit;

    private ArrayList<String> streamerLoginName;
    private ArrayList<Integer> viewerCount;
    private final int[][] relationCount;

    private Node[] nodes;
    private Edge[][] edges;
    private final MyFrame frame = new MyFrame();
    private HashMap<String, Integer> nameToNum = new HashMap<>();

    private final Graph graph = new SingleGraph("twitch4j", false, true);

    private float avg;
    TwitchInfo twitchInfo;

    protected boolean loop = true;

    GraphMaker(int limitParam) throws IOException {
        limit = limitParam;
        twitchInfo = new TwitchInfo(limit);
        streamerLoginName = twitchInfo.getStreamerLoginName();
        viewerCount = twitchInfo.getViewerCount();
        relationCount = twitchInfo.getStreamerRelationship();
        limit = twitchInfo.getLimit();

        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        View view = viewer.addDefaultView(false);

        ViewerPipe viewerPipe = viewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(graph);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add((Component) view, BorderLayout.CENTER);

        graph.setAutoCreate(true);
        graph.setStrict(false);

        avg = getAvg();
        System.out.println(avg);

        setAttribution();
        setEdge();

        frame.pack();
        frame.setVisible(true);

        while (loop) {
            viewerPipe.pump();
        }
    }

    private float getAvg() {
        AtomicInteger ret = new AtomicInteger();
        viewerCount.forEach(ret::addAndGet);
        return (float) ret.get() / limit;
    }

    private void setAttribution() {
        nodes = new Node[limit];
        for (int i = 0; i < limit; i++) {
            nodes[i] = graph.addNode(streamerLoginName.get(i));
            nameToNum.put(streamerLoginName.get(i), i);
            nodes[i].setAttribute("ui.label", streamerLoginName.get(i));
            nodes[i].setAttribute("ui.style", "size-mode: dyn-size;");
            nodes[i].setAttribute("ui.size", (float) viewerCount.get(i) / avg * 100);
            nodes[i].setAttribute("ui.style", "fill-mode: dyn-plain;");
            nodes[i].setAttribute("ui.style", "fill-color: rgb(100,65,165);");
        }
    }

    private void setEdge() {
        edges = new Edge[limit][limit];

        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < limit; j++) {
                edges[i][j] = graph.addEdge(streamerLoginName.get(i) + streamerLoginName.get(j), nodes[i], nodes[j]);
                edges[i][j].setAttribute("layout.weight", 0.001 * relationCount[i][j]);
            }
        }
    }

    @Override
    public void viewClosed(String viewName) {
        loop = false;
        System.out.println("goodbye");
    }

    @Override
    public void buttonPushed(String id) {
//        System.out.println("button pushed on" + id);
        if (nameToNum.containsKey(id)) {
            int num = nameToNum.get(id);
            try {
                ClickedNode node = new ClickedNode(twitchInfo, id, num);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void buttonReleased(String id) {
    }

    @Override
    public void mouseOver(String id) {
    }

    @Override
    public void mouseLeft(String id) {
    }
}
