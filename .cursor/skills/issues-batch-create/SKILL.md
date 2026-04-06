---
name: issues-batch-create
description: Create multiple GitHub issues for this repository from provided context with per-item duplicate checks and explicit confirmation.
disable-model-invocation: true
---

# Issues Batch Create

## Purpose
Create multiple issues in `rooZzz/RUNE` using the existing `user-github` MCP integration.

## Required Inputs
- source backlog text, notes, or requirements
- batching preference if provided
- issue type hints when provided

## Workflow
1. Confirm the request is for repository `rooZzz/RUNE`.
2. Parse candidate issues from the provided context.
3. If no candidates are clear, stop and ask for clearer story boundaries.
4. For each candidate issue:
   - generate concise title
   - generate body sections
   - run duplicate search via `user-github` MCP issue search/read tools
5. Present a dry-run table that includes:
   - title
   - one-line scope
   - likely duplicates
   - proposed action (`create`, `skip as duplicate`, or `needs split/merge`)
6. Ask for explicit confirmation for the final create list.
7. Create only approved issues through `user-github` MCP write tools.
8. Return created issue URLs and skipped items with reasons.

## Batch Limits
- Default to creating at most 5 issues per run unless the user confirms a higher number.
- Keep each issue scoped to one shippable outcome.

## Hard Rules
- Use `user-github` MCP integration for all GitHub operations in this workflow.
- Do not create any issue without duplicate checks and explicit confirmation.
- If MCP auth/tool access fails, stop and ask the user how to proceed.
