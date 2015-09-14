package at.granul.mason.properties;

/**
 * The Header is a special property Element that emulates a read only static field having only a name
 * It is of special use to the "TitledSimpleInspector" where Headers are used to structure the list.
 *
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Header extends PropertyElement{
    private static final long serialVersionUID = 1L;

    public Header(String name) {
        super(name);
    }
}


