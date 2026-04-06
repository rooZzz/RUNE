# RUNE Project Skills

This directory contains manually-invoked Cursor Skills for repository workflow tasks.

## Invocation Names
- `/issue-create`
- `/issues-batch-create`
- `/git-prepare-commit`
- `/pr-create-main`
- `/post-pr-sync`

All skills in this directory are explicit-only and must be invoked manually.

## GitHub Integration Requirement
Issue and pull-request workflows in this directory are defined for `rooZzz/RUNE` and must use the existing `user-github` MCP integration for GitHub operations.
Do not use `gh` CLI for GitHub write actions in these workflows.

## Confirmation Prompt Format
All skills in this directory must format confirmation prompts as plain multiline text.
- Use real line breaks and bullets.
- Do not include literal escape sequences like `\n`.
- End with one explicit question line such as `Proceed with ...?`.

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

### `/post-pr-sync`
- Inputs: base branch, merged branch, delete-local-branch decision, optional next branch name.
- Confirmation gates: branch cleanup and deletion confirmation.
- Output: synced base status, cleanup actions, active branch, post-sync status.

## Scope Mapping
- Existing `.cursor/commands` files are Android emulator and runtime helpers:
  - `.cursor/commands/run-tv-emulator.md`
  - `.cursor/commands/rebuild-restart-app.md`
- `.cursor/skills` files are repository workflow helpers for issue, commit, PR, and post-merge sync flows.

These scopes are intentionally separated to avoid overlap.
