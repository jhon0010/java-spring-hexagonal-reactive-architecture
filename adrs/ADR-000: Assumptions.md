# Assumptions

## Business Assumptions 

* Every process will start with the consumption of a possible lead (LeadDto) from another system, in this 
example CLI and REST interfaces.

* The output of the process will be a Lead with a related state that at the end could be:
  * PROSPECT : All the validations were completed in a successfull way.
  * REJECTED : At least one of the validations failed.

* The validation results would be relevant to the business, so they will be returned with the Lead information.