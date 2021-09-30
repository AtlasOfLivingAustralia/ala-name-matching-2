# Defining a Bayesian Network

* [Preliminaries](#preliminaries)
* [Network Definition](#network-definition)
  * [Identification](#identification)
  * [Network](#network)
  * [Normalisers](#normalisers)
  * [Observables](#observables)
    * [Properties](#properties)
    * [Derivations](#derivations)
    * [Groups](#groups)
  * [Vertices](#vertices)
  * [Edges](#edges)
  * [Issues](#issues)
  * [Modifications](#modifications)
  * [Modifiers](#modifiers)
* [Compiling the Network](#compiling-the-network)

Defining a Bayesian network in enough detail that it can be compiled into code
usually requires more detail than simply sketching out the network.
In particular, the type of data that is being stored, what counts as "equivalent"
and how to condition incoming data so that it is consistent usually needs to be
specified, even to the point of saying how to derive the value from other data.

## Preliminaries

If you haven't already, read up on the [vocabulary](bayesian.md#vocabulary) used to
describe various elements.

Before definting the network, you will need to collect the observables that you can use 
and sketch out a [well-conditioned network](bayesian.md#well-conditioned-networks)
that describes the relationships between them.
Not all observables need to be part of the network; you may need to either feed them
back to whatever is doing the search or use them to derive other properties.
From these observables, you will need to identify:

* An _identifier_ the observable that contains a unique identifier for the classifier.
  In a sense, the identifier is used as a stand-in for all the information that the
  classifier contains.
* A _name_ that provides a usable name for the classifier.
* A _full name_ that provides a more formal name for the classifier.
* A _parent identifer_ if the network describes some sort of hierarchical strcuture.
  In the [Linnaean network](bayesian.md#simple-linnaean-classification-example) example,
  a species is contained within a genus, which is contained withing a family, which
  is contained within an order, etc. Possibly with intermediate levels.
  The parent identifier points to the next higher level in the concept tree.
* An _accepted identifier_ if the network contains synonyms.
  Synonyms can contain their own evidence and classifier but they point to the
  accepted version that is in use.
* An _alternative name_ for a classifier.
  This is usually a synonym identified by the accepted identifier but can
  also include alternative spellings
* A _weight_ that contains a weighting value to descirbe how important the concept is.
  Weights are used to calculate prior and conditional probabilities. 
  By default, the weight is 1 and the prior probability is _1/n_ where _n_ is the
  number of classifiers; all possibilities are equally likely.
  Using a weight allows you to declare some animals more equal than others and the
  prior probability is the weight divided by the sum of all weights.

Once you have identified these features, you can start work on an
_analyser_ for the network.
Analysers<sup>1</sup> implement the `au.org.ala.bayesian.Analyser` interface
and allow you to condition incoming information so that it matches properly.

To begin with, you'll usually have to do this in two parts. 
First write a placeholder analyser that does nothing very useful and then,
once the network has been compiled, fill it out using the classes that have
been generated.

<sup>1</sup> Note that we use the Queen's good English in these here parts.

## Network Definition

The basis of a bayesian network is a JSON file that contains a network description.
See the [ALA Linnaean](../ala-linnaean/src/main/resources/ala-linnaean.json) network
for an example.
The file contains the following elements:

### Identification

Most elements, including the network itself, can have the following information:

* **id** A unqiue identifier within the file.
  These identifiers are used to reference other elements in the network
  and to generate variables and constants during code generation.
* **description** A text description of the element.
* **label** A label to use when referencing the element in diagrams, etc.
* **uri** A URI that describes the element.

### Network

As well as identification, the network also contains:

* **concept** A URI describing the concept that the network implements.
  For example, for a taxonomic network, the concept might be
  `http://rs.tdwg.org/dwc/terms/Taxon`
* **vocabularies** A list of classes containing GBIF Term vocabularies that
  should be loaded before analysing the network, as fully qualified class names.
  These vocabularies can be used to provide consistent prefixes to the terms
  that need to be stored in indexes and the like.

### Normalisers

Normalisers can be used to pre-process data for observables, cleaning up
things like strange encodings, inconsistent cases and the like.
A **@class** property identifies the class that the implements the normaliser.
Other parameters are dependent on the normaliser class.
Each normaliser has an `id` thagt can be used to reference the normaliser.

### Observables

The list of observables is the list of attributes that a classifier/classification can have.
Not all of these attributes will be in the actual network.
However, anthing that needs to be included in a classifier and classification needs to
be specified.

An exmaple observable specification is:

```json
{
  "id": "phylum",
  "label": "P",
  "uri": "http://rs.tdwg.org/dwc/terms/phylum",
  "group": "phylum",
  "normaliser": "simple_normaliser",
  "style": "PHRASE",
  "multiplicity": "*",
  "base": {
    "@class": "au.org.ala.bayesian.derivation.ParentDerivation",
    "sources": [ "scientificName" ],
    "condition": {
      "positive": true,
      "observable": "taxonRank",
      "value": "phylum"
    }
  }
}
```

Observables have the id, description, label and uri elements described in
[identification](#identification).
Of these, the id is essential, since observables need to be referenced elsewhere.
A URI is highly encouraged, since it allows the observable to be uniquely described
and allows genus, for example, to be correctly mapped on to
`http://rs.tdwg.org/dwc/terms/genus` rather than be imprecise.

In addition, observables have the following properties:

* **type** The (java) type the observable ghas, defaulting to `java.lang.String`
* **group** A related group of observables that can be assumed to all be present or all be absent.
  Groups are used to reduce the network of cause and effect for classifiers with missing information.
  See [groups](#groups) for more information.
* **normaliser** An optional reference to a [normaliser](#normalisers) to use on the data.
* **style** One of:
  * `IDENTIFIER` - an opaque, case sensitive identifier (eg. taxonId)
  * `CANONICAL` - the default, an string or similar that is treated as a unit but which has a canonical form (eg. all lower case) 
    that can be used to compare non-identical versions (eg. taxonRank)
  * `PHRASE` - a string consisting of units than can be subject to tokenised search (eg. scientificName)
* **multiplicity** One of:
  * `0..1` - the default, an optional value with zero or one value (eg. taxonomicStatus)
  * `1` - a required value with only one value (eg. taxonId)
  * `*` something with zero or more possible values (eg. family)
  * `+` something with one or more possible values (eg. scientificName)
* **analysis** A reference to an object that interprets the observable so that
  it can be parsed, stored, queried and tested for equivalence.
  By default, this is derived from the type of the observable.
  However, special cases can be specified using this attribute.
  A **@class** property gives the class of analysis to support, which must be a subclass
  of `au.org.ala.bayesian.Analysis` with additional properties, if needed to
  configure the analysis object.
* **derivation** If this observable is derived, not given, then how this derivation takes place.
  A **@class** property gives the class of derivation that needs to be implemented.
  Generally, there is a **sources** list that gives the observables that are used
  to construct the derivation and any other parameters that are needed. 
  See [derivations](#derivations), below for more information.
* **base** A source of copied data for hierarchies; a form of derivation.
  If a parent classifier can be specified, then the list of parents can be searched
  for information that would flesh-out the classiifer.
  As an example, if you have a species classifier, the parents can be searched
  for a family classifier.
  If found, the scientificName of the family classifier can be copied into the
  family observable of the species classifier.
  Generally, a base consists of an object the implements the derivation, with a
  **@class** property that contains the type of derivation, a **sources** list
  that gives the observable to get the information from and a
  **condition** that specifies which parent to derived the information from.
* **properties** Additional properties that provide information about nature of the observable.
  See [properties](#properties) for more information.

#### Properties

The properties section of an observable provides descriptive properties that
the network compiler can use to identify the elements described in [preliminaries](#preliminaries).
An example observable with properties that specify that it is the identifier is:

```json
  {
    "id": "taxonID",
    "label": "tID",
    "uri": "http://rs.tdwg.org/dwc/terms/taxonID",
    "properties": {
      "http://id.ala.org.au/bayesian/1.0/identifier": true
    },
    "style": "IDENTIFIER",
    "multiplicity": "1"
  }
```

A property is a URI and a value.
The following properties are recognised by the network compiler.
These are all boolean values at present but the model allows for further properties of
any type.

* `http://id.ala.org.au/bayesian/1.0/weight` This observable provides the classifier weight.
* `http://id.ala.org.au/bayesian/1.0/identifier` This observable is the classifer identifier
* `http://id.ala.org.au/bayesian/1.0/name` This observable provides the name of the classifier.
* `http://id.ala.org.au/bayesian/1.0/fullName` This observable provides the full, formal name of the classifier.
* `http://id.ala.org.au/bayesian/1.0/altName` This observable holds alternative names for the classifier.
* `http://id.ala.org.au/bayesian/1.0/additionalName` This observable holds additional, disambiguating information about a name. 
  For example, the scientificNameAuthorship field holds additional information 
  about how the scientificName and taxonId should be interpreted.
* `http://id.ala.org.au/bayesian/1.0/parent` This observable provides the reference identifier to the parent classifier.
* `http://id.ala.org.au/bayesian/1.0/accepted` This observable provides the linking reference to the
  accepted classifier in the case of a synonym.
* `http://id.ala.org.au/bayesian/1.0/copy` Informtion that should be copied to a synonym from the accepted classifier.
  This property can be used to fill out clarifying infomation for something that has it missing.
  For example, the kingdom is copied from an accepted taxon to a synonym to provide a stable form of disambiguation
  if there is not enough contextual information.
* `http://id.ala.org.au/bayesian/1.0/additional` The is an observable that does not particpate in the 
  Bayesian network but which needs to be included in the final classification.
  As an example, taxonomicStatus is not used during matching but needs to be provided with the match.
* `http://id.ala.org.au/bayesian/1.0/link` This is a link property for information derived from parent-child data.
  If there are different opinions about what the value of this property should be, the parent takes precidence.
  Otherwise, weird things can happen.

#### Derivations

Derivations are used for two main purposes.
The first is to provide derived versions of observables from other observables.
For example soundexFamily is derived from the family via the following derivation:

```json
{
  "id": "soundexFamily",
  "label": "xF",
  "uri": "http://id.ala.org.au/terms/1.0/soundexFamily",
  "group": "family",
  "multiplicity": "*",
  "derivation": {
    "@class": "au.org.ala.names.TaxonNameSoundexDerivation",
    "sources": [ "family" ],
    "rank": "FAMILY"
  }
}
```

In this case, the derivation generates a soundex (using the taxamatch algorithm)
from the name in family, based on a rank of FAMILY, since the taxamatch algorithm
that we use varies slightly depending on the rank of the name.

Derivations must inherit from `au.org.ala.bayesian.Derivation` but can be specified
to have multiple parameters, including other observables.
Derivations generate code, which is embedded into the classification and builder
classes that then perform the derivations directly.
If a derived value is provided, then no derivation is run.

Derivations can be _generators_ where a value is automatically generated for a classifier if it is not present.
Generators are run before any analysis and other derivation is done, 

The other main use of derivation is to copy values from a parent in a hierarchical
classification scheme.
An example of a copy derivation is

```json
{
  "id": "family",
  "label": "F",
  "uri": "http://rs.tdwg.org/dwc/terms/family",
  "group": "family",
  "normaliser": "simple_normaliser",
  "style": "PHRASE",
  "multiplicity": "*",
  "base": {
    "@class": "au.org.ala.bayesian.derivation.ParentDerivation",
    "sources": [ "scientificName" ],
    "condition": {
      "positive": true,
      "observable": "taxonRank",
      "value": "family"
    }
  }
}
```

In this case, the base derivation copies a value from a parent classifier.
The particular classifier is chosen by a condition that specifies that
an particular observable in the parent (the taxonRank) has a specific value (family).

The analyser can also be used to derive values in a more ad-hoc fashion.
When building a classifier, the sequence is:

1. Generator derivations are executed during loading
2. Any parent information is gathered
3. The analyser is run over a classification generated from the classifier.
4. Other derivations are executed

#### Groups

It is possible for classifiers to be completely missing information on which inference depends.
For example, in the following fragment of the ALA Linnaean network, all of the genus information is missing.

![Missing Genus](group1.png)

The approach taken by the network compiler is to generate sub-networks with the missing observables
removed and dependencies shifted down to the nearest immediate antecedents in the graph.
In the above example, the resulting sub-graph will be

![Reduced Genus](group2.png)

_Groups_ specify related observables that can be removed from the graph as a single operation.
Classifiers contain a _signature_ which shows which groups are present and which are absent.
During inference, the signature determines which sub-graph will be used when computing probabilities
and parameters.
Each subgraph generates a separate class for inference and each combination of group
presence or absence generates a subgraph.
So use groups sparingly.

### Vertices

The vertices are simply a list of the observables (via id) that take
part in the Bayesian network.

### Edges

The edges are a list of cause-effect relationships within the network.
An example edge is

```json
{ "source": "genus", "target": "genusID",  "edge": { } }
```

Where the **source** specifies the cause observable, the **target** the
effect observable and the **edge** contains additional information (currently always empty).
The above example is a statement that the genus name affects the genus identifier.

### Issues

Issues are statements about various problems encountered when matching information,
ranging from the minor (eg, `http://id.ala.org.au/issues/1.0/canonicalName` which means
that the name had to be parsed into components) to the slightly more catastrophic
(eg. `http://id.ala.org.au/issues/1.0/unresolvedHomonym` which indicates that a
name might apply to multiple, widely distibuted taxa).

Issues contain the elements in [identification](#identification).
They must contain a URI, which is used to precisely describe the nature of the issue.

### Modifications

Modifications are actions that can be used to modify a template classification,
so that problems like misspellings, inaccurate data and the like can be handled
in a controlled manner.

A typical modification is

```json
{
  "@class" : "au.org.ala.bayesian.modifier.RemoveModifier",
  "id" : "misspelled_scientific_name_base",
  "issue" : "misspelled_scientific_name",
  "observables" : [ "scientificName", "genus", "specificEpithet" ]
}
```

As might be expected, there is a class that implements the modifier,
provided by the **@class** property, possibilities from the [identification](#identification)
section, an associated issue (since modifying the template implies that something is wrong)
and implementation-specific configuration.
In the example, the modifier sets to null the scientificName, genus and specificEpithet
values, if they exist, allowing inference to occur based on the soundex branches.

### Modifiers 

Modifier lists give the order and mixture in which modifications can be applied.
In general, the modifier lists are a list of lists.
If a modifier can be applied to a template classification, then the modification
process will try each combination of one element from each list (with an implicit
element of no modification in each list).

There are two lists of modifiers.
The **sourceModifiers** list gives modifications to be applied before searching
for candidates and is used for fairly serious surgery to the search that will
require building a new collection of candidates, such as searching for a higher-level 
match than the supplied name.
The **matchModifiers** list gives modifications that can be applied without
re-querying the store for candidates and covers things like misspelled names, 
inaccurate ranks and the like.

An example modifier list is shown below:

```json
"matchModifiers": [
  [
    "remove_order", "remove_class", "remove_phylum"
  ],
  [
    "remove_order", "remove_class", "remove_phylum"
  ],
  [
    "remove_authorship"
  ],
  [
    "misspelled_scientific_name_base", "misspelled_scientific_name_full"
  ],
  [
    "remove_rank"
  ]
]
```

If applied to the following template

```yaml
scientificName: Acacia dealbata
soundexScientificName: AKACA DELBATA
scientificNameAuthorship: Link
genus: Acacia
soundexGenus: AKACA
taxonRank: species
order: Fabales
```

Then the sequence of application is:

1. none
2. remove_rank
3. mispelled_scientific_name_base
4. misspelled_scientific_name_base, remove_rank
5. misspelled_scientific_name_full
6. misspelled_scientific_name_full, remove_rank
7. remove_authorship
8. ...
9. remove_order, remove_authorship, misspelled_scientific_name_base, remove_rank
10. ...

At the end of step 9, the template would be:

```yaml
soundexScientificName: AKACA DELBATA
soundexGenus: AKACA
```

## Compiling the Network

The easiest way to compile the network is to use the maven plugin.
See [here](../bayesian-maven-plugin/README.md) for more information.

It is good practice to compile the network into at least three modules:

* The _model_ is what client applications can use.
  It contains enough code that a list of candidates can be analysed and
  probabilities computed. (ala-linnaean)
* The _builder_ contains the code needed to build an index with conditional
  probabilities, based on loaded data from some source of data. (ala-linnaean-builder)
  Keeping this as a separate module allows client applications to not have to load
  a vast collection of libraries largely concerned with the compiling and building
  of classifier data.
* The _matcher_ links the model to a persistent data store and provides
  specialist analysis and matching of the matched candidates. (ala-namematching)