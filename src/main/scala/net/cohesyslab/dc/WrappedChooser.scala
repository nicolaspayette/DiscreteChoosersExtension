package net.cohesyslab.dc

import io.github.carrknight
import io.github.carrknight.Observation
import net.cohesyslab.dc.utils.AlwaysValid
import net.cohesyslab.dc.utils.ValidationRule
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.core.ExtensionObject
import org.nlogo.core.LogoList

case class OptionValue(option: AnyRef, value: java.lang.Double) {
  def toLogoList = LogoList(option, value)
}

trait Chooser extends ExtensionObject {
  override def getExtensionName: String = DiscreteChoosersExtension.name
  override def recursivelyEqual(obj: AnyRef): Boolean = eq(obj)
  override def dump(readable: Boolean, exporting: Boolean, reference: Boolean): String = hashCode.toHexString

  def observe(observedOption: AnyRef, observedResult: Double): Unit
  def choice(context: Context): AnyRef
  def bestOption(rng: MersenneTwisterFast): AnyRef =
    bestOptions match {
      case xs if xs.size == 1 => xs.head
      case xs => xs(rng.nextInt(xs.size))
    }
  def bestOptions: Vector[AnyRef] = {
    val expectations = this.optionValues
    val maxValue: Double = expectations.map(_.value).max
    expectations.filter(_.value == maxValue).map(_.option)
  }
  def optionValues: Vector[OptionValue] = options.map(optionValue)
  def options: Vector[AnyRef]
  def isValidOption(option: AnyRef): Boolean
  def optionValue(option: AnyRef): OptionValue
  def lastObservation: Option[Observation[AnyRef, Double, Null]]
}

abstract class WrappedChooser[C <: carrknight.Chooser[AnyRef, Double, Null]](val delegate: C) extends Chooser {

  val observedResultValidationRule: ValidationRule[Double] = AlwaysValid()
  private[this] var _lastObservation: Option[Observation[AnyRef, Double, Null]] = None
  override def getNLTypeName: String = delegate.getClass.getSimpleName
  def lastObservation: Option[Observation[AnyRef, Double, Null]] = _lastObservation

  def choice(context: Context): AnyRef = delegate.updateAndChoose(null)

  def observe(observedOption: AnyRef, observedResult: Double): Unit = {
    _lastObservation = Some(new Observation(
      AvailableOption(observedOption).get,
      observedResultValidationRule(observedResult).get,
      null
    ))
    _lastObservation.foreach(delegate.updateAndChoose(_))
  }

  object AvailableOption extends ValidationRule[AnyRef] {
    override def isValid(value: AnyRef): Boolean = isValidOption(value)
    override def message(value: AnyRef): String = Dump.logoObject(value) + " is not an option of this chooser."
  }

}