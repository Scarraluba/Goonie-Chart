package concrete.goonie.core.chartlayers;

import concrete.goonie.core.ChartMouseHandler;

public interface SubWindowListener {
    void onSubWindowRemoved(SubWindow subWindow);
    void onResize(SubWindow subWindow);

}
