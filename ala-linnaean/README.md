# ALA Namematching Networks

This module defines two Bayesian networks:

* A network based on the Linnaean classification system. 
  This network is based around the kingdom - phylum - class - order - family - genus - species
  hierarchy used in biological scientific nomenclature.
  It uses higher-order information in the supplied template to confirm and disambuguate evidence.
  It includes a number of other elements that prove useful when dealing with taxa.
* A network for vernacular names.
  This network uses ancially information, such as sex, location or lifeStage to help disambiguate
  usaes of a name.

Each network has an analyser that (particularly in the case of the Linnaean network)
attempts to beat supplied information into a common form.