
# Validations Design patterns

## Usinf reactor for a better asyncronous programming model
### TODO : CONFIGURE netty as a web server


### Data Mapper as a pattern

## Combinator pattern with functional interfaces for domain object data validation. LeadDataValidatorService

This pattern is used to validate domain objects in a functional way, allowing for a more declarative style of validation. It uses functional interfaces to define validation rules and combines them to create a comprehensive validation service.


## Collector Pattern combined with a Result/Validation wrapper

Key Design Patterns Used:
1. Strategy Pattern - ValidationResultFormatter allows different output formats
2. Template Method Pattern - Abstract formatter with concrete implementations
3. Composite Pattern - ValidationGroup for organizing related validations
4. Factory Pattern - Static factory methods in ValidationResult
5. Fluent Interface Pattern - Method chaining for readable validation setup
6. Command Pattern - Each validation rule is encapsulated as a command
   Key Enhancements:
   Severity Levels

Added ValidationSeverity enum (INFO, WARNING, ERROR)
Different validation outcomes can have different importance levels

Validation Rules Interface

ValidationRule<T> functional interface for reusable validation logic
CommonValidations utility class with predefined rules

Validation Groups

Organize related validations together
Execute groups of validations on objects

Multiple Output Formats

Default, Errors Only, Summary, and Detailed formatters
Easy to add new formatting strategies

Enhanced Query Methods

Filter by field, severity, or validation status
Check overall validation state

Type Safety

Generic types ensure type safety across validation rules
Prevents runtime type errors

Best Practices Implemented:

Immutable Results - ValidationResult objects are immutable
Null Safety - Proper null checking in validation rules
Separation of Concerns - Validation, collection, and formatting are separate
Extensibility - Easy to add new rules, formatters, and validation types
Fluent API - Readable and chainable method calls
Factory Methods - Clean object creation with meaningful names

This design is production-ready and follows SOLID principles, making it maintainable and extensible for complex validation scenarios.