# OpenCode 2 setup

This directory contains the agents, commands, and skills migrated from the original GitHub Copilot setup.

## Agents

All agents use `mode: all`, so they can run as a primary agent or be launched as a subagent. They inherit the session model.

| ID | Purpose |
|---|---|
| `analyst` | Generates the PRD from an idea or briefing. |
| `architect` | Generates the ADD and updates `AGENTS.md`. |
| `product-owner` | Writes feature, bug, or enhancement specifications. |
| `engineer` | Creates the issue JSON, branch, and implementation plan. |
| `coder` | Implements the plan and commits the code. |
| `tester` | Writes and runs E2E tests. |
| `devops` | Updates documentation, changelog, version, and merges. |

## Commands

| Command | Agent | Purpose |
|---|---|---|
| `/analyze` | `analyst` | Analyze a briefing and generate the PRD. |
| `/resolve-issue` | `coder` | Implement an issue from `specs/`. |
| `/start-feature` | `product-owner` | Run the complete workflow for a feature. |
| `/commit` | `build` | Commit pending changes. |

Examples:

```text
/analyze Read the briefing in astrobookings-cli.briefing.md
/resolve-issue feat-booking-management
/start-feature Add booking management
/commit
```

## Skills

Skills are discovered automatically from `.opencode/skills/<id>/SKILL.md`. Load one by ID (for example, `@committing-changes`) or ask an agent to use it.

## Workflow

```text
analyst -> architect -> product-owner -> engineer -> coder -> tester -> devops
```

Copilot handoffs have no direct equivalent in OpenCode. Each agent ends with a **Next step** section. Run the suggested command or ask the current agent to launch the next agent as a subagent.

## Issues

This setup uses local JSON issues in `specs/<id>.issue.json` instead of the GitHub MCP server. `experimental.todo` is enabled in `opencode.json` because the original Copilot agents used the todo tool.
