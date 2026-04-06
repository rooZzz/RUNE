---
name: git-prepare-commit
description: Prepare branch and commit for this repository with branch guards, explicit staging intent, and project-aligned commit message style.
disable-model-invocation: true
---

# Git Prepare Commit

## Purpose
Prepare a branch, create one commit, and push to remote for the current local changes.

## Required Inputs
- commit intent (`feat`, `fix`, `refactor`, `docs`, `test`, `chore`) when known
- optional scope and ticket reference
- optional list of files to include or exclude

## Workflow
1. Read repository state (`git status`, current branch, staged/unstaged changes).
2. If on `main`, create and switch to a feature branch before committing.
3. Propose branch name in kebab-case and confirm it if ambiguity exists.
4. Present a dry-run summary of files to be staged.
5. Ask for explicit confirmation before staging and committing.
6. Stage only intended files.
7. Create one commit with concise imperative subject aligned to repository style.
8. Push the current branch to its remote after confirmation.
9. Return:
   - branch name
   - commit SHA
   - final commit message
   - push result
   - remaining working tree status

## Commit Message Style
- imperative mood
- concise subject line
- no trailing decoration unless user requests

## Hard Rules
- Never commit on `main`.
- Never include likely secret files.
- If there are ambiguous staged-file decisions, stop and ask.
