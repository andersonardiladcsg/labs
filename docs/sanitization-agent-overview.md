# Automating Cart Sanitization Pipeline Documentation with a Custom GitHub Copilot Agent

## The Problem

Our cart sanitization pipeline (`CartSanitizerService`) runs 5+ sanitizers in sequence every time a cart is retrieved or modified. Each sanitizer has its own configuration properties, external service dependencies, and specific behaviors. The only documentation for this pipeline lives in a Confluence page that we rarely update — it's consistently out of date, and developers end up reading source code directly to understand how the pipeline works.

We needed documentation that **stays accurate automatically** and lives close to the code, without relying on someone remembering to go update a Confluence page after every code change.

## Options We Explored

### 1. GitHub Actions workflow + shell script calling an LLM API

Our initial approach was a GitHub Actions workflow triggered on PR merges to `develop`. It would compute the sanitizer diff, pass it to an LLM via the GitHub Models API (`openai/gpt-4o`), and create a docs-update PR with the AI-generated changes.

- **Pros:** Fully automated, no manual trigger needed
- **Cons:** Required maintaining a shell script with JSON construction via `jq`, retry logic, response validation, and token limit management. The script alone was ~150 lines. Debugging failures in CI was painful — you'd have to read workflow logs to understand what the AI decided to do and why. The LLM received only the diff (not full context), which led to incomplete updates when multiple sanitizers changed simultaneously.

### 2. Manual documentation updates (status quo)

Just ask developers to update docs when they change sanitizers.

- **Pros:** Simple, no infrastructure
- **Cons:** Doesn't work. Documentation consistently drifts. Developers focus on the code change, not the doc change. PRs get merged without doc updates.

### 3. Custom GitHub Copilot Agent (what we built)

A `.github/agents/sanitization-docs.agent.md` file that defines a specialized Copilot coding agent. When invoked from the GitHub Agents tab (or assigned to a PR), it reads the source code, determines what changed, and opens a PR updating the documentation.

- **Pros:** No infrastructure to maintain (no scripts, no workflow files, no API keys). The agent has full access to the repo via Copilot's built-in tools (`read`, `edit`, `search`, `execute`). It reads ALL sanitizer source files — not just the diff — so it always produces complete, accurate documentation. The agent definition is a single markdown file that's easy to read and modify.
- **Cons:** Requires manual trigger (not fully automated on merge). This is actually a feature — it gives us control over when docs are updated and lets us review before merging.

## Why the Custom Agent Is the Right Choice

1. **Zero infrastructure.** The entire agent is a single markdown file (`.github/agents/sanitization-docs.agent.md`). No workflows, no scripts, no API keys, no token management. GitHub hosts and runs it.

2. **Full codebase context.** Unlike the shell script approach (which sent only the diff to an LLM), the agent reads every sanitizer file, the pipeline orchestrator, and `application.properties` on every run. This means it catches indirect changes and always produces a complete, consistent document.

3. **Built-in safety guards.** The agent has multiple layers of protection against bad updates:
   - **Pre-flight check**: Won't run if there's already an open docs PR (prevents conflicts)
   - **Empty diff detection**: If no sanitizer code changed since the last update, it stops immediately — no empty PRs
   - **Content-before-footer check**: Only updates the "Last updated" footer if actual documentation content changed (this was a real bug we caught and fixed)
   - **Self-verification**: Before committing, it validates marker presence, sanitizer count matches, source file links exist, and footer is correct
   - **Orphan detection**: Flags sanitizer files that exist on disk but aren't in the pipeline (and vice versa)

4. **Structured, consistent output.** The agent follows a strict document structure: pipeline table, then per-sanitizer sections with Purpose, Behavior, Configuration Properties, and External Dependencies. Every section reads like it was written by the same person.

5. **Auditable.** Every docs update comes as a PR with a structured description showing what was added/updated/removed, related commits, ticket IDs, and verification checks. Code review still applies.

## How It Works

The agent follows a 6-step pipeline:

| Step | What it does |
|------|-------------|
| **Pre-flight** | Checks for existing open docs PRs to prevent conflicts |
| **1. Baseline** | Finds the last commit SHA when docs were updated (from the doc footer) |
| **2. Check** | Diffs sanitizer files from baseline to HEAD — stops if nothing changed |
| **3. Classify** | Categorizes changes as added, modified, deleted, or renamed |
| **4. Read** | Reads ALL sanitizer source files (not just changed ones) for full context |
| **5. Update** | Modifies only the content between `<!-- AUTO-START -->` / `<!-- AUTO-END -->` markers |
| **6. Verify** | Validates the output before committing |

It then creates a single-commit PR targeting `develop` with the `documentation` label.

## The Documentation File

The agent maintains `docs/cart-sanitization.md` — a single reference document for the entire sanitization pipeline. It includes:

- Pipeline overview with a link to `CartSanitizerService.java`
- Execution order table with source file links
- Per-sanitizer sections with behavior, config properties (with defaults), and external service dependencies
- Auto-update markers so the agent knows exactly what it can modify
- Footer with the last-updated date and commit SHA (used as the baseline for the next run)

## How to Use It

1. Make your sanitizer code changes and merge to `develop` as usual
2. Go to the **Agents** tab in the repository on GitHub
3. Select **sanitization-docs** and run it
4. Review the PR it creates and merge when satisfied

## Files

| File | Purpose |
|------|---------|
| `.github/agents/sanitization-docs.agent.md` | The agent definition |
| `docs/cart-sanitization.md` | The documentation it maintains |

The agent file is in the repo — read it to see the full specification. It's plain markdown and self-documenting.
