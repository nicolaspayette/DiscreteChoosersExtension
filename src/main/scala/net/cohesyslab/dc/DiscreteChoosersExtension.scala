package net.cohesyslab.dc

import net.cohesyslab.dc.bandits.EpsilonGreedyBanditPrim
import net.cohesyslab.dc.bandits.GetEpsilonPrim
import net.cohesyslab.dc.bandits.GetTemperaturePrim
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
  GetEpsilonPrim,
  SetEpsilonPrim,

  // SoftMax bandit:
  SoftmaxBanditPrim,
  GetTemperaturePrim,
  SetTemperaturePrim,

  // UCB bandit:
  UCBBanditPrim
)