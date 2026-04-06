---
name: issue-create
description: Create one GitHub issue for this repository with mandatory duplicate checks and explicit user confirmation before writing.
disable-model-invocation: true
---

# Issue Create

## Purpose
Create exactly one issue in `rooZzz/RUNE` using the existing `user-github` MCP integration.

## Required Inputs
- issue title
- problem statement
- desired outcome
- acceptance criteria or completion signals
- issue type hint (`bug`, `feature`, or `custom`) when provided

## Workflow
1. Confirm the request is for repository `rooZzz/RUNE`.
2. If any required input is missing, stop and ask for the missing fields.
3. Build a duplicate-search query from title and key terms.
4. Use `user-github` MCP issue search/read tools to find related open and recent closed issues.
5. Present likely duplicates with links and short rationale.
6. Present a dry-run summary with:
   - proposed title
   - proposed body sections
   - labels if requested
   - duplicate findings
7. Ask for explicit confirmation to proceed.
8. Only after confirmation, create the issue through `user-github` MCP write tools.
9. Return the created issue URL and a short post-action summary.

## Body Shape
Use this section order unless the user specifies otherwise:
1. Problem
2. Desired solution
3. Alternatives considered
4. Additional context
5. Acceptance criteria

## Hard Rules
- Use `user-github` MCP integration for all GitHub operations in this workflow.
- Do not create an issue before duplicate check and explicit confirmation.
- If MCP auth/tool access fails, stop and ask the user how to proceed.
