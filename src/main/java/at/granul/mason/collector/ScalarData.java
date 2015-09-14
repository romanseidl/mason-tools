package at.granul.mason.collector;

import java.io.Serializable;

/**
 * Interface used by DataCollector to store access functions to doubles
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
interface ScalarData extends Serializable {
    public Double getData();
}
