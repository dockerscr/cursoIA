#!/usr/bin/env bash
set -euo pipefail

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "CURRENT_BRANCH:$CURRENT_BRANCH"

STATUS=$(git status --porcelain)
if [ -n "$STATUS" ]; then
  echo "UNCOMMITTED_CHANGES:true"
else
  echo "UNCOMMITTED_CHANGES:false"
fi

git fetch origin

if git ls-remote --heads origin main | grep -q refs/heads/main; then
  DEFAULT_BRANCH=main
else
  DEFAULT_BRANCH=master
fi
echo "DEFAULT_BRANCH:$DEFAULT_BRANCH"

if [ "$CURRENT_BRANCH" = "$DEFAULT_BRANCH" ]; then
  echo "Already on the default branch. Nothing to merge."
  exit 0
fi

git checkout "$DEFAULT_BRANCH"
git pull origin "$DEFAULT_BRANCH"
git merge --no-ff "$CURRENT_BRANCH" -m "chore: merge branch $CURRENT_BRANCH into $DEFAULT_BRANCH"
git push origin "$DEFAULT_BRANCH"
