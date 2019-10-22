package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.bandits.EpsilonGreedyBandit
import io.github.carrknight.imitators.ExploreExploitImitate
import net.cohesyslab.dc.imitators.StochasticObservationPredicate
import net.cohesyslab.dc.utils.DoubleSetter
import net.cohesyslab.dc.utils.Getter
import net.cohesyslab.dc.utils.InRange
import net.cohesyslab.dc.utils.RichArgument
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
    args(0).getChooser.choice(context).toLogoObject
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

object LastObservationPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooser.lastObservation
      .map(obs => Seq(obs.getChoiceMade, obs.getResultObserved).toLogoList)
      .getOrElse(Nobody)
}

object ExplorationProbabilityPrim extends Getter[Chooser[_, _, _]] {
  override def get(chooser: Chooser[_, _, _]): Any =
    chooser match {
      case chooser: EpsilonGreedyBandit[_, _, _] =>
        chooser.getEpsilon
      case chooser: ExploreExploitImitate[_, _, _] =>
        chooser.getExplorationRule match {
          case rule: StochasticObservationPredicate[_, _, _] => rule.explorationProbability
          case _ => throw new ExtensionException("Exploration rule of " + chooser + " is not stochastic.")
        }
      case _ => throw new ExtensionException("Chooser " + chooser + " does not have an exploration probability.")
    }
}

object SetExplorationProbabilityPrim extends DoubleSetter[Chooser[AnyRef, Double, Null]] {
  override val validationRule: ValidationRule[Double] = InRange(0, 1)
  override def set(chooser: Chooser[AnyRef, Double, Null], value: Double): Unit =
    chooser match {
      case chooser: EpsilonGreedyBandit[AnyRef, Double, Null] =>
        chooser.setEpsilon(value)
      case chooser: ExploreExploitImitate[AnyRef, Double, Null] =>
        chooser.setExplorationRule(new StochasticObservationPredicate[AnyRef, Double, Null](value))
      case _ => throw new ExtensionException("Chooser " + chooser + " does not have an exploration probability.")
    }
}