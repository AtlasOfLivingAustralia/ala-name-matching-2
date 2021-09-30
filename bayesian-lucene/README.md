# Lucene Store and Classifier Implementation

This module uses [Apache Lucene](https://lucene.apache.org/) to implement a
[classifier](../bayesian-core/src/main/java/au/org/ala/bayesian/Classifier.java)
that can be stored and retrieved by a lucene index and a
[classifier searcher](../bayesian-core/src/main/java/au/org/ala/bayesian/ClassifierSearcher.java)
that can be used to search for candidate classifications.