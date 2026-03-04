# CLAUDE.md

## Git Rules

- **Never force push** without explicit approval from the user.
- **Never amend commits** without explicit approval. Always create new commits by default. When a single-commit PR is required, amending is allowed to maintain that constraint.
- **Always ask before pushing** — never push to remote without explicit approval from the user. Before pushing, run `git fetch origin` then `git rebase origin/<branch>` to incorporate remote changes without merge commits. If rebase conflicts arise, resolve them before pushing.
- **Sync before starting** — run `git fetch origin && git rebase origin/<branch>` on the current branch at the start of every new task. When creating a new branch from develop, fetch and rebase develop first (`git fetch origin && git rebase origin/develop`) to branch from the latest state.
- **Default branch is `develop`** — PRs should target `develop`, not `main`.
- **Commit message format:** `TICKET-ID | Description` (e.g., `PROJ-1001 | Add backorder support`). Use `NON-JIRA |` prefix when there is no associated ticket.
- **Branch naming:** `<prefix>/<short-description>` (e.g., `PROJ-1001/add-backorder-support`, `NON-JIRA/auto-sanitization-docs-agent`, `test/sanitization-docs-agent`).
- Don't create PRs unless explicitly asked.
- **No Co-Authored-By** — never append `Co-Authored-By` trailers to commit messages.
- Don't skip hooks (`--no-verify`) or bypass signing unless explicitly asked.

## Code & Changes

- Read files before modifying them — understand existing code first.
- Don't make changes beyond what was asked. No unsolicited refactors, comments, or "improvements".
- Don't create documentation files (README, .md) unless explicitly requested.
- Prefer editing existing files over creating new ones.

## Communication

- Be concise. Skip unnecessary preamble.
- Don't ask for confirmation on safe, reversible operations (file edits, reads, local git commands).
- Always ask before destructive or irreversible operations (force push, reset, delete branches, drop data).
- When creating PRs, include a clear summary with sections for changes, test plan, and related tickets.

## Maintaining This File

- If a conversation establishes a new convention, preference, or project context that would apply across sessions, update this file as part of the same commit.
- If the user explicitly asks to "remember" or "always do" something, add it to the appropriate section.
- If an existing rule is contradicted by the user, update or remove it.
- Don't add speculative or one-off rules — only stable patterns confirmed by the user.

## Agent Task Rules

- **Never call `report_progress` before confirming there are actual file changes to commit.** Calling `report_progress` creates a commit and pushes a branch, which opens a PR. If a task turns out to be a no-op (e.g., the sanitization-docs agent finds no diff), no `report_progress` call must be made — end the task with a plain-text reply instead.
- This applies especially to the sanitization-docs agent: if the diff against baseline is empty, do **not** call `report_progress` at all. Not even for an "Initial plan".

## Project Context

- This is a lab/testing repo (`andersonardiladcsg/labs`) used for prototyping before applying changes to `checkout-cart-engine`.
- The sanitization pipeline lives in `src/main/java/com/example/sanitizers/` and is orchestrated by `CartSanitizerService.java`.
- A custom Copilot agent (`.github/agents/sanitization-docs.agent.md`) maintains the sanitization pipeline documentation (`docs/cart-sanitization.md`) automatically.
- Pipeline documentation uses `<!-- AUTO-START -->` / `<!-- AUTO-END -->` markers — only content between them is agent-managed.
