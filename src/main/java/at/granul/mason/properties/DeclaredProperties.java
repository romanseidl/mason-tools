package at.granul.mason.properties;

import sun.reflect.FieldAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Class that implements sim.util.Properties for an object based on the PropertyElements given
 *
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class DeclaredProperties extends sim.util.Properties {
    private static final long serialVersionUID = 1L;

    public DeclaredProperties(Object obj, java.lang.Iterable<PropertyElement> p) {
        this.object = obj;
        this.setProperties(p);
    }

    ArrayList<PropertyElement> elements = new ArrayList<PropertyElement>();

    ArrayList<Method> getMethods = new ArrayList<Method>();
    ArrayList<Method> setMethods = new ArrayList<Method>();
    ArrayList<Field> fields = new ArrayList<Field>();

    ArrayList<HiddenMethod> hiddenMethods = new ArrayList<HiddenMethod>();
    boolean _volatile = false;

    public DeclaredProperties setProperties(java.lang.Iterable<PropertyElement> p) {
        for (PropertyElement propertyElement : p) {
            this.add(propertyElement);
        }
        return this;
    }


    private Field fieldForName(String name) {
        try {
            return object.getClass().getField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getterForName(String name) {
        return findMethodByNames(new String[]{ "get" + capitalize(name), "is" + capitalize(name), name});
    }

    private Method findMethodByNames(String names[]) {
        final Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
            for (int j = 0; j < names.length; j++)
                if(methods[i].getName().equals(names[j]))
                    return methods[i];
        return null;
    }

    public static String capitalize(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1);
    }

    public DeclaredProperties add(PropertyElement prop) {
            elements.add(prop);
            if( prop instanceof Property)
            {
                final Method getter = getterForName(prop.name);
                getMethods.add(getter);
                if(getter == null)
                {
                    fields.add(fieldForName(prop.name));
                    setMethods.add(null);
                }
                else
                {
                    fields.add(null);
                    setMethods.add(findMethodByNames(new String[]{"set" + capitalize(prop.name), prop.name + "_$eq"}));
                }

                final PropertyElement p = prop;

                HiddenMethod hiddenMethod = null;
                final Object hiddenMethodObject = p.getHiddenMethod();
                if (hiddenMethodObject != null){
                    if(hiddenMethodObject instanceof String) {
                        hiddenMethod = new HiddenMethod() { public boolean isHidden() {
                            try {
                                return (Boolean) getterForName(hiddenMethodObject.toString()).invoke(object);
                            } catch (Exception e) {
                                return false;
                            }
                        } };
                    } else if (hiddenMethodObject instanceof HiddenMethod ){
                        hiddenMethod = (HiddenMethod) hiddenMethodObject;
                    }
                }
                hiddenMethods.add(hiddenMethod);
            }
            else {
                getMethods.add(null);
                setMethods.add(null);
                fields.add(null);
                hiddenMethods.add(null);
            }
        return this;
    }

    public int numProperties() { return elements.size(); }

    public void setVolatile() { _volatile = true ; }

    public boolean isVolatile()  { return _volatile; }

    public Object getValue(int index) {
        if(elements.get(index) instanceof Header){
            return elements.get(index).name;
        } else {
            final Method method = getMethods.get(index);
            if(method != null)
                try {
                    return method.invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            else {
                final Field field = fields.get(index);
                if(field != null) {
                    try {
                        return field.get(object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
          }
        return null;
    }

    public Object _setValue(int index, int value) {
        return null;
    }

    public Object _setValue(int index, Object value) {
        if(isReadWrite(index))
        {
            final Method method = setMethods.get(index);
            if(method != null)
                try {
                    method.invoke(object, value);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            else {
                final Field field = fields.get(index);
                if(field != null) {
                    try {
                        field.set(object, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                else
                    return null;
            }
        }
        return getValue(index);
    }

    public boolean isHidden(int index) {
        final HiddenMethod method = hiddenMethods.get(index);
        if(method != null)
            return method.isHidden();
        else
            return false;
    }

    public Object getDomain(int index) { return elements.get(index).getDomain(); }

    public boolean isReadWrite(int index) { return !elements.get(index).isReadOnly();}

    public String getName(int index) { return elements.get(index).getTitle(); }

    public String getDescription(int index) { return elements.get(index).getDesc(); }

    public Class getType(int index) {
        if(elements.get(index) instanceof Header)
            return Header.class;
        else
        {
            final Method method = getMethods.get(index);
            if(method != null)
                return method.getReturnType();
            else {
                final Field field = fields.get(index);
                if(field != null)
                    return field.getType();
                else
                    return null;
            }
        }
    }
    public String toString() { return super.toString() + " obj: " + object + " elements:"+ elements; }
}

