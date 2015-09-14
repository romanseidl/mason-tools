package at.granul.mason.example.scala

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
import at.granul.mason.ChartedGUIState
import at.granul.mason.collector.DataCollector
import at.granul.mason.inspector.TitledSimpleInspector
import sim.engine._
import sim.display._
import sim.portrayal.Inspector
import sim.portrayal.grid._
import sim.portrayal.inspector.TabbedInspector
import java.awt._
import javax.swing._
import sim.util.gui.SimpleColorMap

object SchellingWithUI {
  def main(args: Array[String]) {
    ChartedGUIState.setNativeLookAndFeel()
    val schelling: SchellingWithUI = new SchellingWithUI
    val c: Console = new Console(schelling)
    c.setVisible(true)
  }
}

class SchellingWithUI(state: SimState) extends ChartedGUIState(state) {
  var display: Display2D = null
  var displayFrame: JFrame = null
  var agentPortrayal: FastValueGridPortrayal2D = new FastValueGridPortrayal2D("Agents")

  def this() {
    this(new Schelling(System.currentTimeMillis))
  }

  def getDataCollector: DataCollector = {
    return (state.asInstanceOf[Schelling]).dataCollector
  }

  override def getInspector: Inspector = {
    val insp: TabbedInspector = new TabbedInspector
    insp.addInspector(new TitledSimpleInspector((state.asInstanceOf[Schelling]).params, this, null), "Parameters")
    insp.addInspector(new TitledSimpleInspector((state.asInstanceOf[Schelling]).dataCollector, this, null), "Data")
    return insp
  }

  override def start() {
    super.start()
    setupPortrayals()
  }

  override def load(state: SimState) {
    super.load(state)
    setupPortrayals()
  }

  def setupPortrayals() {
    agentPortrayal.setMap(new SimpleColorMap(Array[Color](new Color(0, 0, 0, 0), new Color(64, 64, 64), Color.red, Color.blue)))
    agentPortrayal.setField((state.asInstanceOf[Schelling]).neighbors)
    display.reset()
    display.repaint()
  }

  override def init(c: Controller) {
    super.init(c)
    display = new Display2D(layout.getDisplayWidth, layout.getDisplayWidth, this, 1)
    displayFrame = display.createFrame
    c.registerFrame(displayFrame)
    displayFrame.setVisible(true)
    display.attach(agentPortrayal, "Agents")
    display.setBackdrop(Color.black)
    layout(displayFrame)
  }

  override def quit() {
    super.quit()
    if (displayFrame != null) displayFrame.dispose()
    displayFrame = null
    display = null
  }
}






