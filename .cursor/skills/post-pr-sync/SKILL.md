---
name: post-pr-sync
description: Sync local repository state after a PR merge, clean up merged branch safely, and prepare the next feature branch.
disable-model-invocation: true
---

# Post PR Sync

## Purpose
Sync the local repository after a PR is merged remotely, then prepare for the next feature.

## Required Inputs
- base branch (`main` or `master`)
- merged feature branch name when it is not the current branch
- whether to delete the merged local branch
- optional name for the next feature branch

## Workflow
1. Read repository state (`git status`, current branch, and branch tracking state).
2. If the working tree is not clean, stop and ask before any branch cleanup.
3. Confirm base branch (`main` or `master`) before syncing.
4. Fetch remote updates.
5. Switch to the base branch and fast-forward from remote.
6. Verify the merged feature branch is fully merged.
7. Ask for explicit confirmation before deleting any local merged branch.
8. Delete the merged local branch only after confirmation.
9. If a next feature branch name is provided, create and switch to it.
10. Return:
   - synced base branch and HEAD
   - deleted branch list
   - current active branch
   - remaining working tree status

## Confirmation Prompt Format
- Format dry-run and approval prompts as plain multiline text.
- Use real line breaks and bullets, not literal `\n`.
- End with a single explicit question: `Proceed with branch cleanup and sync actions?`.

## Hard Rules
- Never delete any branch without explicit confirmation.
- Never clean up branches when uncommitted changes are present unless user explicitly confirms.
- If base branch is ambiguous, stop and ask.
