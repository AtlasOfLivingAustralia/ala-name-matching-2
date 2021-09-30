# ALA Namematching Implementation

This module uses the model from [ALA Namematching](../ala-linnaean/README.md) and
the [lucence classifiers](../bayesian-lucene/README.md) to provide a name matching
service that can be used by a client application.

Also present here is a classification matcher that tests for a number of 
conditions, such as parent-child synonyms, multiple synonyms and the like and produces
a preferred match.