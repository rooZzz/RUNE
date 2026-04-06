---
name: issue-plan
description: Build a comprehensive implementation plan from a GitHub issue. Use when the user asks to plan issue delivery, break down issue execution, or generate an evidence-backed implementation plan. This skill requires Plan Mode, codebase and external research with cited evidence, behavior-driven test planning, and no fallback/backwards-compatibility additions unless explicitly approved.
disable-model-invocation: true
---

# Issue Plan

## Purpose
Create a comprehensive, evidence-backed implementation plan from a GitHub issue for `rooZzz/RUNE`.

## Mandatory Operating Rules
1. Always switch to Plan Mode before planning work.
2. Treat this as a research-first workflow. Do not produce a final plan until evidence is gathered.
3. Research at two levels:
   - codebase-level evidence from current repository implementation
   - external evidence when needed for APIs, platform behavior, framework guidance, or uncertainty resolution
4. Every non-trivial planned change must be justified by explicit evidence.
5. Plan must be behavior-driven and test-driven:
   - define expected user-visible behavior first
   - define verification strategy and tests before implementation steps
   - extend existing test frameworks and patterns when coverage is missing
6. Do not include backwards-compatibility paths, fallbacks, polyfills, or defensive legacy branches unless the user explicitly requests and approves them.
7. If required evidence cannot be found, stop and report the gap instead of guessing.

## Inputs
- issue URL or issue number
- repository confirmation (`rooZzz/RUNE`)
- constraints from user (scope, timelines, exclusions)

## Workflow
1. Enter Plan Mode.
2. Read the issue details and clarify missing boundaries.
3. Extract concrete goals:
   - problem statement
   - in-scope behaviors
   - out-of-scope behaviors
   - acceptance criteria
4. Run codebase research:
   - locate relevant modules, entry points, and tests
   - identify existing architecture and conventions to reuse
   - identify risks, dependencies, and migration touchpoints
5. Run external research when uncertainty exists:
   - use authoritative sources
   - capture only information that directly affects implementation choices
6. Build an evidence log:
   - each evidence item includes source and why it matters
   - map evidence to specific planned changes
7. Produce an implementation plan:
   - ordered phases with concrete tasks
   - each task includes objective, affected areas, and evidence references
   - explicit testing tasks for unit/integration/e2e as applicable
   - explicit behavior verification steps for acceptance criteria
8. Run a compliance pass on the plan:
   - Plan Mode used
   - evidence cited for all major changes
   - test and behavior coverage included
   - no fallback/backwards-compatibility work unless explicitly requested

## Output Format
Use this structure:

### Issue Understanding
- concise restatement of issue goal
- scope boundaries

### Evidence
- codebase evidence bullets with source paths/symbols
- external evidence bullets with links

### Behavior Contract
- expected user-visible behaviors
- non-goals

### Implementation Plan
- numbered phases
- concrete tasks per phase
- evidence references for each major task

### Test Plan
- tests to add/update by layer
- frameworks/patterns being extended
- behavior validation checklist mapped to acceptance criteria

### Risks and Open Questions
- unresolved unknowns
- blocking questions requiring user input

## Hard Stop Conditions
- issue scope is ambiguous and cannot be bounded
- required evidence is unavailable
- requested approach conflicts with no-fallback rule and no explicit approval exists
