---
name: post-pr-sync
description: Sync local repository state after a PR merge by running the project script that stashes changes, updates main, deletes merged branch, and restores stash.
disable-model-invocation: true
---

# Post PR Sync

## Purpose
Run the project post-merge sync script to stash local changes, update `main`, delete the merged local branch, and restore stashed changes.

## Required Inputs
- merged feature branch name when it is not the current branch

## Workflow
1. Read repository state (`git status`, current branch, and branch tracking state).
2. Determine target merged branch:
   - use provided branch name when given
   - otherwise use current branch
3. Present a dry-run summary including script invocation:
   - `./scripts/post-pr-sync.sh <merged_branch>`
4. Ask for explicit confirmation.
5. Execute `./scripts/post-pr-sync.sh <merged_branch>`.
6. Return:
   - synced base branch and HEAD
   - deleted branch list
   - current active branch
   - remaining working tree status

## Confirmation Prompt Format
- Format dry-run and approval prompts as plain multiline text.
- Use real line breaks and bullets, not literal `\n`.
- End with a single explicit question: `Proceed with branch cleanup and sync actions?`.

## Hard Rules
- Always use `main` as the base branch.
- Always delete the merged local branch after sync.
- Never create a next feature branch in this workflow.
- Always run the repository script `scripts/post-pr-sync.sh` for this workflow.
- Never replace script behavior with ad hoc git commands.
