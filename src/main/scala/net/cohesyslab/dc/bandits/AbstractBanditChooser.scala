package net.cohesyslab.dc.bandits

import com.google.common.collect.BiMap
import io.github.carrknight.bandits.AbstractBanditAlgorithm
import net.cohesyslab.dc.OptionValue
import net.cohesyslab.dc.WrappedChooser

import scala.collection.JavaConverters._

abstract class AbstractBanditChooser[C <: AbstractBanditAlgorithm[AnyRef, Double, Null]](
  chooser: C
) extends WrappedChooser(chooser) {

  private def getOptionsAvailable: BiMap[AnyRef, Integer] = {
    // TODO: send PR with getter to Ernesto instead of using reflection
    val optionsAvailableField = chooser.getClass.getSuperclass.getDeclaredField("optionsAvailable")
    optionsAvailableField.setAccessible(true)
    optionsAvailableField.get(chooser).asInstanceOf[BiMap[AnyRef, Integer]]
  }

  override def optionValue(option: AnyRef): OptionValue = {
    val validOption = AvailableOption(option).get
    OptionValue(validOption, chooser.getBanditState.predict(validOption, null))
  }

  override def options: Vector[AnyRef] =
    getOptionsAvailable.entrySet.asScala.toVector.sortBy(_.getValue).map(_.getKey)

  override def isValidOption(option: AnyRef): Boolean =
    getOptionsAvailable.containsKey(option)
}
