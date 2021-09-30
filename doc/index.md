# ALA Name Matching Framework (v2)

* [Overview](#overview)
  * [Bayes Theorem](#bayes-theorem)
  * [A Note](#a-note)
* [Bayesian](bayesian.md) Basic principles of bayesian networks and how they are implemented
  * [Bayesian Network](network.md) How to define a Bayesian network for something.

## Overview

This suite of libraries and tools was developed to build a framework for matching
scientific names for the Atlas of Living Australia.
The basic idea behind name matching is that an occurrence record
(the what, where and when of observing an animal or plant in the wild)
usually comes with the "what" expressed as a scientific name.
This name needs to be matched against the current view of taxonomy and slotted
into the correct position on the taxonomic tree.

But to do this, the system needs to recognise _what_ scientific name is
being supplied.
These supplied names are sometimes ambiguous, subject to error and
can be presented in different ways.
The record may also contain a number of related elements, such as location
or the nomenclatural code being used, that can influence the choice of
taxon.

### Bayes Theorem

The new name matching system used Bayes Theorem to select matching taxa.
The occurrence record is treated as a vector of evidence, containing
statements about the observable properties of the record.
This evidence is matched against a number of hypotheses, assertions
that the evidence matches the required properties of the hypothesis.
Bayes Theorem allows us to combine the likelyhood of the hypothesis
being correct in the absence of any evidence and the evidence to
compute the probability that the hypothesis is correct, given the 
supplied evidence.

![Equation 1](https://latex.codecogs.com/png.download?p%28H%20%7C%20E%29%20%3D%20%5Cfrac%7Bp%28H%29%20%5Ccdot%20p%28E%20%7C%20H%29%7D%7Bp%28E%29%7D)

where _p(H | E)_ is the probability of the hypothesis _H_ being
true given the evidence _E_, also called the posterior probability,
_p(H)_ is the prior probability that _H_ is true without any further
information, _p(E | H)_ is the probability of seeing the evidence,
given the hypothesis and _p(E)_ is the probability of seeing the supplied
evidence.

It is theoretically possible to pre-compute _p(H)_, _p(E | H)_ and _p(E)_
for all the possible combinations of hypothesis and evidence, allowing
us to compute the probability that _H_ is the correct choice for the occurrence
record.
In fact, various tricks are used to reduce the amount of pre-computation
required, since there are a large number of - possibly absent - pieces
of evidence that can contribute to a match.
A key element is the construction of a _Bayseian Network_, a graph of
cause and effect that allows calculations to navigate the chain of implications
present in the data. (For example, a scentific name of _Acacia dealbata_ makes
a kingdom of _Plantae_ an almost certainty.)

# A Note

How everything fits together can be somewhat indirect.
Sorry 'bout that but it's a result of being able to declaratively define how
things work and use the system to do all the heavy lifting.

Code generation is used to do the compilation.
Once the conditional probabilities are worked out, classifcation, inferencer,
parameters and  builder classes are generated to provide
a concrete implementation of the actual network.
You could do this on the fly, using an engine that follows the network.
However, following the engine while debugging would be a bit like:

> Oh yes. I've been through there on my trip around the image.
> The giant vaulted Klein bottles covered with mosaics of other, different, Klein bottles ...
> the rows of gargoyles on the roof, each holding a sign reading "See Previous Gargoyle" ...
> the little food stands around the base, where they sell you food stand vouchers,
> redeemable for food stand vouchers at all food stands except this one ...
> the hall of the penitents ...
> the giant Romanesque stained glass windows, built out of thousands of tiny LooksLike blocks,
> lit from behind by the radiance of the great Aka ...
>
> Truly one of the architectural wonders of our age. I've been there alright. And I bought postcards.
>
> -- Steve Taylor, talking about some completely different software

