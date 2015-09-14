package at.granul.mason.collector;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.media.chart.TimeSeriesChartGenerator;
import sim.util.media.chart.XYChartGenerator;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * DataWriter which produces a chart for every ScalarData series and a compound chart as png and pdf files in the
 * current directory
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class ChartFileScalarDataWriter implements Steppable {

    DataCollector dataCollector;
    TimeSeriesChartGenerator chart = new sim.util.media.chart.TimeSeriesChartGenerator();
    ArrayList<XYSeries> scalarSeries;

    int width = 1000, height = 600;
    String prefix = "run";

    public ChartFileScalarDataWriter(DataCollector dataCollector, String prefix, int width, int height) {
        this.dataCollector = dataCollector;
        init();
        this.prefix = prefix;
        this.width = width;
        this.height = height;
    }

    public void init() {
        chart.removeAllSeries();

        //Scalar Series
        scalarSeries = new ArrayList<XYSeries>();

        if (dataCollector.scalarNames != null)
            for (int i = 0; i < dataCollector.scalarNames.size(); i++) {
                final XYSeries series = new XYSeries(dataCollector.scalarNames.get(i), false);
                scalarSeries.add(series);
                chart.addSeries(series, null);
            }

        chart.setTitle("");
        chart.setYAxisLabel("Value");
        chart.setXAxisLabel("Time");
    }

    @Override
    public void step(SimState state) {
        final double x = state.schedule.getTime();
        // now add the data
        if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION) {
            //          val a = simState.dataCollector.values(0).asInstanceOf[Double]
            for (int a = 0; a < scalarSeries.size(); a++) {
                scalarSeries.get(a).add(x, dataCollector.scalarData.get(a).getData(), true);
            }
            //          chart.updateChartLater(state.schedule.getSteps())
            chart.updateChartWithin(state.schedule.getSteps(), 250);
            //          histogram.updateChartLater(state.schedule.getSteps())
        }
    }


    public void stop() {
        exportGraph(chart, prefix, width, height);
    }

    public static void exportGraph(XYChartGenerator chart, String prefix, int width, int height) {
        try {
            Document document = new Document(new com.lowagie.text.Rectangle(width, height));

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(prefix + "_" + DataWriter.DF.format(new Date()) + ".pdf")));

            document.addAuthor("MASON");
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            //PdfTemplate tp = cb.createTemplate(width, height);

            //Write the chart with all datasets
            chart.addLegend();
            /*LegendTitle title = new LegendTitle(chart.getChart().getPlot());
            title.setLegendItemGraphicPadding(new org.jfree.ui.RectangleInsets(0,8,0,4));
            chart.addLegend(title);*/
            LegendTitle legendTitle = chart.getChart().getLegend();
            legendTitle.setPosition(RectangleEdge.BOTTOM);

            Graphics2D g2 = cb.createGraphics(width, height, new DefaultFontMapper());
            Rectangle2D rectangle2D = new Rectangle2D.Double(0, 0, width, height);
            chart.getChart().draw(g2, rectangle2D);
            g2.dispose();

            //PNG Output
            ChartUtilities.saveChartAsJPEG(new File(prefix + "_" + DataWriter.DF.format(new Date()) + ".png"), chart.getChart(), width, height);

            chart.getChart().removeLegend();
            //tp = cb.createTemplate(width, height);

            //All invisible
            final XYItemRenderer renderer = chart.getChartPanel().getChart().getXYPlot().getRenderer();
            for (int a = 0; a < chart.getSeriesCount(); a++) {
                renderer.setSeriesVisible(a, false);
            }

            final Dataset seriesDataset = chart.getSeriesDataset();
            XYSeriesCollection series = ((XYSeriesCollection) seriesDataset);
            for (int a = 0; a < chart.getSeriesCount(); a++) {
                renderer.setSeriesVisible(a, true);
                final String seriesName = series.getSeries(a).getKey() + "";
                chart.setYAxisLabel(seriesName);
                document.newPage();
                g2 = cb.createGraphics(width, height * (a + 2), new DefaultFontMapper());
                g2.translate(0, height * (a + 1));
                chart.getChart().draw(g2, rectangle2D);
                g2.dispose();

                //PNG Output
                ChartUtilities.saveChartAsJPEG(new File(prefix + "_" + seriesName + DataWriter.DF.format(new Date()) + ".png"), chart.getChart(), width, height);

                renderer.setSeriesVisible(a, false);
            }
            //cb.addTemplate(tp, 0, 0);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
