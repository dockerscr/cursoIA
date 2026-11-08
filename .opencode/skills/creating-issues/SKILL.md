---
name: creating-issues
description: >
  Creates an issue based on a provided specification or context.
  To be used to create issues for features, bug corrections, or enhancements.
---

# Creating JSON Issues Skill

When asked for creating a JSON issue, follow these steps:

1. **Capture inputs**:
  - Draft the issue title from the specification or context; if unclear, ask.
  - Identify the repo remote URL from Context. 
  - If not, identify it by using local git commands to get the remote URL.
    - Save the remote URL in the root `AGENTS.md` file for later use.
   
2. **Create the JSON issue**:
  - Create the issue in the specs folder naming it `<id>.issue.json`.
  - Set the issue title and body based on the provided specification or context.
  - Add a label `bug` or `enhancement` based on the content.
  - Save the issue file path for reference and the ID for tracking.

3. **Double-link to specification (if applicable)**:
  - If the issue is created based on a specification file:
    - Add the issue file path back to the specification for traceability.
    - Add the specification file path to the issue body for context.

## Example

```json
{
  "title": "Feature: Add new user",
  "body": "Add a new user to the system",
  "steps": [
    {
      "title": "Step 1",
      "description": "Description of the step",
      "tasks": [
        {
          "done": false,
          "title": "Task 1",
          "description": "Description of the task"
        }
      ]
    }
  ],
  "labels": ["feature"]
}
```

