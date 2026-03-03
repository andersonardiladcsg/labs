# CLAUDE.md

## Git Rules

- **Never force push** without explicit approval from the user.
- **Never amend commits** without explicit approval. Always create new commits by default. When a single-commit PR is required, amending is allowed to maintain that constraint.
- **Always ask before pushing** — never push to remote without explicit approval from the user. Before pushing, fetch the remote and check for conflicts. If there are conflicts, resolve them before pushing.
- **Pull before starting** — run `git pull` on the current branch at the start of every new task to ensure you're working with the latest changes. When creating a new branch from develop, pull develop first to avoid conflicts.
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

## Project Context

- This is a lab/testing repo (`andersonardiladcsg/labs`) used for prototyping before applying changes to `checkout-cart-engine`.
- The sanitization pipeline lives in `src/main/java/com/example/sanitizers/` and is orchestrated by `CartSanitizerService.java`.
- A custom Copilot agent (`.github/agents/sanitization-docs.agent.md`) maintains the sanitization pipeline documentation (`docs/cart-sanitization.md`) automatically.
- Pipeline documentation uses `<!-- AUTO-START -->` / `<!-- AUTO-END -->` markers — only content between them is agent-managed.
