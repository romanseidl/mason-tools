package at.granul.mason.collector;

import org.jfree.data.xy.XYSeries;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.media.chart.HistogramGenerator;
import sim.util.media.chart.TimeSeriesChartGenerator;
import sim.util.media.chart.XYChartGenerator;

import javax.swing.*;
import java.util.ArrayList;

/**
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class DataCollectorChart {
    private static final int HISTOGRAM_DEFAULT_BINS = 16;
    private static final int UPDATE_INTERVAL = 250;

    /**
     * Charting
     */

    DataCollector dataCollector;
    GUIState state;
    TimeSeriesChartGenerator chart = new sim.util.media.chart.TimeSeriesChartGenerator();
    JFrame chartFrame = chart.createFrame();
    HistogramGenerator histogram = new HistogramGenerator();
    JFrame histogramFrame = histogram.createFrame();
    JFrame collectorFrame = null;


    public DataCollectorChart(DataCollector dataCollector, GUIState state) {
        this.dataCollector = dataCollector;
        this.state = state;
        createChartFrame(state.controller);
    }

    public void setDataCollector(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    public void schedule() {
        chart.removeAllSeries();
        histogram.removeAllSeries();
        final DataCollector dc = dataCollector;

        //Scalar Series
        final ArrayList<XYSeries> scalarSeries = new ArrayList<XYSeries>();

        if(dc.scalarNames != null)
            for (int i = 0; i < dc.scalarNames.size(); i++) {
                final XYSeries series = new XYSeries(dc.scalarNames.get(i), false);
                scalarSeries.add(series);
                chart.addSeries(series, null);
            }

        //Array Series
        if(dc.arrayNames != null)
            for (int i = 0; i < dc.arrayNames.size(); i++) {
                histogram.addSeries(null, HISTOGRAM_DEFAULT_BINS, dc.arrayNames.get(i), null);
            }

        state.scheduleRepeatingImmediatelyAfter(new Steppable() {
            @Override
            public void step(SimState simState) {
                final double x = state.state.schedule.getTime();
                // now add the data
                if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION) {
                    //          val a = simState.dataCollector.values(0).asInstanceOf[Double]
                    for (int a = 0; a < scalarSeries.size(); a++) {
                        scalarSeries.get(a).add(x, dc.scalarData.get(a).getData(), true);
                    }
                    for (int a = 0; a < dc.arrayNames.size(); a++) {
                        histogram.updateSeries(a, dc.arrayData.get(a).provide());
                    }
                    //          chart.updateChartLater(state.schedule.getSteps())
                    chart.updateChartWithin(state.state.schedule.getSteps(), UPDATE_INTERVAL);
                    //          histogram.updateChartLater(state.schedule.getSteps())
                    histogram.updateChartWithin(state.state.schedule.getSteps(), UPDATE_INTERVAL);
                }
            }
        });

    }

    private void createChartFrame(Controller controller) {
        chart.setTitle("Charts");
        chart.setYAxisLabel("Value");
        chart.setXAxisLabel("Time");
        chart.removeAllSeries();

        histogram.setTitle("Histograms");
        histogram.setYAxisLabel("Count");
        histogram.setXAxisLabel("Value");
        histogram.removeAllSeries();

        // Create a Tabbed Pane with Chats and Histograms
        JTabbedPane graphTabs = new JTabbedPane();
        graphTabs.addTab("Charts", chartFrame.getContentPane());
        graphTabs.addTab("Histograms", histogramFrame.getContentPane());
        collectorFrame = new JFrame();
        collectorFrame.setTitle("Graphs");
        collectorFrame.add(graphTabs);
        collectorFrame.setVisible(true);
        collectorFrame.pack();
        controller.registerFrame(collectorFrame);
    }

    private void stop(XYChartGenerator chart) {
        //chart.update(state.schedule.getSteps(), true)
        chart.repaint();
        chart.stopMovie();
    }

    public void stop() {
        stop(chart);
        stop(histogram);
    }

    public void dispose() {
        stop();

        if (chartFrame != null) chartFrame.dispose();
        chartFrame = null;

        if (histogramFrame != null) histogramFrame.dispose();
        histogramFrame = null;
    }

    public void setSize(int w, int h){
        collectorFrame.setSize(w,h);
    }

    public void setLocation(int x, int y){
        collectorFrame.setLocation(x,y);
    }

}

