package concrete.goonie;

import java.awt.*;
import java.io.File;

import static concrete.goonie.ChartConfig.getFont;

public class ChartThemes {

    public static ChartConfig lightTheme() {
        Font interFont = getFont(Font.PLAIN,12);
        return new ChartConfig()
                .setTheme(ChartConfig.Theme.LIGHT)
                .setBackgroundColor(new Color(255, 255, 255))  // Pure white
                .setAxisColor(new Color(42, 46, 56))           // Dark gray for axes
                .setGridColor(new Color(240, 241, 245))        // Very light gray for grid
                .setBullishColor(new Color(22, 166, 121))      // TradingView green
                .setBearishColor(new Color(234, 74, 90))       // TradingView red
                .setTextColor(new Color(42, 46, 56))           // Dark gray for text
                .setTextFont(interFont)
                .setMovingAverageColor(new Color(55, 114, 220))  // TradingView blue for MA
                .setTrendlineColor(new Color(255, 152, 0))     // TradingView orange
                .setCrosshairColor(new Color(120, 120, 120))   // Medium gray
                .setEnableAntiAliasing(true)
                .setShowGrid(true)
                .setShowCrosshair(true);
    }

    public static ChartConfig darkTheme() {
        Font interFont = getFont(Font.PLAIN,12);

        return new ChartConfig()
                .setTheme(ChartConfig.Theme.DARK)
                .setBackgroundColor(new Color(20, 22, 27))      // Dark background
                .setAxisColor(new Color(24, 106, 255))        // Light gray for axes
                .setGridColor(new Color(42, 46, 56))           // Dark gray for grid
                .setBullishColor(new Color(22, 166, 121))      // Same green as light theme
                .setBearishColor(new Color(234, 74, 90))       // Same red as light theme
                .setTextColor(new Color(200, 203, 210))        // Light gray for text
                .setTextFont(interFont)
                .setMovingAverageColor(new Color(55, 114, 220))  // Same blue as light theme
                .setTrendlineColor(new Color(255, 152, 0))     // Same orange as light theme
                .setCrosshairColor(new Color(100, 100, 100))
                .setEnableAntiAliasing(true)
                .setShowGrid(true)
                .setShowCrosshair(true);
    }


}
