# Metronome — Copilot Instructions

## Project Overview

Metronome is a native Android metronome application built with **Kotlin** and **Jetpack Compose**. The package name is `com.ieshaan12.metronome`.

## Tech Stack

- **Language:** Kotlin
- **UI framework:** Jetpack Compose with Material 3
- **Build system:** Gradle (Kotlin DSL) with a version catalog (`gradle/libs.versions.toml`)
- **Min SDK:** 29 (Android 10) | **Target SDK:** 36
- **Architecture target:** Single-module (`app/`)

## Project Structure

```
app/src/main/java/com/ieshaan12/metronome/
├── MainActivity.kt          # Entry point, hosts Compose content
├── ui/theme/                 # Compose theming (Color, Theme, Type)
app/src/main/res/             # Resources (drawables, mipmaps, values, xml)
app/src/test/                 # Unit tests
app/src/androidTest/          # Instrumented tests
gradle/libs.versions.toml     # Dependency version catalog
```

## Coding Conventions

- Write all new code in **Kotlin**. Do not use Java.
- Use **Jetpack Compose** for all UI — no XML layouts.
- Follow Material 3 design guidelines and use the existing `MetronomeTheme`.
- Prefer `remember` / `rememberSaveable` for local state and `ViewModel` + `StateFlow` for screen-level state.
- Name composable functions with **PascalCase**; annotate previews with `@Preview`.
- Keep composables small and focused — extract reusable components into their own files under `ui/`.
- Use the Gradle version catalog (`libs.*`) when adding new dependencies.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Guidelines

- Prefer **coroutines** and `Flow` for async work; avoid callbacks where possible.
- Do not add unnecessary permissions to `AndroidManifest.xml`.
- Keep the `proguard-rules.pro` updated if adding libraries that require keep rules.
- Write unit tests for non-UI logic and Compose UI tests for screen behavior.
