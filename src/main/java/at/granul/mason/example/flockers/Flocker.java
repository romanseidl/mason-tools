package at.granul.mason.example.flockers;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
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
public class Flocker implements Steppable, sim.portrayal.Orientable2D {
    private static final long serialVersionUID = 1;

    public Double2D loc = new Double2D(0, 0);
    public Double2D lastd = new Double2D(0, 0);
    public Continuous2D flockers;
    public Flockers theFlock;
    public boolean dead = false;

    public Flocker(Double2D location) {
        loc = location;
    }

    public Bag getNeighbors() {
        return flockers.getNeighborsExactlyWithinDistance(loc, theFlock.params.neighborhood, true);
    }

    public double getOrientation() {
        return orientation2D();
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean val) {
        dead = val;
    }

    public void setOrientation2D(double val) {
        lastd = new Double2D(Math.cos(val), Math.sin(val));
    }

    public double orientation2D() {
        if (lastd.x == 0 && lastd.y == 0) return 0;
        return Math.atan2(lastd.y, lastd.x);
    }

    public Double2D momentum() {
        return lastd;
    }

    public Double2D consistency(Bag b, Continuous2D flockers) {
        if (b == null || b.numObjs == 0) return new Double2D(0, 0);

        double x = 0;
        double y = 0;
        int i = 0;
        int count = 0;
        for (i = 0; i < b.numObjs; i++) {
            Flocker other = (Flocker) (b.objs[i]);
            if (!other.dead) {
                double dx = flockers.tdx(loc.x, other.loc.x);
                double dy = flockers.tdy(loc.y, other.loc.y);
                Double2D m = ((Flocker) b.objs[i]).momentum();
                count++;
                x += m.x;
                y += m.y;
            }
        }
        if (count > 0) {
            x /= count;
            y /= count;
        }
        return new Double2D(x, y);
    }

    public Double2D cohesion(Bag b, Continuous2D flockers) {
        if (b == null || b.numObjs == 0) return new Double2D(0, 0);

        double x = 0;
        double y = 0;

        int count = 0;
        int i = 0;
        for (i = 0; i < b.numObjs; i++) {
            Flocker other = (Flocker) (b.objs[i]);
            if (!other.dead) {
                double dx = flockers.tdx(loc.x, other.loc.x);
                double dy = flockers.tdy(loc.y, other.loc.y);
                count++;
                x += dx;
                y += dy;
            }
        }
        if (count > 0) {
            x /= count;
            y /= count;
        }
        return new Double2D(-x / 10, -y / 10);
    }

    public Double2D avoidance(Bag b, Continuous2D flockers) {
        if (b == null || b.numObjs == 0) return new Double2D(0, 0);
        double x = 0;
        double y = 0;

        int i = 0;
        int count = 0;

        for (i = 0; i < b.numObjs; i++) {
            Flocker other = (Flocker) (b.objs[i]);
            if (other != this) {
                double dx = flockers.tdx(loc.x, other.loc.x);
                double dy = flockers.tdy(loc.y, other.loc.y);
                double lensquared = dx * dx + dy * dy;
                count++;
                x += dx / (lensquared * lensquared + 1);
                y += dy / (lensquared * lensquared + 1);
            }
        }
        if (count > 0) {
            x /= count;
            y /= count;
        }
        return new Double2D(400 * x, 400 * y);
    }

    public Double2D randomness(MersenneTwisterFast r) {
        double x = r.nextDouble() * 2 - 1.0;
        double y = r.nextDouble() * 2 - 1.0;
        double l = Math.sqrt(x * x + y * y);
        return new Double2D(0.05 * x / l, 0.05 * y / l);
    }

    public void step(SimState state) {
        final Flockers flock = (Flockers) state;
        loc = flock.flockers.getObjectLocation(this);

        if (dead) return;

        Bag b = getNeighbors();

        Double2D avoid = avoidance(b, flock.flockers);
        Double2D cohe = cohesion(b, flock.flockers);
        Double2D rand = randomness(flock.random);
        Double2D cons = consistency(b, flock.flockers);
        Double2D mome = momentum();

        final FlockerParameters params = flock.params;
        double dx = params.cohesion * cohe.x + params.avoidance * avoid.x + params.consistency * cons.x + params.randomness * rand.x + params.momentum * mome.x;
        double dy = params.cohesion * cohe.y + params.avoidance * avoid.y + params.consistency * cons.y + params.randomness * rand.y + params.momentum * mome.y;

        // renormalize to the given step size
        double dis = Math.sqrt(dx * dx + dy * dy);
        if (dis > 0) {
            dx = dx / dis * params.jump;
            dy = dy / dis * params.jump;
        }

        lastd = new Double2D(dx, dy);
        loc = new Double2D(flock.flockers.stx(loc.x + dx), flock.flockers.sty(loc.y + dy));
        flock.flockers.setObjectLocation(this, loc);
    }

}
