package at.granul.mason.example.schelling;

import at.granul.mason.collector.Collector;
import at.granul.mason.collector.DataCollector;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * Based on Schelling Example from Mason, copyright 2006 by Sean Luke and George Mason University
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Schelling extends SimState {
    public IntGrid2D neighbors;
    public Bag emptySpaces = new Bag();
    public static final int EMPTY = 0;
    public static final int UNAVAILABLE = 1;
    public static final int RED = 2;
    public static final int BLUE = 3;

    public SchellingParameters params = new SchellingParameters();

    public DataCollector dataCollector = new DataCollector();

    {
        dataCollector.addWatcher("test", new Collector() {
            public Object getData() {
                return new Integer(1);
            }
        });
    }

    /**
     * Creates a Schelling simulation with the given random number seed.
     */
    public Schelling(long seed) {
        this(seed, 100, 100);
    }

    public Schelling(long seed, int width, int height) {
        super(new MersenneTwisterFast(seed));
        params.gridWidth = width;
        params.gridHeight = height;
        createGrids();
    }


    protected void createGrids() {
        emptySpaces.clear();
        neighbors = new IntGrid2D(params.gridWidth, params.gridHeight, 0);
        int[][] g = neighbors.field;
        for (int x = 0; x < params.gridWidth; x++)
            for (int y = 0; y < params.gridHeight; y++) {
                double d = random.nextDouble();
                if (d < params.redProbability) g[x][y] = RED;
                else if (d < params.redProbability + params.blueProbability) g[x][y] = BLUE;
                else if (d < params.redProbability + params.blueProbability + params.emptyProbability) {
                    g[x][y] = EMPTY;
                    emptySpaces.add(new Int2D(x, y));
                } else g[x][y] = UNAVAILABLE;
            }
    }

    /**
     * Resets and starts a simulation
     */
    public void start() {
        super.start();  // clear out the schedule

        // make new grids
        createGrids();
        for (int x = 0; x < params.gridWidth; x++)
            for (int y = 0; y < params.gridHeight; y++) {
                schedule.scheduleRepeating(new Agent(x, y));
            }
        schedule.scheduleRepeating(dataCollector, Integer.MAX_VALUE, 1);
    }

    public static void main(String[] args) {
        doLoop(Schelling.class, args);
        System.exit(0);
    }
}





