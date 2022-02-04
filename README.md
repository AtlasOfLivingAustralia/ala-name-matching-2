# Redesign of the ala-name-matching library

See [here](doc/index.md) for documentation.

**This is very much a work in progress. You have been warned**

The approach taken is to treat taxonomic information as a vector of evidence and try to
find the taxon that has the highest probability of matching all the supplied evidence.
To do this, a Bayesian network is used to identify how the various pieces of taxonomic
information interact and build a conditional probability graph based on the network.
Since handling every possible combination of pieces of evidence would result in a 
combinatorial explosion, the network is "compiled" into a graph of cause and effect,
narrowing conditional probabilities to the smallest possible set of antecendents.

## Test indexes

To use the test cases in ala-linnaean module, you will need the corresponding
Linnaean and vernacular name indexes. You can get:

* [index-20210811-2.zip](https://archives.ala.org.au/archives/nameindexes/20210811-2/index-20210811-2.zip)
* [vernacular-20210811-2.zip](https://archives.ala.org.au/archives/nameindexes/20210811-2/vernacular-20210811-2.zip)

These need to be unzipped into `/data/lucene`

Every time the network definition changes, the indexes change, since the underlying
inference model will have also changed.
If you get exceptions or errors, you may need a new copy of the index.

## Name Matching Libraries

### Generic Libraries

These libraries are subject-matter agnostic and can be used to build
matching systems for any domain that can be structured into a
graph of cause and effect.

* [Bayesian Core](bayesian-core/README.md)
  Core classes for describing observable properties and how they can
  be derived, stored and matched.
* [Bayesian Lucene](bayesian-lucene/README.md)
  A storage implementation using the [lucene](https://lucene.apache.org/)
  index and search system.
* [Bayseian Builder](bayesian-builder/README.md)
  Builder software that will take a Bayesian network and "compile" it into a
  set of java classes that implement the deductive framework specified by
  the network.
  Also, a generic index builder that takes source data and builds a
  store that can be used to search for matches.
* [Bayesian Maven Plugin](bayesian-maven-plugin/README.md)
  A maven plugin that allows you to embed network building and
  compilation into your maven build cycle.

### Taxonomy-Specific Libraries

These libraries are oriented towards handling  generic biological nomenclature,
wihout insisting on a specific model.
These libraries draw extensively on [Darwin Core](https://dwc.tdwg.org/terms/)
and the [Global Biodiversity Information Framework](https://www.gbif.org/)
suite of tools and software.

* [Taxonomic Tools](taxonomic-tools/README.md)
  Utility vocabularies and processing designed to handle biological taxonomy.
* [Taxonomic Tools Builder](taxonomic-tools-builder/README.md)
  Builder processing designed to complement the taxonomic tools.

### ALA-Specific Libraries

Libraries that contain the ALA-specific implementation of taxonomy matching.
There are two netorks:

* The [Linnaean](ala-linnaean/src/main/resources/ala-linnaean.json)
  network models scientific names based on the Linnaean hierarchy.
* The [Vernacular](ala-linnaean/src/main/resources/ala-vernacular.json)
  network models vernacular (common) names.


* [ALA Linnaean](ala-linnaean/README.md)
  The classes needed to implement and analyse the Linnaean and vernacular networks
  and match a search against candidates.
  A library that allows a client to build a template of known information
  about a name, search an index built by the ALA builder library and
  return a "most likely" match to a specific taxon.
  In particular, it includes more sophisticated post-processing of results
  to take care of oddities such as parent-child synonyms, misapplied names, etc.
  This is the library that an application would use to implement
  name searching.
* [ALA Linnaean Builder](ala-linnaean-builder/README.md)
  The classes needed to build name indexes for both networks.
