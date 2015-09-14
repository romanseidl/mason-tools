package at.granul.mason.collector;

import sim.display.ChartUtilities;

import java.io.Serializable;

/**
 * There are two basic datatypes: Scalars and arrays. This is a datatype that supplies an array of doubles
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public interface ArrayData extends Serializable, ChartUtilities.ProvidesDoubles {
}
