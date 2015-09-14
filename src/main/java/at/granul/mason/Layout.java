package at.granul.mason;

import at.granul.mason.collector.DataCollectorChart;
import sim.display.Console;

import javax.swing.*;
import java.awt.*;

/**
 * Automatic layout helper to produce a layout of the console and a DataCollectorChart that fills the screen based
 * on a given displayFrame.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Layout {
    /**
     * Screen auto adapt
     */
    private int scWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width -30;

    //Screen is large enough
    private boolean largeScreen = scWidth >= 1300.0;
    private double screenFactor = scWidth / 1300.0;
    private int cWidth = largeScreen ? (int) (550 * screenFactor) : 550;
    private int dWidth = largeScreen ? (int) (750 * screenFactor) : scWidth - 550;
    private int cHeight = (int) (dWidth * 0.6);

    public int getDisplayWidth() { return dWidth; }

    public void layout(JFrame displayFrame, Console console, DataCollectorChart chart){
        Point dLoc = displayFrame.getLocation();
        Dimension dSize = displayFrame.getSize();

        console.setSize(cWidth, cHeight);
        console.setLocation((int) (dLoc.getX() + dSize.getWidth()), (int) dLoc.getY());

        chart.setSize( cWidth, (int) dSize.getHeight() - cHeight);
        chart.setLocation((int) (dLoc.getX() + dSize.getWidth()), (int) dLoc.getY() + cHeight);
    }
}
