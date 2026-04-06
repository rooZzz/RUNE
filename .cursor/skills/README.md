# RUNE Project Skills

This directory contains manually-invoked Cursor Skills for repository workflow tasks.

## Invocation Names
- `/issue-create`
- `/issues-batch-create`
- `/git-prepare-commit`
- `/pr-create-main`

All skills in this directory are explicit-only and must be invoked manually.

## GitHub Integration Requirement
Issue and pull-request workflows in this directory are defined for `rooZzz/RUNE` and must use the existing `user-github` MCP integration for GitHub operations.

## Inputs and Confirmation Points

### `/issue-create`
- Inputs: title, problem, desired outcome, acceptance criteria.
- Confirmation gates: duplicate-check review, then explicit create confirmation.
- Output: issue URL plus duplicate-check summary.

### `/issues-batch-create`
- Inputs: backlog/source text describing multiple stories.
- Confirmation gates: per-story duplicate-check report, then explicit batch create confirmation.
- Output: created issue URLs and skipped items with reasons.

### `/git-prepare-commit`
- Inputs: commit intent, optional scope/ticket, include or exclude file intent.
- Confirmation gates: branch decision and staging dry-run confirmation.
- Output: branch name, commit SHA, final commit message, post-commit status.

### `/pr-create-main`
- Inputs: PR summary intent, test plan, linked issues when available.
- Confirmation gates: pre-publish PR dry-run confirmation.
- Output: PR URL and final published metadata.

## Scope Mapping
- Existing `.cursor/commands` files are Android emulator and runtime helpers:
  - `.cursor/commands/run-tv-emulator.md`
  - `.cursor/commands/rebuild-restart-app.md`
- `.cursor/skills` files are repository workflow helpers for issue, commit, and PR flows.

These scopes are intentionally separated to avoid overlap.
