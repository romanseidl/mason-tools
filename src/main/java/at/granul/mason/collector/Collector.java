package at.granul.mason.collector;

import java.io.Serializable;

/**
 * Base class for constructing a Collector.
 * This is used in the DataCollector class method to add a watcher.
 * <p/>
 * For now the class must return either double, any Number Object (Double, Int, Long, etc.) or double[] or any Number[]
 * Maybe a typed class would be better suited.
 * I think I didn't use a typed class because of boxing/unboxing issues. The good old Java madness. Scala ftw...
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 * @todo consider replacing with a typed class
 */
public interface Collector extends Serializable {
    public Object getData();
}

