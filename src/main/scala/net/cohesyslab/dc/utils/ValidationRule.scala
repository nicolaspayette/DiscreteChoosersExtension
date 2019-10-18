package net.cohesyslab.dc.utils

import org.nlogo.api.ExtensionException

import scala.math.Ordering.Implicits._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait ValidationRule[T] {
  def isValid(value: T): Boolean
  def validated(value: T): Try[T] =
    if (isValid(value)) Success(value)
    else Failure(new ExtensionException(message(value)))
  def message(value: T): String = s"Validation failed for $value."
}

case class AlwaysValid[T]() extends ValidationRule[T] {
  override def isValid(value: T): Boolean = true
}

case class InRange[T: Ordering](inclusiveLowerBound: T, inclusiveUpperBound: T) extends ValidationRule[T] {
  override def message(value: T): String =
    s"Expected value to be between $inclusiveLowerBound and $inclusiveUpperBound (inclusively) but got $value instead."
  override def isValid(value: T): Boolean = value >= inclusiveLowerBound && value <= inclusiveUpperBound
}