# Assumptions

## Business Assumptions 

* Every process will start with the consumption of a possible lead (LeadDto) from another system (CRM), then the 
sales agent (User of this system) in this need to run some manual checks to validate the lead for that purpose we enable a
 CLI and REST interfaces.

* The output of the process will be a Lead with a related state and validations that at the end could be:
  * PROSPECT : All the validations were completed in a successful way.
  * REJECTED : At least one of the validations failed.

* The validation results would be relevant to the business, so they will be returned with the Lead information.

* Some of the validations need to be executed in a parallel way, so the process will be executed in a 
  parallel way.
* Other validations has a dependency from the previous ones, so they will be executed in a sequential way.
