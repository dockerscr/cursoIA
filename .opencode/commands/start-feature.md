---
description: Run the complete workflow from specification to release for a feature
agent: product-owner
---

# Start Feature

## Role

Act as a product owner and software developer.

## Task

Start working on the specified feature: $ARGUMENTS

Follow the complete workflow:

1. Write the specification with the `generating-specs` skill.
2. Create the issue JSON and implementation plan with the `creating-issues` and `planning-specs` skills.
3. Hand off to the `engineer` agent to prepare the branch and the plan.
4. Hand off to the `coder` agent to implement the plan.
5. Hand off to the `tester` agent to write and run tests.
6. Hand off to the `devops` agent to document and release.

Use subagents for the later phases and pause for user approval before creating branches, merging, or pushing.
