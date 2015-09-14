package at.granul.mason.example.scala

import at.granul.mason.collector.scala.DataCollector
import sim.engine.SimState
import sim.field.grid.IntGrid2D
import sim.util.Bag
import sim.util.Int2D

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
object Schelling {
  def main(args: Array[String]) {
    SimState.doLoop(classOf[Schelling], args)
    System.exit(0)
  }

  final val EMPTY: Int = 0
  final val UNAVAILABLE: Int = 1
  final val RED: Int = 2
  final val BLUE: Int = 3
}

class Schelling(seed: Long) extends SimState(seed) {
  var neighbors: IntGrid2D = null
  var emptySpaces: Bag = new Bag()
  var params: SchellingParameters = new SchellingParameters()
  var dataCollector: DataCollector = new DataCollector() {
    dataCollector.addWatcher("test", () =>  { new java.lang.Double(1.0)})
  }

  params.gridWidth = 100
  params.gridHeight = 100
  createGrids()

  def this(seed: Long, width: Int, height: Int) {
    this(seed)
  }

  protected def createGrids() {
    emptySpaces.clear
    neighbors = new IntGrid2D(params.gridWidth, params.gridHeight, 0)
    val g: Array[Array[Int]] = neighbors.field

    for (x <- 0 until params.gridWidth)
      for (y <- 0 until params.gridHeight) {
        val d: Double = random.nextDouble
        if (d < params.redProbability) g(x)(y) = Schelling.RED
        else if (d < params.redProbability + params.blueProbability) g(x)(y) = Schelling.BLUE
        else if (d < params.redProbability + params.blueProbability + params.emptyProbability) {
          g(x)(y) = Schelling.EMPTY
          emptySpaces.add(new Int2D(x, y))
        }
        else g(x)(y) = Schelling.UNAVAILABLE
      }
  }

  /** Resets and starts a simulation */
  override def start() {
    super.start()
    createGrids()

    for (x <- 0 until params.gridWidth)
      for (y <- 0 until params.gridHeight)
        schedule.scheduleRepeating(new Agent(x, y))
  }

}






