# Documents - How to Read and Use This Pack

## Purpose
This folder provides an industry-style test documentation set for the **Expense Splitter** system, covering both:
- the **standalone/core domain library** (`expense-splitter.standalone`)
- the **webservice/Spring Boot application** (`expense-splitter`)

The intent is to give you a documentation pack that is useful for:
- interview submission support
- project handover
- QA execution
- regression planning
- auditability and traceability

## Recommended reading order
Read the files in this sequence:

1. **01_Test_Strategy.md**  
   Read this first. It explains the overall testing philosophy, scope, levels, non-goals, risks, and what “good coverage” means for this project.

2. **02_Master_Test_Plan.md**  
   This is the operational plan. It tells you what is being tested, by whom, in which environments, with what entry and exit conditions.

3. **03_Requirements_Traceability_Matrix.csv**  
   This is the control sheet. Use it to trace each major requirement to tests across standalone, webservice, and repository layers.

4. **04_Standalone_Test_Design_Spec.md**  
   Read this if you want to understand how the domain model and settlement algorithms should be tested in isolation.

5. **05_Webservice_Test_Design_Spec.md**  
   Read this if you want to understand how the controllers, validation, exception handling, service wiring, and persistence-backed behaviors should be tested.

6. **06_Test_Environment_and_Data_Management_Guide.md**  
   Use this when setting up execution locally or in CI, especially for PostgreSQL and Testcontainers-backed runs.

7. **07_Test_Case_Catalogue.md**  
   This is the practical catalogue of test scenarios. It is the fastest way to see exactly what should exist in the test suite.

8. **08_Test_Execution_and_Defect_Management_Guide.md**  
   Use this when you start running tests and logging failures.

9. **09_Quality_Gates_and_Exit_Criteria.md**  
   Read this when deciding whether the build is ready to merge, demo, or submit.

10. **10_Assumptions_Risks_and_Known_Issues.md**  
    Read this to understand current project-specific caveats, technical debt, and documentation assumptions.

## How to interpret each file
- **Strategy** = “why and what”
- **Plan** = “when, where, who, and with what controls”
- **Traceability matrix** = “show me requirement-to-test mapping”
- **Design specs** = “how should tests be structured”
- **Environment guide** = “how do I make tests runnable”
- **Case catalogue** = “what exact scenarios must exist”
- **Execution guide** = “how do I run and report”
- **Quality gates** = “what passes as done”
- **Assumptions/risks** = “what may still cause surprises”

## Intended audience
- You, as the primary developer
- Reviewer/interviewer
- QA or test engineer
- Future maintainer
- Anyone trying to validate persistence and settlement correctness after refactors

## Notes about project alignment
These documents are aligned to the codebase present in the supplied archive, including:
- standalone domain model with `Trip`, `Participant`, `Transaction`, `BasicSettler`, `SimplifiedSettler`
- Spring Boot webservice with `TripController`, `TripTransactionController`, `SettlementController`
- PostgreSQL-backed repositories
- current use of validation and exception handlers
- persisted settlement snapshot rewrite/clear behavior

If the code evolves, update the **traceability matrix**, **test catalogue**, and **known issues** first.
