package at.granul.mason.example.scala

import sim.engine._
import sim.util._

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
class Agent extends Steppable {
  var loc: Int2D = null
  var neighborsX: IntBag = new IntBag(9)
  var neighborsY: IntBag = new IntBag(9)

  def this(x: Int, y: Int) {
    this()
    loc = new Int2D(x, y)
  }

  def step(state: SimState) {
    val sch: Schelling = state.asInstanceOf[Schelling]
    val params: SchellingParameters = sch.params
    val locs: Array[Array[Int]] = sch.neighbors.field
    val x: Int = loc.x
    val y: Int = loc.y

    if (locs(x)(y) < Schelling.RED) return
    if (sch.emptySpaces.numObjs == 0) return
    sch.neighbors.getNeighborsMaxDistance(loc.x, loc.y, params.neighborhood, false, neighborsX, neighborsY)

    var value: Double = 0
    val threshold: Int = params.threshold
    val numObjs: Int = neighborsX.numObjs
    val objsX: Array[Int] = neighborsX.objs
    val objsY: Array[Int] = neighborsY.objs
    val myVal: Int = locs(x)(y)

    for (i <- 0 until numObjs)
      if (locs(objsX(i))(objsY(i)) == myVal && !(objsX(i) == x && objsY(i) == y)) {
        value += 1.0 / Math.sqrt((x - objsX(i)) * (x - objsX(i)) + (y - objsY(i)) * (y - objsY(i)))
        if (value >= threshold) return
      }

    val newLocIndex: Int = state.random.nextInt(sch.emptySpaces.numObjs)
    val newLoc: Int2D = (sch.emptySpaces.objs(newLocIndex)).asInstanceOf[Int2D]
    sch.emptySpaces.objs(newLocIndex) = loc

    val swap: Int = locs(newLoc.x)(newLoc.y)
    locs(newLoc.x)(newLoc.y) = locs(loc.x)(loc.y)
    locs(loc.x)(loc.y) = swap
    loc = newLoc
  }
}




