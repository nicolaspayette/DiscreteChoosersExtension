package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.Observation
import io.github.carrknight.bandits.AbstractBanditAlgorithm
import io.github.carrknight.imitators.ExploreExploitImitate
import net.cohesyslab.dc.utils.AlwaysValid
import net.cohesyslab.dc.utils.ValidationRule
import org.nlogo.api.Context
import org.nlogo.api.ExtensionException
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.core.ExtensionObject
import org.nlogo.core.LogoList

import scala.collection.JavaConverters._

case class Expectation(option: AnyRef, value: java.lang.Double) {
  def toLogoList = LogoList(option, value)
}

class ChooserObject(val chooser: Chooser[AnyRef, Double, Null]) extends ExtensionObject {

  // TODO: the methods with pattern matching on chooser type could probably be handled by type classes

  val observedResultValidationRule: ValidationRule[Double] = AlwaysValid()
  private[this] var _lastObservation: Option[Observation[AnyRef, Double, Null]] = None
  override def getExtensionName: String = DiscreteChoosersExtension.name
  override def getNLTypeName: String = chooser.getClass.getSimpleName
  override def recursivelyEqual(obj: AnyRef): Boolean = eq(obj)
  override def dump(readable: Boolean, exporting: Boolean, reference: Boolean): String = hashCode.toHexString
  def observe(choiceMade: AnyRef, observedResult: Double): Unit = {
    val result = observedResultValidationRule.validated(observedResult).get
    _lastObservation = Some(new Observation(choiceMade, result, null))
    _lastObservation.foreach(chooser.updateAndChoose(_))
  }

  def choice(context: Context): AnyRef = chooser.updateAndChoose(null)

  def bestOption(rng: MersenneTwisterFast): AnyRef = bestOptions match {
    case xs if xs.size == 1 => xs.head
    case xs => xs(rng.nextInt(xs.size))
  }

  def bestOptions: Vector[AnyRef] = chooser match {
    case eei: ExploreExploitImitate[AnyRef, _, Null] =>
      Vector(eei.getState.getFavoriteOption)
    case _ => {
      val expectations = this.expectations
      val maxValue: Double = expectations.map(_.value).max
      expectations.filter(_.value == maxValue).map(_.option)
    }
  }

  def expectations: Vector[Expectation] = chooser match {
    case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
      options.map(option => Expectation(option, bandit.getBanditState.predict(option, null)))
    case _: ExploreExploitImitate[_, _, _] =>
      throw new ExtensionException("Explore-Exploit-Imitate choosers do not maintain expectations.")
  }

  def options: Vector[AnyRef] = chooser match {
    case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
      bandit.getOptionsAvailable.entrySet.asScala.toVector.sortBy(_.getValue).map(_.getKey)
    case eei: ExploreExploitImitate[AnyRef, _, Null] =>
      eei.getOptionsAvailable.asScala.toVector
  }

  def lastObservation: Option[Observation[AnyRef, Double, Null]] = _lastObservation

}
