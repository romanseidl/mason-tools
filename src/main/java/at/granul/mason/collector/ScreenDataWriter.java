package at.granul.mason.collector;

import sim.engine.SimState;

import java.util.Arrays;

/**
 * Data Writer which writes to "System.out".
 * This is a class for debugging only.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class ScreenDataWriter extends DataWriter {

    public ScreenDataWriter(DataCollector dataCollector) {
        super(dataCollector);
    }

    @Override
    public void step(SimState p1) {
        final StringBuffer line = new StringBuffer();

        line.append(p1.schedule.getTime());
        line.append(" - ");

        for (int i = 0; i < dataCollector.scalarNames.size(); i++){
            if(i != 0) line.append(", ");
            line.append(dataCollector.scalarNames.get(i));
            line.append(": ");
            line.append(dataCollector.scalarData.get(i).getData());
        }


        if(writeArrayData) {
            if(dataCollector.scalarNames.size() > 0) line.append(", ");

            for (int i = 0; i < dataCollector.arrayNames.size(); i++){
                if(i != 0) line.append(", ");
                line.append(dataCollector.arrayNames.get(i));
                line.append(": ");
                line.append(Arrays.toString(dataCollector.arrayData.get(i).provide()));
            }
        }
        System.out.println(line);
    }
}
