## ADR‑01 – Reactive Programming Baseline
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Lead ingestion, validation and persistence must be **non‑blocking** to hit >5 k req/s and integrate with R2DBC. |
| **Decision** | Adopt **Project Reactor** (`Mono` / `Flux`) as the default execution model for controllers, services and repositories. |
| **Consequences** | Back‑pressure handled by Reactor. Blocking drivers are banned or confined to `Schedulers.boundedElastic()`. |

---

## ADR‑02 – Functional Combinator Validation
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Need declarative, easily testable field‑level checks for Lead DTOs. |
| **Decision** | Implement **Combinator pattern** using a `Validator<T>` SAM; compose via `.and()`. |
| **Consequences** | Validators are pure, unit‑testable functions; new rules added with zero central modification. |

---

## ADR‑03 – Error‑Accumulator + Composite Validator
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Business wants *all* validation errors in one response. |
| **Decision** | Combine **Notification (Error Accumulator)** with **Composite**. `ValidationResult` collects messages; `CompositeValidator` merges child results in parallel. |
| **Consequences** | Clear bulk feedback; rules obey SRP; open/closed for new checks. |

---

## ADR‑04 – Idempotent Lead Promotion
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Same lead may be resubmitted; duplicate side‑effects must be avoided. |
| **Decision** | Guard in DB: `UPDATE … SET state='PROSPECT' WHERE id=:id AND state<>'PROSPECT' RETURNING *`. Service returns existing row when no update occurred. |
| **Consequences** | Safe against retries & concurrency; no extra tokens or caches needed. |

---

## ADR‑05 – Data Mapper Layer
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Contract DTOs differ from persistence schema. |
| **Decision** | Use **MapStruct** to map DTO ↔ Entity. |
| **Consequences** | Compile‑time mapping, no reflection, clean separation of layers. |

---

## ADR‑06 – Strategy for Dependent / Independent Validators
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Some rules depend on prior outcomes (e.g. password strength needs leak check). |
| **Decision** | Define `IndependentValidator` & `DependentValidator`; schedule in phased executor. |
| **Consequences** | Maximum parallelism while preserving explicit dependencies. |

---

## ADR‑07 – Adapter Ports for External Checks
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Judicial, scoring and registry systems evolve independently. |
| **Decision** | Hexagonal **ports** + **adapters** (`ScoringAdapter`, etc.) translating domain ➔ external API. |
| **Consequences** | Loose coupling; adapters swap via Spring profiles or stubs. |

---

## ADR‑08 – Builder for Validation Result
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Validation result has optional sections and must be immutable. |
| **Decision** | Lombok `@Builder` on `LeadValidationResult`. |
| **Consequences** | Fluent creation; no telescoping constructors. |

---

## ADR‑09 – Centralised Exception Handling
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Need uniform JSON envelope and logging. |
| **Decision** | `@RestControllerAdvice` converts all exceptions to `ApiResponse{success:false}` + business error code. |
| **Consequences** | Zero boilerplate in controllers; single edit point for error policy. |

---

## ADR‑10 – Reactive Repository Layer
|  |  |
|---|---|
| **Status** | ✅ Accepted |
| **Context** | Must persist without blocking. |
| **Decision** | `ReactiveCrudRepository<LeadEntity, UUID>` on R2DBC Postgres; IDs generated in DB (`uuid_generate_v4()`). |
| **Consequences** | Back‑pressure to DB; JPA excluded from classpath. |

---

## ADR‑11 – CLI Command Pattern
|  |  |
|---|---|
| **Status** | 🟡 Under review |
| **Context** | Need batch validation from shell. |
| **Decision** | `LeadCrmValidatorCli` implements `CommandLineRunner`; may evolve to Spring Shell later. |
| **Consequences** | Simple container entry point; easy replacement when Ops finalises scheduling. |
