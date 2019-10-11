package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.Observation
import io.github.carrknight.bandits.AbstractBanditAlgorithm
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.core.ExtensionObject
import org.nlogo.core.LogoList

import scala.collection.JavaConverters._

case class Expectation(option: AnyRef, value: java.lang.Double) {
  def toLogoList = LogoList(option, value)
}

class ChooserObject(val chooser: Chooser[AnyRef, Double, Null]) extends ExtensionObject {

  override def getExtensionName: String = DiscreteChoosersExtension.name
  override def getNLTypeName: String = chooser.getClass.getSimpleName
  override def recursivelyEqual(obj: AnyRef): Boolean = eq(obj)
  override def dump(readable: Boolean, exporting: Boolean, reference: Boolean): String = hashCode.toHexString

  def observe(choiceMade: AnyRef, resultObserved: Double): Unit =
    chooser.updateAndChoose(new Observation(choiceMade, resultObserved, null))

  def choice: AnyRef = chooser.updateAndChoose(null)

  def bestOption(rng: MersenneTwisterFast): AnyRef = bestOptions match {
    case xs if xs.size == 1 => xs.head
    case xs => xs(rng.nextInt(xs.size))
  }

  def bestOptions: Vector[AnyRef] = {
    val expectations = this.expectations
    val maxValue: Double = expectations.map(_.value).max
    expectations.filter(_.value == maxValue).map(_.option)
  }

  def expectations: Vector[Expectation] = chooser match {
    case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
      options.map(option => Expectation(option, bandit.getBanditState.predict(option, null)))
  }

  def options: Vector[AnyRef] = chooser match {
    case bandit: AbstractBanditAlgorithm[AnyRef, _, Null] =>
      bandit.getOptionsAvailable.entrySet.asScala.toVector.sortBy(_.getValue).map(_.getKey)
  }

}
