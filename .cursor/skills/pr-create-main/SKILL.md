---
name: pr-create-main
description: Create a pull request to main for this repository with branch validation, structured summary, and explicit confirmation before publish.
disable-model-invocation: true
---

# PR Create Main

## Purpose
Create a pull request targeting `main` for repository `rooZzz/RUNE`.

## Required Inputs
- PR intent or summary
- test plan items
- linked issue references when available

## Workflow
1. Confirm current branch is not `main`.
2. Inspect branch status, commits, and diff from `main`.
3. Build proposed PR title and body from branch changes.
4. Present a dry-run summary:
   - base branch `main`
   - head branch
   - title
   - summary bullets
   - test plan checklist
   - linked issues
5. Ask for explicit confirmation to publish.
6. Push branch if needed.
7. Create PR through `user-github` MCP tools and return PR URL.

## PR Body Shape
1. Summary
2. Test plan
3. Linked issues

## Hard Rules
- Base branch must be `main`.
- Never create PR from `main`.
- Use `user-github` MCP integration for all GitHub operations in this workflow.
- Do not use `gh` CLI for PR creation in this workflow.
- If branch or remote state is unclear, stop and ask.
