package net.cohesyslab.dc

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions.RichAny
import org.nlogo.api.ScalaConversions.RichSeq
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax

object ChoicePrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(
    right = List(WildcardType), // the chooser
    ret = WildcardType // the choice made
  )
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.choice.toLogoObject
}

object ObservePrim extends Command {
  override def getSyntax: Syntax = commandSyntax(
    right = List(
      WildcardType, // the chooser object
      WildcardType, // the choice made
      NumberType // the result observed
    )
  )
  override def perform(args: Array[Argument], context: Context): Unit =
    args(0).getChooser.observe(args(1).get, args(2).getDoubleValue)
}

object ExpectationsPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.expectations.map(_.toLogoList).toLogoList
}

object OptionsPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.options.toLogoList
}

object BestOptionsPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = WildcardType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.bestOptions.toLogoList
}

object BestOptionPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = WildcardType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.bestOption(context.getRNG).toLogoObject
}
