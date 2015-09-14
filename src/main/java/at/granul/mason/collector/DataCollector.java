package at.granul.mason.collector;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Propertied;
import sim.util.Properties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * DataCollector Storing runtime Data. For the moment it will only allow Number, Number[] and double, double[].
 * I don't know if this is smart at all. In practice almost everything seems to be a double.
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class DataCollector implements Propertied, Steppable, Serializable {
    private static final long serialVersionUID = 1L;

    ArrayList<String> watcherNames = new ArrayList<String>();
    ArrayList<Collector> watcherFunctions = new ArrayList<Collector>();

    ArrayList<Object> values = new ArrayList<Object>();

    //Stores the Array and scala Values references seperately for the graphics
    public ArrayList<String> scalarNames = new ArrayList<String>();
    public ArrayList<ScalarData> scalarData =  new ArrayList<ScalarData>();

    public ArrayList<String> arrayNames = new ArrayList<String>();
    public ArrayList<ArrayData> arrayData = new ArrayList<ArrayData>();

    boolean update = true;

    public void addWatcher(String name, Collector collector) {
        watcherNames.add(name);
        watcherFunctions.add(collector);

        String a = "";
        try {
//            a.getClass().getMethod("getData", null);
            Class getterType = collector.getClass().getMethod("getData", null).getReturnType();
            if(getterType == Double.class){
                scalarNames.add(name);
                final int i = values.size();
                scalarData.add( new ScalarData() {
                    @Override
                    public Double getData() {
                        Object r = values.get(i);
                        if(r instanceof Double)
                            return (Double) values.get(i);
                        else
                            return null;
                    }
                });
            } else if(getterType == double[].class){// || (new Double[0]).getClass()){
                arrayNames.add(name);
                final int i = values.size();
                arrayData.add( new ArrayData() {
                    @Override
                    public double[] provide() {
                        Object r = values.get(i);
                        if(r instanceof double[])
                            return (double[]) values.get(i);
                        else
                            return null;
                    }
                });
            } else if(Number.class.isAssignableFrom(getterType)){
                    scalarNames.add(name);
                    final int i = values.size();
                    scalarData.add( new ScalarData() {
                        @Override
                        public Double getData() {
                            Object r = values.get(i);
                            if(r instanceof Number)
                                return ((Number) values.get(i)).doubleValue();
                            else
                                return null;
                        }
                    });
            } else if(Number[].class.isAssignableFrom(getterType)) {
                arrayNames.add(name);
                final int i = values.size();
                arrayData.add(new ArrayData() {
                    @Override
                    public double[] provide() {
                        Object r = values.get(i);
                        if (r instanceof Number[]){
                            Number [] source =  (Number[]) r;
                            double[] dest = new double[source.length];
                            for(int i=0; i<source.length; i++) {
                                dest[i] = source[i].doubleValue();
                            }
                            return dest;
                        }

                        else
                            return null;
                    }
                });
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        values.add(collector);

    }

    /**
     * Updates all Fetchers
     * @param p1
     */
    public void step(SimState p1) {
        if(update)
            for (int i = 0; i < watcherNames.size(); i++)
                values.set(i, watcherFunctions.get(i).getData());
    }


    /**
     * Mason Properties Class
     * @return
     */
    public Properties properties() {
      return new Properties() {
        DataCollector collector  = DataCollector.this;

        public boolean isVolatile() { return false; }

        public int numProperties() { return collector.watcherNames.size() + 1; }

        public Object getValue(int index) {
            return (numProperties() > index) ?
                        ( index==0 ? collector.update : collector.values.get(index - 1) )
                        : null;
        }

        public boolean isReadWrite(int index) { return index == 0; }

        public String getName(int index) { return index == 0 ? "Update?" : collector.watcherNames.get(index - 1); }

        //def getType(index: Int): Class[_] = collector.watcherTypes(index)
        public Class getType(int index) { return index > 0 ? this.getValue(index).getClass() : Boolean.TYPE; }

        public Object _setValue(int index, Object value) {
            if(index == 0)
                collector.update = (Boolean) value;
            return null;
        }
      };
    }
}

