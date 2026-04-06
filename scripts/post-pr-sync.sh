#!/usr/bin/env bash
set -euo pipefail

base_branch="main"
source_branch="${1:-}"
stashed=0

current_branch="$(git rev-parse --abbrev-ref HEAD)"

if [[ -z "$source_branch" ]]; then
  source_branch="$current_branch"
fi

if [[ "$source_branch" == "$base_branch" ]]; then
  echo "Cannot delete base branch '$base_branch'. Pass merged feature branch explicitly."
  exit 1
fi

if [[ -n "$(git status --porcelain)" ]]; then
  git stash push -u -m "post-pr-sync: ${source_branch}"
  stashed=1
fi

git fetch origin
git switch "$base_branch"
git pull --ff-only origin "$base_branch"

if git show-ref --verify --quiet "refs/heads/${source_branch}"; then
  git merge-base --is-ancestor "$source_branch" "$base_branch"
  git branch -d "$source_branch"
else
  echo "Local branch '${source_branch}' not found, skipping delete."
fi

if [[ "$stashed" -eq 1 ]]; then
  git stash pop
fi

echo "Base branch: ${base_branch}"
echo "Deleted branch: ${source_branch}"
echo "Current branch: $(git rev-parse --abbrev-ref HEAD)"
git status --short --branch
