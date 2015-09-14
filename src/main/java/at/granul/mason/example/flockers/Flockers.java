package at.granul.mason.example.flockers;


import at.granul.mason.collector.Collector;
import at.granul.mason.collector.DataCollector;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Based on Flockers Example from Mason, copyright 2006 by Sean Luke and George Mason University
 * copyright by Roman Seidl, 2014
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Flockers extends SimState {
    private static final long serialVersionUID = 1;
    public Continuous2D flockers;


    public FlockerParameters params = new FlockerParameters();

    public DataCollector dataCollector = new DataCollector();

    {
        dataCollector.addWatcher("orientation", new Collector() {
            public double[] getData() {
                final Object[] flock = flockers.getAllObjects().objs;
                final double[] data = new double[flock.length];
                for (int i = 0; i < flock.length; i++) {
                    if (flock[i] != null)
                        data[i] = ((Flocker) flock[i]).getOrientation();
                }
                ;
                return data;
            }
        });
        dataCollector.addWatcher("avgOrientation", new Collector() {
            public Double getData() {
                final Object[] flock = flockers.getAllObjects().objs;
                double sum = 0;
                long count = 0;
                for (int i = 0; i < flock.length; i++) {
                    if (flock[i] != null) {
                        sum += ((Flocker) flock[i]).getOrientation();
                        count++;
                    }
                }
                ;
                return sum / count;
            }
        });
    }

    /**
     * Creates a Flockers simulation with the given random number seed.
     */
    public Flockers(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        doLoop(Flockers.class, args);
        System.exit(0);
    }

    public Double2D[] getLocations() {
        if (flockers == null) return new Double2D[0];
        Bag b = flockers.getAllObjects();
        if (b == null) return new Double2D[0];
        Double2D[] locs = new Double2D[b.numObjs];
        for (int i = 0; i < b.numObjs; i++)
            locs[i] = flockers.getObjectLocation(b.objs[i]);
        return locs;
    }

    public Double2D[] getInvertedLocations() {
        if (flockers == null) return new Double2D[0];
        Bag b = flockers.getAllObjects();
        if (b == null) return new Double2D[0];
        Double2D[] locs = new Double2D[b.numObjs];
        for (int i = 0; i < b.numObjs; i++) {
            locs[i] = flockers.getObjectLocation(b.objs[i]);
            locs[i] = new Double2D(locs[i].y, locs[i].x);
        }
        return locs;
    }

    public void start() {
        super.start();

        // set up the flockers field.  It looks like a discretization
        // of about neighborhood / 1.5 is close to optimal for us.  Hmph,
        // that's 16 hash lookups! I would have guessed that
        // neighborhood * 2 (which is about 4 lookups on average)
        // would be optimal.  Go figure.
        flockers = new Continuous2D(params.neighborhood / 1.5, params.width, params.height);

        // make a bunch of flockers and schedule 'em.  A few will be dead
        for (int x = 0; x < params.numFlockers; x++) {
            Double2D location = new Double2D(random.nextDouble() * params.width, random.nextDouble() * params.height);
            Flocker flocker = new Flocker(location);
            if (random.nextBoolean(params.deadFlockerProbability)) flocker.dead = true;
            flockers.setObjectLocation(flocker, location);
            flocker.flockers = flockers;
            flocker.theFlock = this;
            schedule.scheduleRepeating(flocker);
        }
        schedule.scheduleRepeating(dataCollector, Integer.MAX_VALUE, 1);
    }
}
