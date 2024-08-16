# 2. HATEOAS

August 15, 2024


## Status

Accepted


## Context

We need to implement reliable and maintainable RESTful APIs that are easy to consume and evolve. We want to follow the best practices for designing RESTful APIs, as described by the resources listed below. We also want to provide a consistent experience for clients that consume our APIs.

### Resources
- [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)
- [The Engine of Application State](https://medium.com/api-center/the-engine-of-application-state-92bfdce0d41c)
- [Quick reference to HAL](https://apigility.org/documentation/api-primer/halprimer)
- [OpenAPI with a sprinkle ✨ of Hypermedia (video)](https://youtu.be/JNuk66FVSM0?t=240)
- [Zalando RESTful API and Event Guidelines](https://opensource.zalando.com/restful-api-guidelines)
- [adidas API Guidelines](https://adidas.gitbook.io/api-guidelines/)


## Decision

We will implement RESTful APIs that follow the HATEOAS principle. We will use the HAL specification to represent the resources and their relationships. We will provide links to related resources in the responses, so clients can navigate the API without hardcoding URLs.


## Consequences

- Our RESTful APIs will be more discoverable and easier to consume.
- Clients will be able to navigate the API by following links in the responses, which will make the API more flexible and evolvable.
- We will need to find a way to include references to the OpenAPI documentation in the responses, so clients can easily understand how to interact with the API.
- Implementation complexity will increase, as we will need to manage the relationships between resources and generate links dynamically.
