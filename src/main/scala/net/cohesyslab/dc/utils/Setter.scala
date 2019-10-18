package net.cohesyslab.dc.utils

import io.github.carrknight.Chooser
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax

import scala.reflect.ClassTag

abstract class Setter[C <: Chooser[_, _, _] : ClassTag, V] extends Command {
  val validationRule: ValidationRule[Double] = AlwaysValid()
  def set(chooser: C, value: V): Unit
  def getArgumentValue(arg: Argument): V
  override def getSyntax: Syntax =
    commandSyntax(List(WildcardType, NumberType))
  override def perform(args: Array[Argument], context: Context): Unit =
    set(args(0).getChooserAs[C], getArgumentValue(args(1)))
}

abstract class DoubleSetter[C <: Chooser[_, _, _] : ClassTag] extends Setter[C, Double] {
  override def getArgumentValue(arg: Argument): Double = arg.getDoubleValue
}