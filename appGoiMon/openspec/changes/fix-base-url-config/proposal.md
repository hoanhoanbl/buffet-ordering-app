## Why

The Android app has a hardcoded base URL (`http://192.168.1.56/appOrder/appGoiMon_API/`) in `RetrofitClient.kt`, which prevents the app from connecting to APIs on different networks or devices. This blocks development on different machines, testing on emulators (which require `10.0.2.2`), and future production deployment.

## What Changes

- Add configurable base URL support using `local.properties` and BuildConfig
- Update `RetrofitClient.kt` to read base URL from BuildConfig instead of hardcoded string
- Add build configuration to read base URL from `local.properties` with sensible default
- Document base URL configuration for other developers

## Capabilities

### New Capabilities
- `configurable-base-url`: Support for environment-specific API base URLs using local.properties and BuildConfig, allowing developers to configure their own API endpoints without code changes

### Modified Capabilities

None. This change does not modify existing user or admin flows - it only changes how the API base URL is configured.

## Impact

- Android app build configuration (`app/build.gradle.kts`)
- `RetrofitClient.kt` singleton
- `local.properties` file (not committed to git)
- No impact on PHP API
- No impact on user flow or admin flow
- No database changes
