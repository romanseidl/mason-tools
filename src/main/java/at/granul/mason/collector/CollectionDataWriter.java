package at.granul.mason.collector;

import sim.engine.Schedule;
import sim.engine.SimState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * DataWriter that writes to collections stored in memory.
 * Ignores ArrayData (is Array Data important anyway?)
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class CollectionDataWriter extends DataWriter implements Data {


    ArrayList<LinkedList<Double>> scalarData;
    ArrayList <String> scalarNames;

    /*int width = 1000, height = 600;
    String prefix = "run";*/

    public CollectionDataWriter(DataCollector dataCollector) {
        super(dataCollector);
        scalarNames = new ArrayList <String>();
        scalarNames.add("Time");
        scalarNames.addAll(dataCollector.scalarNames);
        init();
    }

    public void init() {
        scalarData = new ArrayList<LinkedList<Double>>(scalarNames.size());

        //Time Series
        scalarData.add(new LinkedList<Double>());

        if(scalarNames != null)
            for (int i = 0; i < scalarNames.size(); i++) {
                scalarData.add(new LinkedList<Double>());
            }
    }

    @Override
    public void step(SimState state) {
        double x = state.schedule.getTime();

        // now add the data
        if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION) {
            scalarData.get(0).add(x);
            for (int a = 1; a < (scalarData.size()-1); a++) {
                scalarData.get(a).add(dataCollector.scalarData.get(a-1).getData());
            }
        }
    }

    @Override
    public ArrayList<LinkedList<Double>> getData() {
        return scalarData;
    }

    @Override
    public ArrayList<String> getNames() {
        return scalarNames;
    }

    public void stop()
    {
        System.out.println(Arrays.toString(scalarData.toArray()));
    }

}
