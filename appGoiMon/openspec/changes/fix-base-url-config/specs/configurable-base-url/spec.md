## ADDED Requirements

### Requirement: Base URL is configurable per developer
The system SHALL allow developers to configure the API base URL without modifying committed source code.

#### Scenario: Developer configures custom base URL
- **WHEN** a developer adds `base.url=http://192.168.1.100/appOrder/appGoiMon_API/` to local.properties
- **THEN** the app SHALL build successfully and use that URL for all API calls

#### Scenario: Missing base URL configuration uses default
- **WHEN** local.properties does not contain a base.url property
- **THEN** the app SHALL build with the default URL `http://10.0.2.2/appOrder/appGoiMon_API/`

### Requirement: Base URL is available at compile time
The system SHALL expose the configured base URL as a BuildConfig constant accessible to the app code.

#### Scenario: RetrofitClient reads base URL from BuildConfig
- **WHEN** RetrofitClient initializes
- **THEN** it SHALL use BuildConfig.BASE_URL to configure the Retrofit base URL

### Requirement: Base URL configuration does not require code changes
The system SHALL keep base URL configuration separate from version-controlled code to prevent merge conflicts.

#### Scenario: Multiple developers use different URLs
- **WHEN** Developer A uses `http://192.168.1.56/appOrder/appGoiMon_API/` and Developer B uses `http://10.0.2.2/appOrder/appGoiMon_API/`
- **THEN** both SHALL be able to build and run the app without modifying each other's local.properties files
- **THEN** git status SHALL show no changes to committed files

### Requirement: Invalid base URL configuration provides clear feedback
The system SHALL fail API calls with clear error messages when an invalid base URL is configured.

#### Scenario: Malformed URL at runtime
- **WHEN** a developer configures an invalid URL (e.g., missing protocol or wrong port)
- **THEN** the app SHALL build successfully but Retrofit SHALL fail with a clear connection error during API calls
- **THEN** the error SHALL indicate the URL that was attempted
