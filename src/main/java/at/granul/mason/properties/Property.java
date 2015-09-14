package at.granul.mason.properties;

/**
 * This is a declared property. It is constructed using a "name" (referring to the linked java bean property or
 * instance variable of the class) and the various method can be called to further refine the definition.
 * <p/>
 * Commonly one would use the methods that return the obejct to construct the Property in onw row.
 * e.g: new Property("unavailableProbability").title("Unavailable Share").dom(0.0, 2.0)
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Property extends PropertyElement{
    private static final long serialVersionUID = 1L;

    String title;
    SerializableDomain domain = null;
    String desc = null;
    boolean readOnly = false;
    Object hiddenMethod = null;

    /**
     * Constructs a Property
     * @param name
     */
    public Property(String name) {
        super(name);
    }

    /**
     * A Scala case class defined in Java...
     * @param name
     */
    public static Property apply(String name) {
        return new Property(name);
    }

    public String getTitle() { return title != null ? title : name; }

    /**
     * @see #title(String title)
     */
    public void setTitle(String title) {
        title(title);
    }

    public Object getDomain() {
        return domain != null ? domain.toIntervalOrArray() : null;
    }

    /**
     * Sets a variable domain (range) - will commonly produce a slider
     * @param domain
     */
    public void setDomain(SerializableDomain domain) {
        this.domain = domain;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * @see #desc(String)
     */
    public void setDesc(String desc) {
        desc(desc);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Set the element to read only (default = false)
     * @see #readOnly()
     * @param isReadOnly
     */
    public void setReadOnly(boolean isReadOnly) {
        this.readOnly = isReadOnly;
    }

    public Object getHiddenMethod() {
        return hiddenMethod;
    }

    /**
     * Set this to be a hidden method
     * @todo check what the us of this is
     * @param hiddenMethod
     */
    public void setHiddenMethod(Object hiddenMethod) {
        this.hiddenMethod = hiddenMethod;
    }

    /**
     * Set the domain to the int range
     * @param lower
     * @param upper
     * @return
     */
    public Property dom(long lower, long upper) {
        domain = new SerializableDomain(lower, upper);
        return this;
    }

    /**
     * Set the domain to the double range
     * @param lower
     * @param upper
     * @return
     */
    public Property dom(double lower, double upper) {
        domain = new SerializableDomain(lower, upper);
        return this;
    }

    /**
     * Set the domain to a list of possible String elements - will commonly produce a drop-down element
     * @param dom
     * @return
     */
    public Property dom(String[] dom) {
        domain = new SerializableDomain(dom);
        return this;
    }

    /**
     * Sets a description - will produce a dropdown info in TitledInspector
     * Can be HTML-Formatted text
     * @param d
     * @return Property - allows to call on multiple functions in one row
     */
    public Property desc(String d) {
        desc = d;
        return this;
    }

    /**
     * Changes the title of the element ot something different than the variable / property name
     * @param tf
     * @return Property - allows to call on multiple functions in one row
     */
    public Property title(String tf) {
        title = tf;
        return this;
    }

    /**
     * Will set a method that is evaluated in whether this property is hidden
     * @param tf Name of the Method
     * @return Property - allows to call on multiple functions in one row
     */
    public Property hiddenMethod(String tf) {
        hiddenMethod = tf;
        return this;
    }

    protected Property hiddenMethod(HiddenMethod hm) {
        hiddenMethod = hm;
        return this;
    }

    /**
     * Will set the property to be hidden
     * @return Property - allows to call on multiple functions in one row
     */
     public Property hidden() {
        hiddenMethod = new HiddenMethod() { public boolean isHidden() { return true ; } };
        return this;
     }

    /**
     * Sets the property to read only
     * @return Property - allows to call on multiple functions in one row
     */
     public Property readOnly() {return ro(); }

    /**
     * @see #readOnly()
     */
     public Property ro() {
        readOnly = true;
        return this;
     }
}

