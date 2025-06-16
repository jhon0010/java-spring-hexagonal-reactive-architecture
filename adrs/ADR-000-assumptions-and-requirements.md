# Assumptions

## Business Assumptions 

## 📄 Process Overview

- The validation process begins when a potential lead (`LeadDto`) is received from the CRM system.  
  A sales agent—user of this system—can **manually trigger validations** using either a **CLI** or **REST interface**, depending on the integration.

- For the purpose of external validations, only a **minimal subset of lead information** is required:
    - Document type
    - Document number
    - Email
    - Cellphone number

  We assume this combination of fields is **unique per individual** and is sufficient to perform reliable lookups.  
  Revalidations using the same data are not supported.

- At the end of the process, the lead is enriched with validation results and assigned a **final status**, which can be one of:
    - ✅ **PROSPECT** – All validations were successful, and the lead received a satisfactory score.
    - ❌ **REJECTED** – One or more validations failed, or the lead's score did not meet the minimum threshold.

- The system will always return the **detailed validation results** alongside the lead status. These results are considered valuable for business tracking and auditing.

- Some validations are **independent** and can be executed in **parallel** to improve performance.  
  Once all independent checks complete, **dependent steps (e.g., scoring)** will be triggered asynchronously based on the outcome.


# Business requirements

| ID        | Description                                                                                        |
| --------- |----------------------------------------------------------------------------------------------------|
| **BR-01** | The system must allow manual validation of leads to determine prospect eligibility.                |
| **BR-02** | Leads must be validated using 3 criteria before being promoted to prospects.                       |
| **BR-03** | A user (sales agent) can trigger the validation process on demand via CLI.                         |
| **BR-04** | Validation Criteria:                                                                               |
|           | a. The person must exist in the **National Registry** and match local data. (simulated behaviour)  |
|           | b. The person must have **no judicial records** in the national archives. (simulated behaviour)    |
|           | c. The person must score **above 60** in the **internal qualification system**.(simul.  behaviour) |
| **BR-05** | If validations pass, the lead is promoted to **prospect stage**.                                   |
| **BR-06** | The system must provide feedback if validation fails, including reasons.                           |
| **BR-07** | The system must allow consumption of the lead promotion logic via REST endpoint.                   |


# Technical requirements

| ID        | Description                                                                                           |
| --------- | ----------------------------------------------------------------------------------------------------- |
| **TR-01** | The solution must execute in a **JVM-based language** (Java, Kotlin, Scala).                          |
| **TR-02** | Implement a **CLI (Command Line Interface)** to trigger validations.                                  |
| **TR-03** | The first two validations (registry + judicial) must run **in parallel**.                             |
| **TR-04** | The third validation (scoring) must run **after** the first two complete successfully.                |
| **TR-05** | All external systems must be implemented as **simulated services** (e.g., stubbed or mocked).         |
| **TR-06** | External calls must simulate **latency** to reflect real-world service behavior.                      |
| **TR-07** | Implement **automated tests** for core logic to validate correctness.                                 |
| **TR-08** | A simple CLI input/output interface is enough — **no UI or database** required.                       |
| **TR-09** | The architecture should demonstrate **proper separation of concerns** (domain, ports, adapters, etc). |
| **TR-10** | Provide a **README** explaining assumptions, design decisions, and improvement ideas.                 |

