package concrete.goonie;

import java.awt.*;

public class ChartThemes {

    public static ChartConfig lightTheme() {
        return new ChartConfig()
            .setTheme(ChartConfig.Theme.LIGHT)
            .setBackgroundColor(Color.WHITE)
            .setAxisColor(Color.BLACK)
            .setGridColor(new Color(220, 220, 220))
            .setBullishColor(Color.GREEN)
            .setBearishColor(Color.RED)
            .setTextColor(Color.BLACK)
            .setTextFont(new Font("Arial", Font.PLAIN, 10))
            .setMovingAverageColor(Color.BLUE)
            .setTrendlineColor(Color.ORANGE)
            .setCrosshairColor(Color.GRAY)
            .setEnableAntiAliasing(true)
            .setShowGrid(true)
            .setShowCrosshair(true);
    }

    public static ChartConfig darkTheme() {
        return new ChartConfig()
            .setTheme(ChartConfig.Theme.DARK)
            .setBackgroundColor(new Color(30, 30, 30))
            .setAxisColor(Color.LIGHT_GRAY)
            .setGridColor(new Color(60, 60, 60))
            .setBullishColor(new Color(0, 200, 0))
            .setBearishColor(new Color(255, 80, 80))
            .setTextColor(Color.WHITE)
            .setTextFont(new Font("Consolas", Font.PLAIN, 11))
            .setMovingAverageColor(new Color(0, 170, 255))
            .setTrendlineColor(new Color(255, 165, 0))
            .setCrosshairColor(new Color(100, 100, 100))
            .setEnableAntiAliasing(true)
            .setShowGrid(true)
            .setShowCrosshair(true);
    }
}
