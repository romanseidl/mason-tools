package at.granul.mason.collector;

import sim.engine.SimState;
import sim.engine.Steppable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Abstract base DataWriter
 * A DataWriter is a Steppable (Schedulable object) that writes Data from a DaataCollector to an output
 * This should be scheduled to write at every moment in time where data should be written.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public abstract class DataWriter implements Steppable {
    public static final DateFormat DF = new SimpleDateFormat("yyyy_MM_dd_HHmmss");

    DataCollector dataCollector;
    boolean writeArrayData = true;

    public DataWriter(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    public void setWriteArrayData(boolean writeArrayData) {
        this.writeArrayData = writeArrayData;
    }

    /**
     * Writes current state
     * @param p1
     */
    public abstract void step(SimState p1);

    public void stop() {};
}
