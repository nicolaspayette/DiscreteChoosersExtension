package net.cohesyslab.dc

import net.cohesyslab.dc.bandits.EpsilonGreedyChooser
import net.cohesyslab.dc.imitators.ExploreExploitImitateChooser
import net.cohesyslab.dc.imitators.StochasticObservationPredicate
import net.cohesyslab.dc.utils.DoubleSetter
import net.cohesyslab.dc.utils.Getter
import net.cohesyslab.dc.utils.InRange
import net.cohesyslab.dc.utils.RichAnyRef
import net.cohesyslab.dc.utils.ValidationRule
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.ExtensionException
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions.RichAny
import org.nlogo.api.ScalaConversions.RichSeq
import org.nlogo.core.Nobody
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax

// TODO: test that all primitives work (or fail gracefully) with all kinds of choosers

object ChoicePrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(
    right = List(WildcardType), // the chooser
    ret = WildcardType // the choice made
  )
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].choice(context).toLogoObject
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
    args(0).get.as[Chooser].observe(args(1).get, args(2).getDoubleValue)
}

object ValuePrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(
    right = List(
      WildcardType, // the chooser object
      WildcardType // the option of which we want to know the value
    ),
    ret = NumberType // the value of the option
  )
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].optionValue(args(1).get).value
}

object OptionValuesPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].optionValues.map(_.toLogoList).toLogoList
}

object OptionsPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].options.toLogoList
}

object BestOptionsPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = WildcardType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].bestOptions.toLogoList
}

object BestOptionPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = WildcardType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].bestOption(context.getRNG).toLogoObject
}

object LastObservationPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[Chooser].lastObservation
      .map(obs => Seq(obs.getChoiceMade, obs.getResultObserved).toLogoList)
      .getOrElse(Nobody)
}

object ExplorationProbabilityPrim extends Getter[Chooser] {
  override def get(chooser: Chooser): Any =
    chooser match {
      case chooser: EpsilonGreedyChooser =>
        chooser.delegate.getEpsilon
      case chooser: ExploreExploitImitateChooser =>
        chooser.delegate.getExplorationRule match {
          case rule: StochasticObservationPredicate[_, _, _] => rule.explorationProbability
          case _ => throw new ExtensionException("Exploration rule of " + chooser + " is not stochastic.")
        }
      case _ => throw new ExtensionException("Chooser " + chooser + " does not have an exploration probability.")
    }
}

object SetExplorationProbabilityPrim extends DoubleSetter[Chooser] {
  override val validationRule: ValidationRule[Double] = InRange(0, 1)
  override def set(chooser: Chooser, value: Double): Unit =
    chooser match {
      case chooser: EpsilonGreedyChooser =>
        chooser.delegate.setEpsilon(value)
      case chooser: ExploreExploitImitateChooser =>
        chooser.delegate.setExplorationRule(new StochasticObservationPredicate[AnyRef, Double, Null](value))
      case _ => throw new ExtensionException("Chooser " + chooser + " does not have an exploration probability.")
    }
}