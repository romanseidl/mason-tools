package at.granul.mason.collector.scala

import at.granul.mason.collector.Collector

import scala.reflect.runtime.universe._

/**
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
class DataCollector extends at.granul.mason.collector.DataCollector {

  def addWatcher[String, T <: () => AnyRef](name: String, func: T)(implicit tag: TypeTag[T]) {
    typeOf[T] match {
      case t if t =:= typeOf[() => java.lang.Double] => {
        super.addWatcher(name.asInstanceOf[java.lang.String], new Collector() {
          def getData(): java.lang.Double = func.asInstanceOf[() => java.lang.Double].apply()
        })
      }
      case t if t <:< typeOf[() => Array[Double]] => {
        super.addWatcher(name.asInstanceOf[java.lang.String], new Collector() {
          def getData: Array[Double] = func.asInstanceOf[() => Array[Double]].apply()
        })
      }
      case _ =>
        super.addWatcher(name.asInstanceOf[java.lang.String], new Collector() {
          def getData = func
        })
    }
  }
}
