package at.granul.mason.properties;

import java.io.Serializable;

/**
 * Abstract base class that defines the generally accessible elements of a PropertyElement.
 * <p/>
 * Property and Header are derived from this class
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public abstract class PropertyElement implements Serializable {
    String name;

    protected PropertyElement(String name) {
        this.name = name;
    }

    String getTitle() { return name; }

    Object getHiddenMethod() {return null;}

    String getDesc() { return null; }

    public Object getDomain() { return null; }

    public boolean isReadOnly() { return true; }
}
