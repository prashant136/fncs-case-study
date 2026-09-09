# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes. I would gradually make the database access more consistent.

Currently, Product uses a Panache repository, Store uses the Active Record approach, and Warehouse uses a domain + repository/port approach.

I would keep the Warehouse approach because it separates business logic from database details and is easier to test and change later.

For Product and Store, I would prefer repository classes as well. This would keep database operations in one place and make the resources/controllers responsible mainly for HTTP handling.

I would not do a big rewrite immediately. I would refactor gradually when modifying each feature, so the risk of introducing regressions stays low.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Both approaches are valid, depending on the project.

Using OpenAPI for Warehouse gives us a clear API contract and keeps the API documentation and implementation aligned. It is especially useful when multiple teams or clients depend on the API. The downside is that generated code can add complexity and can feel slower to change for a small project.

Coding Product and Store directly is simpler and faster, especially for small APIs. However, the API contract can become less explicit and documentation can drift from the actual implementation.

For a larger production system, I would prefer OpenAPI-first for public or shared APIs. It gives the team a clear contract and makes API changes easier to review. For small internal APIs, direct implementation can be reasonable.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would focus first on tests for business-critical logic, especially the Warehouse use cases because they contain most of the business rules.

My priority would be:

1. Unit tests for validation and business rules.
2. Repository/database integration tests to verify persistence and queries.
3. REST/API tests for important success and error scenarios.
4. A small number of end-to-end tests for the main flows.

I would especially test edge cases such as duplicate business unit codes, invalid locations, capacity limits, stock limits, archiving, and warehouse replacement.

To keep coverage effective over time, I would run tests and JaCoCo coverage in CI and enforce a minimum coverage threshold. I would also review whether important business rules are actually tested instead of focusing only on increasing the percentage.
```