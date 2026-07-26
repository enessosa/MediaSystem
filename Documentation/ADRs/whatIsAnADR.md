## Why do I use ADRs?

I think the most valuable lesson this whole Project will teach me is the importance of Documentation and Planning before even beginning to write the code. Therefore i searched for Structured way, to reason all of the important architectural decisions.


(AI from here on)

# What is an ADR?

An Architecture Decision Record (ADR) is a short document that captures a
single significant architectural or technical decision, along with the
reasoning behind it. Instead of only recording *what* was decided, an ADR
also preserves *why* it was decided, which alternatives were considered,
and what trade-offs were accepted.

## Why use ADRs?

- **Traceability:** Future contributors (including the original author,
  later on) can understand why the system looks the way it does, without
  having to guess or reconstruct the reasoning from code alone.
- **Avoiding repeated discussions:** Alternatives that were already
  considered and rejected are documented, so they aren't re-evaluated
  from scratch every time the topic comes up again.
- **Lightweight process:** An ADR is deliberately short and focused on one
  decision, making it easy to write and quick to read.

## Structure used in this project

Each ADR in this folder follows the same layout:

- **Status** — e.g. Proposed, Accepted, Superseded
- **Date** — when the decision was made
- **Context** — the problem or situation that required a decision
- **Decision** — what was decided
- **Alternatives Considered** — other options and why they were rejected
- **Consequences** — positive and negative effects of the decision
