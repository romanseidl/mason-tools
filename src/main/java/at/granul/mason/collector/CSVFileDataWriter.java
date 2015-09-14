package at.granul.mason.collector;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CSVDataWriter that writes to a File
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class CSVFileDataWriter extends CSVDataWriter{
    public CSVFileDataWriter(DataCollector dataCollector, String prefix) throws IOException {
        super(dataCollector,  new FileWriter(prefix + "_" + new File(DataWriter.DF.format(new Date()) + ".csv")));
    }

    public void stop() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
