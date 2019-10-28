package net.cohesyslab.dc.bandits

import io.github.carrknight.bandits.AbstractBanditAlgorithm
import net.cohesyslab.dc.OptionValue
import net.cohesyslab.dc.WrappedChooser

import scala.collection.JavaConverters._

abstract class AbstractBanditChooser[C <: AbstractBanditAlgorithm[AnyRef, Double, Null]](
  chooser: C
) extends WrappedChooser(chooser) {

  override def optionValue(option: AnyRef): OptionValue = {
    val validOption = AvailableOption(option).get
    OptionValue(validOption, chooser.getBanditState.predict(validOption, null))
  }

  override def options: Vector[AnyRef] =
    chooser.getOptionsAvailable.entrySet.asScala.toVector.sortBy(_.getValue).map(_.getKey)

  override def isValidOption(option: AnyRef): Boolean =
    chooser.getOptionsAvailable.containsKey(option)
}
