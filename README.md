# Redesign of the ala-name-matching library

**This is very much a work in progress. You have been warned**

The approach taken is to treat taxonomic information as a vector of evidence and try to
find the taxon that has the highest probability of matching all the supplied evidence.
To do this, a Bayesian network is used to identify how the various pieces of taxonomic
information interact and build a conditional probability graph based on the network.
Since handling every possible combination of pieces of evidence would result in a 
combinatorial explosion, the network is "compiled" into a graph of cause and effect,
narrowing conditional probabilities to the smallest possible set of antecendents.

Code generation is used to do the compilation.
Once the conditional probabilities are worked out, inferencer, parameters and
builder classes (TODO and a classification model class) are generated to provide
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

The code has been split into multiple modules, with more to come.
As much as possible, the aim is to make it so that importing the name matching library
into an application is a small thing, without a huge number of dangling dependencies.
Building the index and other processing is split out into separate modules so that
you don't have to drink the entire bottle in one go.

## GBIF Name Parser Library

This code uses a modified of the GBIF name parser library that 
parses phrase names.
You will need to install this library before compilation.

Clone https://github.com/charvolant/name-parser and checkout the `phrase-names` branch.
Install this branch with `mvn clean install`