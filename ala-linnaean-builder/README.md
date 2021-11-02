# ALA Namematching Builder

The builder classes for the [ALA Namematching Model](../ala-linnaean/README.md).

The `AlaWeightAnalyser` builds weights for incoming taxa.
It does this in three ways:

* Looks up a list of taxon ids and weights in the index builder's data directory
  in a file called `taxon-weights.csv`.
  The taxon list has the following entries:
  * `taxonId` The taxon identifier
  * `weight` The weight to give this taxon
* Uses the `priority` value from the taxon merger.
* Modifies weights based on ranks, using an optional `rank-weights.csv` file from the 
  index builder's config directory.