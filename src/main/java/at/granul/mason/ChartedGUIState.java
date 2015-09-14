package at.granul.mason;

import at.granul.mason.collector.DataCollector;
import at.granul.mason.collector.DataCollectorChart;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;

import javax.swing.*;

/**
 * Automatic charting of  GUI model data as defined in a DataCollector. To allow for that simply extend the class and
 * provide a @link #getDataCollector() method.
 * <p/>
 * Can also produce a layout that matches the screen (@link #layout(DisplayFrame)).
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public abstract class ChartedGUIState extends GUIState {
    protected Layout layout = new Layout();
    DataCollectorChart chart = null;

    public ChartedGUIState(SimState state) {
        super(state);
    }

    public abstract DataCollector getDataCollector();

    @Override
    public void init(Controller controller) {
        super.init(controller);
        chart = new DataCollectorChart(getDataCollector(), this);
        chart.schedule();
    }

    @Override
    public void quit() {
        super.quit();
        chart.dispose();
    }

    @Override
    public void finish() {
        super.finish();
        chart.stop();
    }

    @Override
    public void start() {
        super.start();
        chart.schedule();
    }

    @Override
    public void load(SimState state) {
        super.load(state);
        chart.setDataCollector(getDataCollector());
        chart.schedule();
    }

    /**
     * Make the Layout do the Layout
     * @param displayFrame
     */
    public void layout(JFrame displayFrame){
        layout.layout(displayFrame, (Console) controller, chart);
    }

    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.getLookAndFeelDefaults().put("Slider.paintValue", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
