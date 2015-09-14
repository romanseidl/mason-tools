package at.granul.mason.example.schelling;

import at.granul.mason.properties.DeclaredPropertied;
import at.granul.mason.properties.Header;
import at.granul.mason.properties.Property;
import sim.util.Interval;

import java.util.Arrays;

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
public class SchellingParameters extends DeclaredPropertied {
    public int gridHeight;
    public int gridWidth;
    public int neighborhood = 1;
    public int threshold = 3;
    public double redProbability = 0.3;
    public double blueProbability = 0.3;
    public double emptyProbability = 0.3;
    public double unavailableProbability = 0.1;

    {
        this.declaredProperties = Arrays.asList(
                new Header("Init"),
                new Property("gridHeight").title("Width").dom(10, 500),
                new Property("gridWidth").title("Height").dom(10, 500),
                new Property("neighborhood").title("Neighbourhood Size").dom(1, 10),
                new Header("Behaviour"),
                new Property("threshold").title("Tolerance Threshold").dom(1, 10),
                new Property("redProbability").title("Red Share").dom(0.0, 1.0),
                new Property("blueProbability").title("Blue Share").dom(0.0, 1.0),
                new Property("emptyProbability").title("Empty Share").dom(0.0, 2.0),
                new Property("unavailableProbability").title("Unavailable Share").dom(0.0, 2.0)
        );
    }
}
