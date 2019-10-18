package net.cohesyslab.dc.utils

import io.github.carrknight.Chooser
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

import scala.reflect.ClassTag

abstract class Getter[C <: Chooser[_, _, _] : ClassTag] extends Reporter {
  def get(chooser: C): Any
  override def getSyntax: Syntax =
    reporterSyntax(right = List(WildcardType), ret = NumberType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    get(args(0).getChooserAs[C]).toLogoObject
}
