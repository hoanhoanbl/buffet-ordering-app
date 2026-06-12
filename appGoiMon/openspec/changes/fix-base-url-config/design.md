## Context

The Android app currently has a hardcoded base URL (`http://192.168.1.56/appOrder/appGoiMon_API/`) in `RetrofitClient.kt`. This creates issues when:
- Different developers work on different networks with different server IPs
- Testing on Android Emulator (requires `10.0.2.2` instead of `192.168.x.x`)
- Moving from development to production environments

The project uses Gradle with Kotlin DSL for build configuration. The existing `RetrofitClient` is a singleton object that provides a Retrofit `ApiService` instance. No production deployment URL exists yet, and the team primarily works in development mode.

## Goals / Non-Goals

**Goals:**
- Allow each developer to configure their own API base URL without modifying committed code
- Support different URLs for emulator vs physical devices
- Prepare for future production deployment with different URL
- Keep configuration simple and familiar to Android developers

**Non-Goals:**
- Runtime URL switching (changing URL without rebuilding the app)
- UI for entering base URL in the app
- Support for multiple simultaneous API endpoints
- Authentication or API security (separate concern)

## Decisions

### Use local.properties with BuildConfig

Use `local.properties` to store the base URL, read it in `build.gradle.kts`, and expose via BuildConfig.

**Rationale:** 
- `local.properties` is already gitignored (standard Android practice)
- Each developer can configure their own without conflicts
- BuildConfig is compile-time, making it type-safe and performant
- Falls back to sensible default if property is missing

**Alternative considered:** Flavor-based BuildConfig (debug/release). This would require defining URLs at build time for all developers, defeating the purpose of per-developer configuration.

**Alternative considered:** SharedPreferences with settings UI. Over-engineered for development needs; requires UI implementation and makes debugging harder.

### Default to localhost for emulator

If `base.url` is not defined in `local.properties`, default to `http://10.0.2.2/appOrder/appGoiMon_API/` (emulator localhost mapping).

**Rationale:**
- Most common development setup (running XAMPP locally, testing on emulator)
- Graceful fallback prevents build errors
- Developers on physical devices can override easily

### Keep existing RetrofitClient structure

Only change the `BASE_URL` constant from hardcoded string to `BuildConfig.BASE_URL`. No other refactoring.

**Rationale:**
- Minimal change reduces risk
- Existing code using `RetrofitClient.apiService` continues working unchanged
- No impact on user or admin flows

## Risks / Trade-offs

- **New developers may not know to configure local.properties** → Add clear documentation in README with example configurations for common scenarios (emulator, local network, physical device)
- **BuildConfig requires rebuild when URL changes** → Acceptable for development workflow; runtime switching is non-goal
- **No validation of URL format** → Gradle build will succeed with invalid URLs; runtime API calls will fail with clear error messages, which is sufficient for development
- **If local.properties is deleted, app will build with default** → Default is sensible (emulator localhost); document fallback behavior

## Migration Plan

1. Update `app/build.gradle.kts` to read from `local.properties` and create BuildConfig field
2. Update `RetrofitClient.kt` to use `BuildConfig.BASE_URL`
3. Create/update `local.properties` with current hardcoded URL as example
4. Test: Clean build, verify `BuildConfig.BASE_URL` is correct, test API connection
5. Document configuration in README or project wiki

Rollback: Revert changes and restore hardcoded URL (one file change in RetrofitClient.kt).

## Open Questions

None. The approach is straightforward and well-understood in Android development.
