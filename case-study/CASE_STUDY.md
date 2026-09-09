# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
I would first understand where the costs come from and how they should be assigned to Warehouses and Stores.

- Which system is the source of cost data?
- Which costs are direct and which are shared?
- How should shared costs such as rent or transportation be divided?
- Should costs be tracked by Location, Warehouse, Store or Product?
- How often do Finance and Operations need the reports?
- How should corrections or late cost data be handled?

The main goal is to have one clear and consistent way of calculating costs so that Finance and Operations see the same numbers.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
I would first identify the biggest cost drivers and compare them with operational performance.

Possible areas to optimize are:

- Better Warehouse capacity utilization.
- Reducing unnecessary transportation and combining shipments where possible.
- Improving inventory placement.
- Reducing excess or slow-moving inventory.
- Optimizing staffing and overtime.
- Removing unnecessary manual work.

I would prioritize ideas based on **expected savings, effort, risk and impact on customer service**. I would start with small, low-risk improvements, measure the results and then roll out successful changes.


## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
The Cost Control Tool should use reliable financial data so that Finance and Operations do not have different numbers.

Integration would help with:

- More accurate cost reporting.
- Less manual work.
- Faster reporting.
- Better reconciliation.
- Better budgeting and forecasting.
- Easier auditing.

Before designing the integration, I would understand which system is the source of truth and what data needs to move between systems.


I would use APIs or events depending on what the existing financial system supports, with retries, validation and monitoring.


## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
Budgeting tells us what we plan to spend, while forecasting tells us what we currently expect to spend.

For fulfillment, the forecast should consider:

- Expected order volume.
- Inventory levels.
- Warehouse capacity and utilization.
- Labor costs.
- Transportation costs.
- Seasonal changes.
- Historical costs.
- Planned Warehouse changes.

The system should make it easy to compare **budget vs actual vs forecast**.


I would start with a simple approach based on historical costs and business drivers, then make it more advanced if the business actually needs it.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
When replacing a Warehouse, I would not overwrite the old Warehouse record because its cost history is valuable.

The old Warehouse should be archived and the new Warehouse should become active. This is especially important because the Business Unit Code is reused.

Keeping the history allows the company to:

- Compare old and new Warehouse costs.
- Check whether the replacement actually saved money.
- Keep historical budgets and actual costs.
- Support audits and financial reporting.

I would clearly define the transition date so that costs before and after the replacement are separated correctly.

The main principle is **preserve history, don't overwrite it**. This makes it possible to compare the old Warehouse baseline with the new Warehouse's actual costs and check whether the replacement stayed within budget.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
