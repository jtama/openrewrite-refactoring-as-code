<!--
- Version change: 1.0.0 → 2.0.0
- List of modified principles:
  - Removed: IV. No Unsafe Dependencies
- Added sections: none
- Removed sections: none
- Templates requiring updates:
  - ✅ .specify/templates/plan-template.md
- Follow-up TODOs: none
-->
# Toxic Library Remover Constitution

## Core Principles

### I. Declarative Recipes
Recipes MUST be written in a declarative way, using OpenRewrite's YAML format whenever possible. Java-based recipes SHOULD be reserved for complex logic that cannot be expressed in YAML.

### II. Idempotency
Recipes MUST be idempotent. Running a recipe multiple times on the same codebase MUST produce the same result as running it once.

### III. Test-Driven Development
Every recipe MUST have a corresponding test case that verifies its correctness. Tests MUST cover both the "before" and "after" state of the code.

### IV. Performance Matters
Recipes SHOULD be efficient and not introduce significant performance overhead, especially when run on large codebases.

## Development Workflow

All new recipes MUST be defined in `src/main/resources/META-INF/rewrite/rewrite.yml`.
Java code for recipes SHOULD be placed in `src/main/java/com/github/jtama/openrewrite/`.
Tests MUST be placed in `src/test/java/com/github/jtama/openrewrite/`.

## Quality Gates

All code MUST be formatted according to the `.editorconfig` file.
All tests MUST pass before a change is merged.
A pull request MUST be reviewed and approved by at least one other contributor.

## Governance

Changes to the constitution MUST be proposed via a pull request and approved by the project maintainers.
The constitution version WILL be updated according to semantic versioning.

**Version**: 2.0.0 | **Ratified**: 2025-10-21 | **Last Amended**: 2025-10-21
