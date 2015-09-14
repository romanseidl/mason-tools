package at.granul.mason.properties;

import at.granul.mason.*;
import sim.util.Interval;

import java.io.Serializable;
import java.util.List;

/**
 * Object representing a property domain being either an int or double range or a list of possible values.
 * <p/>
 * I think this has been necessary as the sim.util.Domain is neither serializable nor allows for value lists
 * (they are represented by arrays).
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class SerializableDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    Object params = null;
    transient Object interval = null;

    public SerializableDomain(double min, double max) {
        params = new double[]{min,max};
    }

    public SerializableDomain(long min, long max) {
        params = new long[]{min,max};
    }

    public SerializableDomain(String [] values) {
        params = values;
    }

    public Object toIntervalOrArray() {
        if (interval == null) {
            if (params instanceof long[])
                interval =  new Interval(((long[])params)[0], ((long[])params)[1]);
            else if (params instanceof double[])
                interval =  new Interval(((double[])params)[0], ((double[])params)[1]);
            else if (params instanceof String[])
                interval = params;
            else
                return null;
        }
        return interval;
    }
}