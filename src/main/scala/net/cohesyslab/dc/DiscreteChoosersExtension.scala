package net.cohesyslab.dc

import net.cohesyslab.dc.bandits.EpsilonGreedyBanditPrim
import net.cohesyslab.dc.bandits.EpsilonPrim
import net.cohesyslab.dc.bandits.TemperaturePrim
import net.cohesyslab.dc.bandits.SetEpsilonPrim
import net.cohesyslab.dc.bandits.SetTemperaturePrim
import net.cohesyslab.dc.bandits.SoftmaxBanditPrim
import net.cohesyslab.dc.bandits.UCBBanditPrim

object DiscreteChoosersExtension {
  val name = "dc"
}

class DiscreteChoosersExtension extends ExtensionClassManager(
  // Common to all choosers:
  ObservePrim,
  ChoicePrim,
  ExpectationsPrim,
  OptionsPrim,
  BestOptionPrim,

  // Epsilon-greedy bandit:
  EpsilonGreedyBanditPrim,
  EpsilonPrim,
  SetEpsilonPrim,

  // SoftMax bandit:
  SoftmaxBanditPrim,
  TemperaturePrim,
  SetTemperaturePrim,

  // UCB bandit:
  UCBBanditPrim
)