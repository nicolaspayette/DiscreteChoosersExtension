package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.Observation
import io.github.carrknight.bandits.AbstractBanditAlgorithm
import io.github.carrknight.imitators.ExploreExploitImitate
import io.github.carrknight.imitators.ParticleSwarm
import net.cohesyslab.dc.utils.AlwaysValid
import net.cohesyslab.dc.utils.ValidationRule
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.core.ExtensionObject
import org.nlogo.core.LogoList

import scala.collection.JavaConverters._

case class OptionValue(option: AnyRef, value: java.lang.Double) {
  def toLogoList = LogoList(option, value)
}

class WrappedChooser(val chooser: Chooser[AnyRef, Double, Null]) extends ExtensionObject {

  // TODO: the methods with pattern matching on chooser type could probably be handled by type classes

  val observedResultValidationRule: ValidationRule[Double] = AlwaysValid()
  private[this] var _lastObservation: Option[Observation[AnyRef, Double, Null]] = None

  override def getExtensionName: String = DiscreteChoosersExtension.name
  override def getNLTypeName: String = chooser.getClass.getSimpleName
  override def recursivelyEqual(obj: AnyRef): Boolean = eq(obj)
  override def dump(readable: Boolean, exporting: Boolean, reference: Boolean): String = hashCode.toHexString

  def observe(observedOption: AnyRef, observedResult: Double): Unit = {
    _lastObservation = Some(new Observation(
      AvailableOption(observedOption).get,
      observedResultValidationRule(observedResult).get,
      null
    ))
    _lastObservation.foreach(chooser.updateAndChoose(_))
  }

  def choice(context: Context): AnyRef = chooser.updateAndChoose(null)

  def bestOption(rng: MersenneTwisterFast): AnyRef =
    bestOptions match {
      case xs if xs.size == 1 => xs.head
      case xs => xs(rng.nextInt(xs.size))
    }

  def bestOptions: Vector[AnyRef] =
    chooser match {
      case eei: ExploreExploitImitate[AnyRef, _, Null] =>
        Vector(eei.getState.getFavoriteOption)
      case _ => {
        val expectations = this.optionValues
        val maxValue: Double = expectations.map(_.value).max
        expectations.filter(_.value == maxValue).map(_.option)
      }
    }

  def optionValues: Vector[OptionValue] = options.map(optionValue)

  def optionValue(option: AnyRef): OptionValue = {
    val validOption = AvailableOption(option).get
    chooser match {
      case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
        OptionValue(validOption, bandit.getBanditState.predict(validOption, null))
      case _: ExploreExploitImitate[_, _, _] =>
        throw new ExtensionException("Explore-Exploit-Imitate choosers do not maintain option values.")
      case _: ParticleSwarm[_, _, _] =>
        throw new ExtensionException("Particle swarm choosers do not maintain option values.")
    }
  }

  def options: Vector[AnyRef] =
    chooser match {
      case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
        bandit.getOptionsAvailable.entrySet.asScala.toVector.sortBy(_.getValue).map(_.getKey)
      case eei: ExploreExploitImitate[AnyRef, _, Null] =>
        eei.getOptionsAvailable.asScala.toVector
      case pso: ParticleSwarm[AnyRef, _, Null] =>
        pso.getOptionsAvailable.asScala.toVector // TODO: either get this through reflection or make send PR with getter to Ernesto
    }

  def lastObservation: Option[Observation[AnyRef, Double, Null]] = _lastObservation

  object AvailableOption extends ValidationRule[AnyRef] {
    override def isValid(value: AnyRef): Boolean = chooser match {
      case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] => bandit.getOptionsAvailable.containsKey(value)
      case eei: ExploreExploitImitate[AnyRef, _, Null] => eei.getOptionsAvailable.asScala.contains(value)
      case pso: ParticleSwarm[AnyRef, _, Null] => pso.getOptionsAvailable.asScala.exists(_ == value) // TODO: either get this through reflection or make send PR with getter to Ernesto
    }
    override def message(value: AnyRef): String = Dump.logoObject(value) + " is not an option of this chooser."
  }

}
