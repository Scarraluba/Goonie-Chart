package concrete.goonie.core.axis;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.chartlayers.ChartWindow;
import concrete.goonie.core.ENUM_TIMEFRAME;

import java.util.HashMap;
import java.util.Map;

abstract class Axis extends ChartWindow {
    static final Map<Integer, String> MONTH_ABBREV = new HashMap<>();
    static {
        MONTH_ABBREV.put(1, "Jan");
        MONTH_ABBREV.put(2, "Feb");
        MONTH_ABBREV.put(3, "Mar");
        MONTH_ABBREV.put(4, "Apr");
        MONTH_ABBREV.put(5, "May");
        MONTH_ABBREV.put(6, "Jun");
        MONTH_ABBREV.put(7, "Jul");
        MONTH_ABBREV.put(8, "Aug");
        MONTH_ABBREV.put(9, "Sep");
        MONTH_ABBREV.put(10, "Oct");
        MONTH_ABBREV.put(11, "Nov");
        MONTH_ABBREV.put(12, "Dec");
    }


    public Axis(ENUM_TIMEFRAME timeframe, ChartConfig config) {
        super(timeframe, config);
    }

}