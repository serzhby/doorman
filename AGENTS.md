# AGENTS.md

This file provides guidance for agentic coding tools working in this repository.
It summarizes how to build, test, and follow local conventions.

## Repo overview
- IntelliJ Platform plugin written in Kotlin.
- Gradle build with the IntelliJ Platform Gradle plugin.
- Main code in `src/main/kotlin` and resources in `src/main/resources`.
- Tests live in `src/test/kotlin` with test data in `src/test/testData`.

## Build, run, lint, and test

### Common Gradle commands
- `./gradlew build` — compile and run unit tests.
- `./gradlew clean build` — clean build and tests.
- `./gradlew buildPlugin` — build the IntelliJ plugin ZIP.
- `./gradlew runIde` — launch IDE with the plugin for manual testing.
- `./gradlew runIdeForUiTests` — run IDE with robot server settings.
- `./gradlew verifyPlugin` — IntelliJ plugin verification.
- `./gradlew test` — run all tests.
- `./gradlew check` — run verification tasks (tests + reports).

### Run a single test
- `./gradlew test --tests "com.github.serzhby.tools.plugins.doorman.MyPluginTest"`
- `./gradlew test --tests "com.github.serzhby.tools.plugins.doorman.MyPluginTest.testXMLFile"`

### Lint and static analysis
- Qodana is configured in `qodana.yml`.
- Run locally with `./gradlew qodanaScan` (requires Qodana setup).
- There is no dedicated Kotlin formatter/ktlint task configured.

### Publishing and signing (local scripts)
- `build.sh` runs `clean buildPlugin signPlugin` and expects signing env vars.
- Do not commit credentials or generated signing artifacts.

## Code style guidelines

### Formatting
- Use 2-space indentation (no tabs).
- Use IntelliJ Kotlin defaults for wrapping and spacing.
- Keep line lengths reasonable; prefer wrapping long argument lists.
- Use trailing commas only if the file already uses them (current code mostly doesn’t).

### Imports
- Use explicit imports; no wildcards.
- Group imports by Kotlin style: stdlib, third-party, IntelliJ, then project.
- Keep imports sorted alphabetically within groups.
- Avoid unused imports; let IntelliJ optimize them.

### Naming
- Packages use lowercase and reflect the directory structure.
- Classes and objects use `UpperCamelCase`.
- Functions and properties use `lowerCamelCase`.
- Constants use `UPPER_SNAKE_CASE` and live in `companion object` or `private object`.
- Boolean properties/functions use `is/has/can/should` prefixes where natural.

### Types and Kotlin idioms
- Prefer `val` over `var` unless mutation is required.
- Use type inference for locals; add explicit types for public API clarity.
- Prefer data classes for DTO-like models.
- Use `sealed` types when modeling closed sets of variants.
- Use nullable types sparingly; prefer safe access and early returns.
- Use Kotlin collections and standard library functions (`map`, `filter`, `let`, `runCatching`).

### Error handling
- Throw domain-specific exceptions for expected failures (see `boundary/exceptions`).
- Use `runCatching` or `try/catch` around external calls.
- Report UI-facing errors on the EDT thread and use `Messages` dialogs.
- Avoid swallowing exceptions; log them via `thisLogger()` when appropriate.

### Concurrency and threading
- Use coroutines for async operations.
- IO operations should run on `Dispatchers.IO`.
- UI operations should run on `Dispatchers.EDT`.
- When using background progress, wrap work in `withModalProgress`.

### IntelliJ Platform conventions
- Use `@Service` for application-level services.
- Access services via `project.service<T>()` or `service<T>()`.
- Keep action logic in `AnAction` subclasses; use `update` to control enablement.
- Use `DataKey` for passing context in actions.
- UI strings belong in `src/main/resources/messages/BoundaryBundle.properties`.

### Files and resources
- Keep Kotlin sources under `src/main/kotlin` following package structure.
- Keep icons in `src/main/resources/icons`.
- Keep plugin metadata in `src/main/resources/META-INF/plugin.xml`.
- Keep test data under `src/test/testData` and reference with `@TestDataPath`.

## Testing guidance
- Tests use `BasePlatformTestCase`.
- Use `myFixture` helpers for file-based tests.
- Prefer focused tests that validate IntelliJ PSI behavior or plugin logic.
- For rename and refactor tests, place test data in `src/test/testData`.

## Safe changes and housekeeping
- Avoid changing Gradle wrapper versions unless required.
- Do not add new build tools without a clear need.
- Keep changes minimal and consistent with existing patterns.
- Update `CHANGELOG.md` only when explicitly requested.

## Cursor/Copilot rules
- No `.cursor/rules`, `.cursorrules`, or `.github/copilot-instructions.md` found.
- If these appear in future, update this file to mirror them.

## Notes for agents
- This repo targets IntelliJ Platform 2025.1.x and Kotlin/JVM 21.
- The Boundary CLI is invoked by the plugin; ensure commands are assembled safely.
- Use `BoundarySettings` for configurable paths instead of hardcoding.
- Enable debug logs via **Help → Diagnostic Tools → Debug Log Settings**, add `#com.github.serzhby.tools.plugins.doorman`, then inspect `idea.log` via **Help → Show Log in Explorer/Finder**.
- Treat `build.sh` as a local helper; avoid using it in automated CI.
