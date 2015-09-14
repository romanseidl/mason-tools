package at.granul.mason.example.schelling;

/**
 * Created by SeidlR on 17.03.14.
 */

import at.granul.mason.ChartedGUIState;
import at.granul.mason.collector.DataCollector;
import at.granul.mason.inspector.TitledSimpleInspector;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.inspector.TabbedInspector;

import javax.swing.*;
import java.awt.*;

/**
 * Based on Schelling Example from Mason, copyright 2006 by Sean Luke and George Mason University
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class SchellingWithUI extends ChartedGUIState {
    public Display2D display;
    public JFrame displayFrame;

    FastValueGridPortrayal2D agentPortrayal = new FastValueGridPortrayal2D("Agents");

    public static void main(String[] args) {
        setNativeLookAndFeel();
        SchellingWithUI schelling = new SchellingWithUI();
        Console c = new Console(schelling);
        c.setVisible(true);
    }

    public SchellingWithUI() {
        super(new Schelling(System.currentTimeMillis()));
    }

    public SchellingWithUI(SimState state) {
        super(state);
    }

    @Override
    public DataCollector getDataCollector() {
        return ((Schelling) state).dataCollector;
    }

    @Override
    public Inspector getInspector() {
        final TabbedInspector insp = new TabbedInspector();
        insp.addInspector(new TitledSimpleInspector(((Schelling) state).params, this, null), "Parameters");
        insp.addInspector(new TitledSimpleInspector(((Schelling) state).dataCollector, this, null), "Data");
        return insp;
    }

    public void start() {
        super.start();
        // set up our portrayals
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        // we now have new grids.  Set up the portrayals to reflect that
        setupPortrayals();
    }

    // This is called by start() and by load() because they both had this code
    // so I didn't have to type it twice :-)
    public void setupPortrayals() {
        agentPortrayal.setMap(new sim.util.gui.SimpleColorMap(new Color[]{new Color(0, 0, 0, 0), new Color(64, 64, 64), Color.red, Color.blue}));
        agentPortrayal.setField(((Schelling) state).neighbors);

        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
    }

    public void init(Controller c) {
        super.init(c);

        // Make the Display2D.  We'll have it display stuff later.
        display = new Display2D(layout.getDisplayWidth(), layout.getDisplayWidth(), this, 1); // at 400x400, we've got 4x4 per array position
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // attach the portrayals
        display.attach(agentPortrayal, "Agents");

        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(Color.black);
        layout(displayFrame);
    }

    public void quit() {
        super.quit();

        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;  // let gc
        display = null;       // let gc
    }

}





