package at.granul.mason.properties.scala

import scala.collection.JavaConverters

/**
 *
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
case class Property(@transient _name : String) extends at.granul.mason.properties.Property(_name)
case class Header(@transient _name : String) extends at.granul.mason.properties.Header(_name)
case class DeclaredProperties(@transient _obj : AnyRef, pe : scala.collection.Iterable[ at.granul.mason.properties.PropertyElement]) extends at.granul.mason.properties.DeclaredProperties(_obj, JavaConverters.asJavaIterableConverter(pe).asJava)
