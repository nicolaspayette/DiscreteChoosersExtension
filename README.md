# The NetLogo Discrete Choosers Extension (`dc`)

This is a [NetLogo](http://ccl.northwestern.edu/netlogo/) extension aiming to make the [library of discrete choice algorithms](https://github.com/CarrKnight/discrete-choosers) developed by Ernesto Carrella easily usable from within NetLogo.

See http://carrknight.github.io/poseidon/algorithms.html for a discussion of the different algorithms available.

This extension is still in the early stages of development. Not all algorithms are available, and those that are might still be buggy. Feedback and bug reports are welcome. (Just [open an issue](https://github.com/nicolaspayette/DiscreteChoosersExtension/issues).)

You can download the extension from the [releases page](https://github.com/nicolaspayette/DiscreteChoosersExtension/releases). Just unzip `dc.zip` under your NetLogo `extensions/` folder. Once it's a bit more stable, I'll make it available through the NetLogo Extensions Manager.

The extension requires NetLogo >= 6.0.0.

Rudimentary demo models are available in the [`demo/`](https://github.com/nicolaspayette/DiscreteChoosersExtension/tree/master/demo) folder.

# Available primitives

(Documentation coming soon.)

## Chooser creation

### Bandits

* `dc:epsilon-greedy-chooser`
* `dc:softmax-chooser`
* `dc:ucb1-chooser`

### Imitators

* `dc:explore-exploit-imitate-chooser`
* `dc:particle-swarm-chooser`

## Chooser operation

* `dc:choice`
* `dc:observe`

## Chooser information

* `dc:last-observation`
* `dc:options`
* `dc:value`
* `dc:option-values`
* `dc:best-option`
* `dc:best-options`

## Chooser properties

* `dc:exploration-probability`
* `dc:set-exploration-probability`
* `dc:temperature`
* `dc:set-temperature`
* `dc:sigma`
* `dc:set-sigma`
* `dc:imitation-probability`
* `dc:set-imitation-probability`

