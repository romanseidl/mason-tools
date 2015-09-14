package at.granul.mason.example.scala

import at.granul.mason.properties.DeclaredPropertied
import at.granul.mason.properties.scala.{Property, Header}
import scala.collection.JavaConversions._

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
class SchellingParameters extends DeclaredPropertied {
  var gridHeight: Int = 0
  var gridWidth: Int = 0
  var neighborhood: Int = 1
  var threshold: Int = 3
  var redProbability: Double = 0.3
  var blueProbability: Double = 0.3
  var emptyProbability: Double = 0.3
  var unavailableProbability: Double = 0.1

  declaredProperties = List(
    Header("Init"),
    Property("gridHeight").title("Width").dom(10, 500),
    Property("gridWidth").title("Height").dom(10, 500),
    Property("neighborhood").title("Neighbourhood Size").dom(1, 10),
    Header("Behaviour"),
    Property("threshold").title("Tolerance Threshold").dom(1, 10),
    Property("redProbability").title("Red Share").dom(0.0, 1.0),
    Property("blueProbability").title("Blue Share").dom(0.0, 1.0),
    Property("emptyProbability").title("Empty Share").dom(0.0, 2.0),
    Property("unavailableProbability").title("Unavailable Share").dom(0.0, 2.0)
  )
}


