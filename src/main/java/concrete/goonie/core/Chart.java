package concrete.goonie.core;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.axis.XAxis;
import concrete.goonie.core.chartlayers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Chart extends JPanel implements SubWindowListener {
    private final ChartConfig config;
    private ENUM_TIMEFRAME timeframe = ENUM_TIMEFRAME.PERIOD_H1;
    private List<ChartWindow> panes = new ArrayList<>();
    private final AffineTransform transform;
    private final ChartMouseHandler mouseHandler;
    private final MainWindow mainWindow;
    private final MultiSplitPane multiSplit;
    private final XAxis xAxis;

    public Chart(ChartConfig config) {
        this.config = config;
        this.transform = new AffineTransform();
        this.mouseHandler = new ChartMouseHandler(config, panes, this);
        this.mainWindow = new MainWindow(timeframe, config);
        this.mainWindow.setTransform(mouseHandler.getTransform());

        this.multiSplit = new MultiSplitPane(JSplitPane.VERTICAL_SPLIT);
        multiSplit.addComponent(mainWindow, 4);

        setLayout(new BorderLayout(0, 0));
        add(multiSplit, BorderLayout.CENTER);
        panes.add(mainWindow);
        mouseHandler.addListener(mainWindow);

        multiSplit.setDividerColor(new Color(0x0FFFFFF, true));
        multiSplit.setHoverColor(new Color(0x834A7C54, true));
        multiSplit.setDividersBackground(config.getBackgroundColor());

        xAxis = new XAxis(timeframe, config);
        panes.add(xAxis);
        mouseHandler.addListener(xAxis);
        add(xAxis, BorderLayout.SOUTH);

        init();

    }

    private void init() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension panelSize = getSize();
                mouseHandler.setChartSize(panelSize.width, panelSize.height);
            }
        });
        setBackground(config.getBackgroundColor());
    }

    int count = 0;

    public void addWindow() {
        SubWindow subWindow = new SubWindow(timeframe, config, this);
        subWindow.setText(String.valueOf(count));
        subWindow.setTransform(mouseHandler.getTransform());

        multiSplit.addComponent(subWindow, 2.0);
        panes.add(subWindow);
        mouseHandler.addListener(subWindow);
        count = panes.size();
        revalidate();
        repaint();


    }

    public static void printComponentHierarchy(Component comp, int depth) {
        // Indentation for hierarchy visualization
        String indent = "  ".repeat(depth);
        System.out.println(indent + "- " + comp.getClass().getSimpleName());

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                printComponentHierarchy(child, depth + 1);
            }
        }
    }


    @Override
    public void onSubWindowRemoved(SubWindow subWindow) {
        panes.remove(subWindow);
        mouseHandler.removeListener(subWindow);
        multiSplit.removeComponent(subWindow);
        revalidate();
        repaint();
    }

    @Override
    public void onResize(SubWindow subWindow) {
        //mouseHandler.adjustPaneScaleY( subWindow);
    }


    public ChartConfig getConfig() {
        return config;
    }
}