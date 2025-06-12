
# Validations Design patterns

## Usinf reactor for a better asyncronous programming model
### TODO : CONFIGURE netty as a web server


### Data Mapper as a pattern

## Combinator pattern with functional interfaces for domain object data validation. LeadDataValidatorService

This pattern is used to validate domain objects in a functional way, allowing for a more declarative style of validation. It uses functional interfaces to define validation rules and combines them to create a comprehensive validation service.


## External services validation pattern Error Accumulator  and Composite Validator with delegates

Notification (or “Error Accumulator”) Pattern
A simple container (ValidationResult) that holds a list of messages and an overall “valid” flag.

Composite Validator
A Validator<T> interface, many small validator implementations (each focused on one rule), and a “composite” that runs them all and merges their results.

* Implement multiple validator solution design, includes:
- Validator : A SAM interface, that allows the polymorphism.
- ValidationResult: A simple accumulator for errors.
- CompositeValidator: A set of validators, and functions that allows to operate all the validators in a simple way.

Why This Works
Single Responsibility: Each Validator<T> only knows about one rule.

Open/Closed: You can add new Validator implementations without touching existing code.

Reusability: You can reuse validators in different combinations.

Clear Error Reporting: You collect all failures in ValidationResult, so callers can see everything at once.

Testability: You can unit-test each rule in isolation.