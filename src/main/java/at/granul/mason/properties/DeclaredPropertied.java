package at.granul.mason.properties;

import sim.util.Propertied;
import sim.util.Properties;

import java.io.Serializable;

/**
 * This is an alternative way (to e.g. annotations)of defining Properties to be used when creating an object inspector.
 * The class returns properties based on a field called "declaredProperties". To define such properties you must
 * initialize this field in an object constructor without arguments.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 * @todo consider alternatives to the empty constructor - maybe a getter method would be better?
 */
public abstract class DeclaredPropertied implements Propertied, Serializable {
    public transient Iterable<PropertyElement> declaredProperties = null;
    transient DeclaredProperties ___properties = null;
    public String a = "";

    public Properties properties() {
        if (___properties == null) {
            //Get default value from default constructor! This is because this in not serialized to allow for methods in the parameters
            if (declaredProperties == null)
                try {
                    declaredProperties = this.getClass().newInstance().declaredProperties;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            ___properties = new DeclaredProperties(this, declaredProperties);
        }
        return ___properties;
    }
}
