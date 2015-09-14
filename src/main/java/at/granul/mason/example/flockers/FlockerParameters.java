package at.granul.mason.example.flockers;

import at.granul.mason.properties.DeclaredPropertied;
import at.granul.mason.properties.Header;
import at.granul.mason.properties.Property;
import java.util.Arrays;

/**
 *  Based on Flockers Example from Mason, copyright 2006 by Sean Luke and George Mason University
 *  copyright by Roman Seidl, 2014
 *
 *  Licensed under the Academic Free License version 3.0
 *  See the file "LICENSE" for more information
 *
 *  @author Roman Seidl
 *  @version 1.0
 *
 */
public class FlockerParameters extends DeclaredPropertied {

    public int width = 200;
    public int height = 200;
    public int numFlockers = 200;
    public double cohesion = 1.5;
    public double avoidance = 3.0;
    public double randomness = 0.25;
    public double consistency = 1.25;
    public double momentum = 2.0;
    public double deadFlockerProbability = 0.1;
    public double neighborhood = 14;
    public double jump = 1;  // how far do we move in a timestep?


    {
        this.declaredProperties = Arrays.asList(
                new Header("Init"),
                new Property("width").title("Width").dom(10,500),
                new Property("height").title("Height").dom(10,500),
                new Property("numFlockers").title("Number of Flockers").dom(10,2000),
                new Header("Behaviour"),
                new Property("cohesion").title("Cohesion").dom(0.0,3.0),
                new Property("avoidance").title("Avoidance").dom(0.0,10.0),
                new Property("randomness").title("Randomness").dom(0.0,2.0),
                new Property("consistency").title("Consistency").dom(0.0,2.0),
                new Property("momentum").title("Momentum").dom(0.0,4.0),
                new Property("deadFlockerProbability").title("Dead Flocker Propability").dom(0.0,1.0),
                new Property("neighborhood").title("Neighbourhood").dom(1.0,20.0),
                new Property("jump").title("Jump").dom(0.0,2.0)
        );
    }
}
