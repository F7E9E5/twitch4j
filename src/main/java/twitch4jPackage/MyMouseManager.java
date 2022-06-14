package twitch4jPackage;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;
import java.util.EnumSet;

public class MyMouseManager implements MouseManager {
    protected View view;
    protected GraphicGraph graph;

    @Override
    public void init(GraphicGraph graph, View view) {
        this.graph = graph;
        this.view = view;
        view.addListener("Mouse", this);
    }

    @Override
    public void release() {
        view.removeListener(MouseEvent.MOUSE_CLICKED, this);
    }

    @Override
    public EnumSet<InteractiveElement> getManagedTypes() {
        return null;
    }
}
