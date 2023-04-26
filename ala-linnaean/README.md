# ALA Namematching Networks

This module defines two Bayesian networks:

* A network based on the Linnaean classification system. 
  This network is based around the kingdom - phylum - class - order - family - genus - species
  hierarchy used in biological scientific nomenclature.
  It uses higher-order information in the supplied template to confirm and disambuguate evidence.
  It includes a number of other elements that prove useful when dealing with taxa.
  Also present here is a classification matcher that tests for a number of
  conditions, such as parent-child synonyms, multiple synonyms and the like and produces
  a preferred match.
* A network for vernacular names.
  This network uses ancially information, such as sex, location or lifeStage to help disambiguate
  usaes of a name.

Each network has an analyser that (particularly in the case of the Linnaean network)
attempts to beat supplied information into a common form.

Note that to correctly build, this module needs to have the pre-built name indexes installed.
See the [parent README](../README.md) for more information.
If you do not have them and need to build them, skip tests so that you can run the
[ALA index builder](../ala-linnaean-builder/README.md).