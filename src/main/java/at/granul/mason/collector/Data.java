package at.granul.mason.collector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public interface Data  {
    public List<LinkedList<Double>> getData();

    public List<String> getNames();
}
