package at.granul.mason.collector;

import sim.engine.SimState;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * DataWriter that writes to a csv file.
 * Has to be constructed with a Java Writer to write to.
 * Will write array data.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class CSVDataWriter extends DataWriter{
    Writer writer;
    boolean headerWritten = false;

    public CSVDataWriter(DataCollector dataCollector, Writer writer) {
        super(dataCollector);
        this.writer = writer;
        setWriteArrayData(false);
    }

    void writeHeader() throws IOException {
        writer.append("time, ");

        for (int i = 0; i < dataCollector.scalarNames.size(); i++){
            if(i != 0) writer.append(", ");
            writer.append(dataCollector.scalarNames.get(i));
        }

        if(writeArrayData){
            if(dataCollector.scalarNames.size() > 0) writer.append(", ");

            for (int i = 0; i < dataCollector.arrayNames.size(); i++){
                if(i != 0) writer.append(", ");
                writer.append(dataCollector.arrayNames.get(i));
            }
        }
        writer.append('\n');

        headerWritten = true;
    }

    @Override
    public void step(SimState p1) {
        try {
            if(!headerWritten)
                    writeHeader();


            writer.append(p1.schedule.getTime() + "");
            writer.append(", ");

            final int scalarSize = dataCollector.scalarNames.size();
            for (int i = 0; i < scalarSize; i++){
                if(i != 0) writer.append(", ");
                writer.append(dataCollector.scalarData.get(i).getData() + "");
            }

            if(writeArrayData) {
                if(scalarSize > 0) writer.append(", ");
                for (int i = 0; i < dataCollector.arrayNames.size(); i++){
                    if(i != 0) writer.append(", ");
                    writer.append(Arrays.toString(dataCollector.arrayData.get(i).provide()));
                }
            }
            writer.append('\n');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
